package com.pepsi.battleofthebands.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.SeasonAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Seasons;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class SeasonFragment extends Fragment implements ResponseApi, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ArrayList<Seasons> seasons;
    SeasonAdapter adapter;
    Spinner spinner;
    int selectedSeasonPosition = 0;
    RelativeLayout layoutTop8, layoutEpisodes, layoutSchedule, layoutGallery, layoutNews, layoutFollowUs, layoutJudge, layoutSong;
    TextView textViewTop8, textViewEpisodes, textViewSchedule, textViewGallery, textViewBobBuzz, textViewFollowUs, textViewJudge, textViewSong;
    // header list view
    Context context;
    View rootView;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static SeasonFragment newInstance() {
        return new SeasonFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();




        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("SEASONS");
            context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", false));
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_season);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
        rootView = inflater.inflate(R.layout.fragment_season, container, false);
        setViewPadding();
        initializeViews();
        loadAlbumFromInternet();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;




    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    private void initializeViews() {
        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 38));
        int padding16 = Utils.getDpiFromPixel(context, 16);
        int padding8 = Utils.getDpiFromPixel(context, 3);

        RelativeLayout layoutSpinner = rootView.findViewById(R.id.layoutSpinner);

        spinner = rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 38));
        params.setMargins(padding16, 0, padding16, 0);
        layoutSpinner.setLayoutParams(params);

        RelativeLayout layoutDropDown = rootView.findViewById(R.id.layoutDropDown);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 38), Utils.getDpiFromPixel(context, 38));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutDropDown.setLayoutParams(layoutParams);

        ImageView imageViewEpisode = rootView.findViewById(R.id.imageViewEpisodes);
        ImageView imageViewTop8 = rootView.findViewById(R.id.imageViewTop8);
        ImageView imageViewSchedule = rootView.findViewById(R.id.imageViewSchedule);
        ImageView imageViewGallery = rootView.findViewById(R.id.imageViewGallery);
        ImageView imageViewNews = rootView.findViewById(R.id.imageViewNews);
        ImageView imageViewFollowUs = rootView.findViewById(R.id.imageViewFollowUs);
        ImageView imageViewJudges = rootView.findViewById(R.id.imageViewJudges);
        ImageView imageViewSong = rootView.findViewById(R.id.imageViewSeasonSong);

        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (imageHeight / 2));
        paramsImage.setMargins(padding16, (padding8 * 2), padding8, 0);

        imageViewEpisode.setLayoutParams(paramsImage);
        imageViewSchedule.setLayoutParams(paramsImage);
        imageViewNews.setLayoutParams(paramsImage);
        imageViewJudges.setLayoutParams(paramsImage);

        paramsImage = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (imageHeight / 2));
        paramsImage.setMargins(padding8, (padding8 * 2), padding16, 0);

        imageViewTop8.setLayoutParams(paramsImage);
        imageViewGallery.setLayoutParams(paramsImage);
        imageViewFollowUs.setLayoutParams(paramsImage);
        imageViewSong.setLayoutParams(paramsImage);

        layoutTop8 = rootView.findViewById(R.id.layoutTop8);
        layoutEpisodes = rootView.findViewById(R.id.layoutEpisodes);
        layoutSchedule = rootView.findViewById(R.id.layoutSchedule);
        layoutGallery = rootView.findViewById(R.id.layoutGallery);
        layoutNews = rootView.findViewById(R.id.layoutNews);
        layoutFollowUs = rootView.findViewById(R.id.layoutFollowUs);
        layoutJudge = rootView.findViewById(R.id.layoutJudges);
        layoutSong = rootView.findViewById(R.id.layoutSong);

        layoutTop8.setOnClickListener(this);
        layoutEpisodes.setOnClickListener(this);
        layoutSchedule.setOnClickListener(this);
        layoutGallery.setOnClickListener(this);
        layoutNews.setOnClickListener(this);
        layoutFollowUs.setOnClickListener(this);
        layoutJudge.setOnClickListener(this);
        layoutSong.setOnClickListener(this);

        textViewEpisodes = rootView.findViewById(R.id.textViewEpisodes);
        textViewSchedule = rootView.findViewById(R.id.textViewSchedule);
        textViewBobBuzz = rootView.findViewById(R.id.textViewBobBuzz);

        textViewTop8 = rootView.findViewById(R.id.textViewTop8);
        textViewGallery = rootView.findViewById(R.id.textViewGallery);
        textViewFollowUs = rootView.findViewById(R.id.textViewFollowUs);
        textViewJudge = rootView.findViewById(R.id.textViewJudges);
        textViewSong = rootView.findViewById(R.id.textViewSong);

        textViewEpisodes.setPadding(padding16, (padding8 * 2), padding8, padding8 * 2);
        textViewSchedule.setPadding(padding16, (padding8 * 2), padding8, padding8 * 2);
        textViewBobBuzz.setPadding(padding16, (padding8 * 2), padding8, padding8 * 2);
        textViewJudge.setPadding(padding16, (padding8 * 2), padding8, padding8 * 2);

        textViewTop8.setPadding(padding8, (padding8 * 2), padding16, padding8 * 2);
        textViewGallery.setPadding(padding8, (padding8 * 2), padding16, padding8 * 2);
        textViewFollowUs.setPadding(padding8, (padding8 * 2), padding16, padding8 * 2);
        textViewSong.setPadding(padding8, (padding8 * 2), padding16, padding8 * 2);

        textViewTop8.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewEpisodes.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewSchedule.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewGallery.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewBobBuzz.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewFollowUs.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewJudge.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewSong.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            imageViewEpisode.setImageResource(R.mipmap.episodes);
            imageViewTop8.setImageResource(R.mipmap.top8);
            imageViewSchedule.setImageResource(R.mipmap.schedule);
            imageViewGallery.setImageResource(R.mipmap.bob_moments);
            imageViewNews.setImageResource(R.mipmap.news);
            imageViewFollowUs.setImageResource(R.mipmap.follow_us);

            textViewTop8.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewEpisodes.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewSchedule.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewGallery.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewBobBuzz.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewFollowUs.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewJudge.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewSong.setTextColor(ContextCompat.getColor(context, R.color.white));
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_home);
            ((MainActivity) context).layoutToolbarHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        }
    }

    private void loadAlbumFromInternet() {
        if (seasons == null) {
            try {
                PepsiApplication.tracker.setScreenName("SeasonScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.setShowProgress(true);
            okHttpApi.showProgressDialogWithTitle("Loading...", context);
            okHttpApi.callGetRequest(URLManager.GET_SEASONS);



        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        adapter = new SeasonAdapter(context, R.layout.spinner_view_selected, seasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutTop8:
                if (seasons != null) {
                    ((MainActivity) context).showBandFragment(seasons.get(selectedSeasonPosition).getId());
                } else {
                    ((MainActivity) context).showBandFragment("1");
                }
                break;
            case R.id.layoutEpisodes:
                if (seasons != null) {
                    ((MainActivity) context).showEpisodeFragment(seasons.get(selectedSeasonPosition).getId());
                } else {
                    ((MainActivity) context).showBandFragment("1");
                }
                break;
            case R.id.layoutSchedule:
                if (seasons != null) {
                    if (seasons.get(selectedSeasonPosition).getName().contains("2017")||seasons.get(selectedSeasonPosition).getName().contains("2018")) {
                        Toast.makeText(context, "Show already aired on tv", Toast.LENGTH_LONG).show();
                        String mseasonName=seasons.get(selectedSeasonPosition).getName();
                        new Intent().putExtra(mseasonName,0);

                    } else {
                        ((MainActivity) context).showScheduleFragment(seasons.get(selectedSeasonPosition).getId());

                    }
                } else {
                    ((MainActivity) context).showBandFragment("1");
                }
                break;
            case R.id.layoutGallery:
                if (seasons != null) {
                    ((MainActivity) context).showGalleryAlbumFragment(seasons.get(selectedSeasonPosition).getId());
                } else {
                    ((MainActivity) context).showBandFragment("1");
                }
                break;
            case R.id.layoutNews:
                if (seasons != null) {
                    ((MainActivity) context).showNewsFragment(seasons.get(selectedSeasonPosition).getId());
                } else {
                    ((MainActivity) context).showBandFragment("1");
                }
                break;
            case R.id.layoutFollowUs:
                ((MainActivity) context).showFollowUsFragment();
                break;
            case R.id.layoutJudges:
                ((MainActivity) context).showFollowUsFragment();
                break;
            case R.id.layoutSong:
                ((MainActivity) context).showFollowUsFragment();
                break;
        }
    }

    @Override
    public void onNetworkNotAvailable() {
        Toast.makeText(context, getString(R.string.internet_required_alert), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            try {
                Gson gson = new Gson();
                seasons = gson.fromJson(response, new TypeToken<ArrayList<Seasons>>() {
                }.getType());
                setUpViewsWithData();
            } catch (Exception e) {
                DialogHelper.showDialogError(context, "Something went wrong.");
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
    }

    public BroadcastReceiver br_updateMiniPlayer = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewPadding();
        }
    };

    private void setViewPadding() {
        if (((MainActivity) context).showMiniPlayer(true)) {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 28), 0, 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSeasonPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}


