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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.SocialMediaAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Settings;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class FollowUsFragment extends Fragment implements ResponseApi, OnAlbumItemClickListener {
    SocialMediaAdapter socialMediaAdapter;
    RecyclerView recyclerViewSocialMedia;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    ArrayList<Settings> settings;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static FollowUsFragment newInstance() {
        return new FollowUsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("FOLLOW US");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
        rootView = inflater.inflate(R.layout.fragment_follow, container, false);
        setViewPadding();
        try {
            PepsiApplication.tracker.setScreenName("FollowUsScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeViews();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }
    private void initializeViews() {
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerViewSocialMedia = rootView.findViewById(R.id.recyclerViewSocialMedia);
        recyclerViewSocialMedia.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSocialMedia.setHasFixedSize(true);

        if (Prefs.getSettingsResponse(context).isEmpty()) {
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.setShowProgress(true);
            okHttpApi.showProgressDialogWithTitle("Loading...", context);
            okHttpApi.callGetRequest(URLManager.GET_SETTINGS);
        } else {
            Gson gson = new Gson();
            settings = gson.fromJson(Prefs.getSettingsResponse(context), new TypeToken<ArrayList<Settings>>() {
            }.getType());
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        socialMediaAdapter = new SocialMediaAdapter(context, settings.get(0).getSocialmedia());
        socialMediaAdapter.setItemClickListener(this);
        recyclerViewSocialMedia.setAdapter(socialMediaAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.get(0).getSocialmedia().get(position).getLink()));
        startActivity(intent);
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

    @Override
    public void onNetworkNotAvailable() {
        DialogHelper.showDialogError(context, context.getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            Prefs.setSettingsResponse(response, context);
            Gson gson = new Gson();
            settings = gson.fromJson(response, new TypeToken<ArrayList<Settings>>() {
            }.getType());
            Prefs.saveInt(context, Prefs.KEY_THEME, settings.get(0).getTheme());
            setUpViewsWithData();
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

    private void setViewPadding() {
        if (((MainActivity) context).showMiniPlayer(true)) {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, 0);
        }
    }
}