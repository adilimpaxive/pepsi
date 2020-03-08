package com.pepsi.battleofthebands.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.ScheduleAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.ScheduleTime;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment implements ResponseApi {
    public static final String TAG_MINI_PLAYER = "TAG_MINI_PLAYER";
    View rootView;
    RecyclerView recyclerViewSchedule;
    private ArrayList<ScheduleTime> scheduleTimes;
    public ScheduleAdapter scheduleAdapter;
    private ProgressBar progressBar;
    private String season;
    MainActivity activity;
    Context context;
    TextView textViewComingSoon;
    TextView textViewTitle;

    public static ScheduleFragment newInstance(String season) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString("season", season);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        Bundle bundle = getArguments();
        season = bundle.getString("season");

        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("SCHEDULE");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        activity = (MainActivity) context;
        if (scheduleTimes == null)
            scheduleTimes = new ArrayList<>();
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        setViewPadding();
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerViewSchedule = rootView.findViewById(R.id.recyclerViewSchedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSchedule.setHasFixedSize(true);
        textViewTitle = rootView.findViewById(R.id.textViewTitle);
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            textViewTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.download_background_color));
        }
        textViewComingSoon = rootView.findViewById(R.id.textViewComingSoon);

        loadFromInternet();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    private void loadFromInternet() {
        if (scheduleTimes.size() == 0) {
            try {
                PepsiApplication.tracker.setScreenName("ScheduleScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.GET_SEASONS_DATA + season + "/schedule");
        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        scheduleAdapter = new ScheduleAdapter(context, scheduleTimes);
        recyclerViewSchedule.setAdapter(scheduleAdapter);
        textViewTitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkNotAvailable() {
        progressBar.setVisibility(View.INVISIBLE);
        DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                Gson gson = new Gson();
                scheduleTimes = gson.fromJson(response, new TypeToken<ArrayList<ScheduleTime>>() {
                }.getType());
                if (scheduleTimes.size() > 0) {
                    setUpViewsWithData();
                } else {
                    textViewComingSoon.setVisibility(View.VISIBLE);
                }
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
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, 0);
        }
    }
}