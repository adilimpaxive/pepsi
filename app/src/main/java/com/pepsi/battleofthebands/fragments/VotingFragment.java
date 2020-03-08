package com.pepsi.battleofthebands.fragments;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.AppDialog;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Voting;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class VotingFragment extends Fragment implements ResponseApi {
    private Voting voting;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    private Dialog mProgressDialog;
    RelativeLayout layoutBandOne, layoutBandTwo;
    ImageView imageViewVs, imageViewBandOne, imageViewBandTwo;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static VotingFragment newInstance() {
        return new VotingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("VOTE");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
        mProgressDialog = AppDialog.getProgressDialog(context);
        rootView = inflater.inflate(R.layout.fragment_voting, container, false);
        int margin = Utils.getDpiFromPixel(context, 16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 180));
        layoutParams.setMargins(margin, 0, margin, 0);
        layoutBandOne = rootView.findViewById(R.id.layoutBandOne);
        layoutBandOne.setLayoutParams(layoutParams);
        layoutBandTwo = rootView.findViewById(R.id.layoutBandTwo);
        layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 180));
        layoutParams.setMargins(margin, 0, margin, 0);
        layoutBandTwo.setLayoutParams(layoutParams);
        imageViewVs = rootView.findViewById(R.id.imageViewAnd);
        imageViewBandOne = rootView.findViewById(R.id.imageViewBandOne);
        imageViewBandTwo = rootView.findViewById(R.id.imageViewBandTwo);
        imageViewBandOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voting.getVote() == null) {
                    for (int i = 0; i < voting.getBands().size(); i++) {
                        if (voting.getBands().get(i).getName().toLowerCase().contains("xarb") || voting.getBands().get(i).getName().toLowerCase().equalsIgnoreCase("xarb") || voting.getBands().get(i).getName().toLowerCase().startsWith("xarb")) {
                            voteCast(voting.getBands().get(i).getId());
                        }
                    }
                } else {
                    Toast.makeText(context, "You have already voted for " + voting.getVote().getBand().getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageViewBandTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < voting.getBands().size(); i++) {
                    if (voting.getBands().get(i).getName().toLowerCase().contains("bayaan") || voting.getBands().get(i).getName().toLowerCase().equalsIgnoreCase("bayaan") || voting.getBands().get(i).getName().toLowerCase().startsWith("bayaan")) {
                        voteCast(voting.getBands().get(i).getId());
                    }
                }
            }
        });
        setViewPadding();
        progressBar = rootView.findViewById(R.id.progressBar);
        loadFromInternet();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    private void voteCast(String id) {
        if (Prefs.getInt(context, Prefs.KEY_VOTING, 0) == 0) {
            Toast.makeText(context, "Voting lines open SATURDAY 25th August,12PM", Toast.LENGTH_SHORT).show();
        } else {
            RequestBody formBody = new FormBody.Builder()
                    .add("band_id", id)
                    .build();
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callPostRequestWithToken(URLManager.GET_VOTING_URL, formBody);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    private void loadFromInternet() {
        if (voting == null) {
            try {
                PepsiApplication.tracker.setScreenName("VotingScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest();
        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        if (voting.getVote() != null) {
            imageViewVs.setVisibility(View.GONE);
            layoutBandOne.setVisibility(View.VISIBLE);
            layoutBandTwo.setVisibility(View.GONE);
            if (voting.getVote().getBand().getName().toLowerCase().contains("xarb") || voting.getVote().getBand().getName().toLowerCase().equalsIgnoreCase("xarb") || voting.getVote().getBand().getName().toLowerCase().startsWith("xarb")) {
                imageViewBandOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vote_xarb));
            } else if (voting.getVote().getBand().getName().toLowerCase().contains("Bayaan") || voting.getVote().getBand().getName().toLowerCase().equalsIgnoreCase("bayaan") || voting.getVote().getBand().getName().toLowerCase().startsWith("bayaan")) {
                imageViewBandOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vote_byan));
            }
            layoutBandOne.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border_selected));
        } else if (voting.getBands() != null && voting.getBands().size() > 0) {
            imageViewVs.setVisibility(View.VISIBLE);
            layoutBandOne.setVisibility(View.VISIBLE);
            layoutBandTwo.setVisibility(View.VISIBLE);
            imageViewBandOne.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vote_xarb));
            imageViewBandTwo.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.vote_byan));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkNotAvailable() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        progressBar.setVisibility(View.INVISIBLE);
        DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            progressBar.setVisibility(View.INVISIBLE);
            if (response.contains("message")) {
                voting = null;
                loadFromInternet();
            } else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                try {
                    Gson gson = new Gson();
                    voting = gson.fromJson(response, new TypeToken<Voting>() {
                    }.getType());
                    setUpViewsWithData();

                } catch (Exception e) {
                    DialogHelper.showDialogError(context, "Something went wrong.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String response, String webServiceName) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCrash(String crashMsg) {
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
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, 0);
        }
    }
}