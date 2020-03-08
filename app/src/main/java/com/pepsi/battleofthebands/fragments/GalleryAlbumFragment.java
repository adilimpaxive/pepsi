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
import android.support.v7.widget.GridLayoutManager;
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
import com.pepsi.battleofthebands.SpacesItemDecoration;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.GalleryAlbumAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.GalleryAlbum;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class GalleryAlbumFragment extends Fragment implements ResponseApi, OnAlbumItemClickListener {
    private ArrayList<GalleryAlbum> galleryAlbums;
    GalleryAlbumAdapter galleryAlbumAdapter;
    RecyclerView recyclerViewAlbums;
    private String season;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    TextView textViewComingSoon;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static GalleryAlbumFragment newInstance(String season) {
        GalleryAlbumFragment fragment = new GalleryAlbumFragment();
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
            ((MainActivity) context).textViewTitle.setText("BoB MOMENTS");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        rootView = inflater.inflate(R.layout.fragment_album_gallery, container, false);
        setViewPadding();
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerViewAlbums = rootView.findViewById(R.id.recyclerViewGallery);
        recyclerViewAlbums.addItemDecoration(new SpacesItemDecoration(2, Utils.getDpiFromPixel(context, 10), true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewAlbums.setHasFixedSize(false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewAlbums.setLayoutManager(gridLayoutManager);
        recyclerViewAlbums.setNestedScrollingEnabled(false);
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
        if (galleryAlbums == null) {
            try {
                PepsiApplication.tracker.setScreenName("GalleryAlbumScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.GET_SEASONS_DATA + season + "/gallery");
        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        galleryAlbumAdapter = new GalleryAlbumAdapter(context, galleryAlbums);
        galleryAlbumAdapter.setItemClickListener(this);
        recyclerViewAlbums.setAdapter(galleryAlbumAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
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
                galleryAlbums = gson.fromJson(response, new TypeToken<ArrayList<GalleryAlbum>>() {
                }.getType());
                if (galleryAlbums.size() > 0) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onItemClicked(int position) {
        Utils.setSelectedGallery(galleryAlbums.get(position).getGallery());
        ((MainActivity) context).showGalleryFragment();
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
