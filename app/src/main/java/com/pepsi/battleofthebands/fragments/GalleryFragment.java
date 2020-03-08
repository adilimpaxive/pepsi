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

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.SpacesItemDecoration;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.GalleryAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.dialogs.ShowLargeImageDialog;
import com.pepsi.battleofthebands.entity.Gallery;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class GalleryFragment extends Fragment implements OnAlbumItemClickListener {
    private ArrayList<Gallery> galleries;
    GalleryAdapter galleryAdapter;
    RecyclerView recyclerViewAlbums;
    private ProgressBar progressBar;
    Context context;
    View rootView;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("BOB MOMENTS");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        setViewPadding();
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerViewAlbums = rootView.findViewById(R.id.recyclerViewGallery);
        recyclerViewAlbums.addItemDecoration(new SpacesItemDecoration(3, Utils.getDpiFromPixel(context, 10), true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewAlbums.setHasFixedSize(false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewAlbums.setLayoutManager(gridLayoutManager);
        recyclerViewAlbums.setNestedScrollingEnabled(false);
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        galleries = Utils.galleries;
        try {
            PepsiApplication.tracker.setScreenName("GalleryScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpViewsWithData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        galleryAdapter = new GalleryAdapter(context, galleries);
        galleryAdapter.setItemClickListener(this);
        recyclerViewAlbums.setAdapter(galleryAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onItemClicked(int position) {
        String imageLink = galleries.get(position).getSmall_image();
        // Create and show the dialog.
        ShowLargeImageDialog showLargeImageDialog = new ShowLargeImageDialog();
        Bundle b = new Bundle();
        b.putSerializable(ShowLargeImageDialog.KEY_IMAGELINK, imageLink);
        showLargeImageDialog.setArguments(b);
        showLargeImageDialog.show(((MainActivity) context).getSupportFragmentManager(), "showLargeImageDialog");
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
