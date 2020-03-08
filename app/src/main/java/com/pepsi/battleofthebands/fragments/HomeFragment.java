package com.pepsi.battleofthebands.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Settings;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.networkcalls.WebServices;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class HomeFragment extends Fragment implements ResponseApi, View.OnClickListener {
    ArrayList<Settings> settings;
    Context context;
    View rootView;
    RelativeLayout layoutSong, layoutSeasons, layoutJudges, layoutTvs, layoutVoteNow;
    LinearLayout layoutSeasonsAndJudges, layoutVoteNowAndTVS;
    TextView textViewTitle, textViewSong, textViewEpisodes, textViewJudges, textViewTvs, textViewVoteNow;
    String urlTVS = "";
    String votingmesage,bobanthem;
    boolean playAfterLoading = false;
    String url=URLManager.GET_SETTINGS;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
        ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
        ((MainActivity) context).textViewTitle.setText("");
        playAfterLoading = false;
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setViewPadding();
        try {
            PepsiApplication.tracker.setScreenName("HomeScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
        okHttpApi.callGetRequest(URLManager.GET_SETTINGS);
        initUi();
        loadSpinnerData(url);
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", false));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void initUi() {
        int padding16 = Utils.getDpiFromPixel(context, 16);
        int padding08 = Utils.getDpiFromPixel(context, 4);

        layoutSong = rootView.findViewById(R.id.layoutSong);
        layoutSeasons = rootView.findViewById(R.id.layoutSeasons);
        layoutJudges = rootView.findViewById(R.id.layoutJudges);
        layoutTvs = rootView.findViewById(R.id.layoutTvs);
        layoutVoteNow = rootView.findViewById(R.id.layoutVoteNow);

        layoutSeasonsAndJudges = rootView.findViewById(R.id.layoutSeasonsAndJudges);
        layoutVoteNowAndTVS = rootView.findViewById(R.id.layoutVoteNowAndTVS);

        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 40));
        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(MATCH_PARENT, imageHeight / 2);
        paramsImage.setMargins(padding16, 0, padding16, 0);
        layoutSong.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(MATCH_PARENT, (int) (imageHeight / 2.3));
        layoutVoteNowAndTVS.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(MATCH_PARENT, (int) (imageHeight / 2.3));
        paramsImage.setMargins(0, Utils.getDpiFromPixel(context, 8), 0, Utils.getDpiFromPixel(context, 8));
        layoutSeasonsAndJudges.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(imageHeight / 2, MATCH_PARENT);
        paramsImage.setMargins(0, 0, padding08, 0);
        layoutSeasons.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(imageHeight / 2, MATCH_PARENT);
        paramsImage.setMargins(padding08, 0, 0, 0);
        layoutJudges.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(imageHeight / 2, MATCH_PARENT);
        paramsImage.setMargins(0, 0, padding08, 0);
        layoutVoteNow.setLayoutParams(paramsImage);

        paramsImage = new LinearLayout.LayoutParams(imageHeight / 2, MATCH_PARENT);
        paramsImage.setMargins(padding08, 0, 0, 0);
        layoutTvs.setLayoutParams(paramsImage);

        textViewTitle = rootView.findViewById(R.id.textViewTitle);
        textViewSong = rootView.findViewById(R.id.textViewSong);
        textViewEpisodes = rootView.findViewById(R.id.textViewEpisodes);
        textViewJudges = rootView.findViewById(R.id.textViewJudges);
        textViewTvs = rootView.findViewById(R.id.textViewTvs);
        textViewVoteNow = rootView.findViewById(R.id.textViewVoteNow);

        textViewTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewSong.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewEpisodes.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewJudges.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewTvs.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewVoteNow.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        layoutSong.setOnClickListener(this);
        layoutSeasons.setOnClickListener(this);
        layoutJudges.setOnClickListener(this);
        layoutTvs.setOnClickListener(this);
        layoutVoteNow.setOnClickListener(this);
        textViewTitle.setOnClickListener(this);

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
//            textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewSong.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewEpisodes.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewJudges.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewTvs.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewVoteNow.setTextColor(ContextCompat.getColor(context, R.color.white));
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_home);
            ((MainActivity) context).layoutToolbarHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_home_theme);
        }
        try {
            if (!Prefs.getSettingsResponse(context).isEmpty()) {
                Gson gson = new Gson();
                settings = gson.fromJson(Prefs.getSettingsResponse(context), new TypeToken<ArrayList<Settings>>() {
                }.getType());
                textViewTitle.setText(settings.get(0).getMain_page_text());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkNotAvailable() {
        DialogHelper.showDialogError(context, HomeFragment.this.getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            Prefs.setSettingsResponse(response, context);
            Gson gson = new Gson();
            settings = gson.fromJson(response, new TypeToken<ArrayList<Settings>>() {
            }.getType());
            urlTVS = settings.get(0).getTvc_link();
            Prefs.saveInt(context, Prefs.KEY_THEME, settings.get(0).getTheme());
            Prefs.saveInt(context, Prefs.KEY_VOTING, settings.get(0).getVoting());
            textViewTitle.setText(settings.get(0).getMain_page_text());
            if (playAfterLoading) {
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("playOfficialVideo", false).putExtra("videoName", urlTVS).putExtra("isPlay", false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String type, String webServiceName) {

    }

    @Override
    public void onCrash(String crashMsg) {
    }

    @Override
    public void onDestroyView() {
        WebServices.getInstance(context).cancelCurrentRequest();
        DialogHelper.dismissFullScreenProgressDialog();
        DialogHelper.dismissProgressDialog();
        System.gc();
        try {
            if (br_updateMiniPlayer != null) {
                getActivity().unregisterReceiver(br_updateMiniPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    public BroadcastReceiver br_updateMiniPlayer = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewPadding();
        }
    };

    private void setViewPadding() {
        if (((MainActivity) context).showMiniPlayer(true)) {
            rootView.setPadding(0, 0, 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, 0, 0, 0);
        }
    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        votingmesage=jsonobject.getString("voting_message");

                                bobanthem=jsonobject.getString("tvc_description");
                                new Intent().putExtra("anthem",bobanthem);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutSong:
                ((MainActivity) context).showAlbumFragment();
                break;
            case R.id.layoutSeasons:
                ((MainActivity) context).showSeasonFragment("seasons");
                break;
            case R.id.layoutJudges:
                ((MainActivity) context).showJudgesFragment();
                break;
            case R.id.layoutTvs:
                if (Prefs.getSettingsResponse(context).isEmpty()) {
                    playAfterLoading = true;
                    OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
                    okHttpApi.setShowProgress(true);
                    okHttpApi.showProgressDialogWithTitle("Loading...", context);
                    okHttpApi.callGetRequest(URLManager.GET_SETTINGS);
                    return;
                } else if (urlTVS.isEmpty()) {
                    Gson gson = new Gson();
                    settings = gson.fromJson(Prefs.getSettingsResponse(context), new TypeToken<ArrayList<Settings>>() {
                    }.getType());
                    urlTVS = settings.get(0).getTvc_link();
                }
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("playOfficialVideo", false).putExtra("videoName", urlTVS));
                break;
            case R.id.layoutVoteNow:
                if (Prefs.getInt(context, Prefs.KEY_VOTING, 0) == 0) {

                    Toast.makeText(context, votingmesage, Toast.LENGTH_SHORT).show();
                } else {
                    if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
                        ((MainActivity) context).showVotingFragment();
                    } else {
                        DialogHelper.showSignUpAlert(context, context.getResources().getString(R.string.login_error));
                    }
                }
                break;
            case R.id.textViewTitle:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.get(0).getMain_page_link()));
                startActivity(intent);
                break;
        }
    }
}

