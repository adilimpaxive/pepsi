package com.pepsi.battleofthebands.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.PerformanceAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.Episode;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class PerformanceFragment extends Fragment implements OnAlbumItemClickListener, View.OnClickListener {
    public static final String TAG_PERFORMANCE = "TAG_PERFORMANCE";
    private ArrayList<Song> songs;
    private  ArrayList<Episode> episodes;
    PerformanceAdapter performanceAdapter;
    RecyclerView recyclerViewPerformance;
    private String title;
    Context context;
    View rootView;
    int selectedPosition = 0;
    LinearLayout layoutPlayButton;
    ImageView playImageView, forwardImageView, rewindImageView;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PerformanceFragment newInstance(String title) {
        PerformanceFragment fragment = new PerformanceFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText(title);
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.episodes_background_theme);
        }
        rootView = inflater.inflate(R.layout.fragment_performance, container, false);
        setViewPadding();
        recyclerViewPerformance = rootView.findViewById(R.id.recyclerViewPerformance);
        recyclerViewPerformance.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewPerformance.setHasFixedSize(true);

        rewindImageView = rootView.findViewById(R.id.rewindImageView);
        playImageView = rootView.findViewById(R.id.playImageView);
        forwardImageView = rootView.findViewById(R.id.forwardImageView);

        rewindImageView.setOnClickListener(this);
        playImageView.setOnClickListener(this);
        forwardImageView.setOnClickListener(this);

        LinearLayout.LayoutParams params;
        layoutPlayButton = rootView.findViewById(R.id.layoutPlayButton);
        params = new LinearLayout.LayoutParams(WRAP_CONTENT, Utils.getDpiFromPixel(context, 56));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16));
        layoutPlayButton.setLayoutParams(params);

        context.registerReceiver(br_updatePlayerUI, new IntentFilter(TAG_PERFORMANCE));
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        songs = Utils.selectedPerformance;
        try {
            PepsiApplication.tracker.setScreenName("PerformanceScreen");
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
        layoutPlayButton.setVisibility(View.VISIBLE);
        performanceAdapter = new PerformanceAdapter(context, songs,selectedPosition);
        performanceAdapter.setItemClickListener(this);
        recyclerViewPerformance.setAdapter(performanceAdapter);
        if (songs.size() > 0)
            context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", songs.get(selectedPosition).getVideo_code()).putExtra("isPlay", false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
        if (br_updatePlayerUI != null)
            context.unregisterReceiver(br_updatePlayerUI);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onItemClicked(int position) {
        selectedPosition = position;
        context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", songs.get(position).getVideo_code()).putExtra("isPlay", true));
        performanceAdapter.notifyDataSetChanged();
        performanceAdapter.setNewIndex(selectedPosition);
    }

    public BroadcastReceiver br_updateMiniPlayer = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewPadding();
        }
    };
    public BroadcastReceiver br_updatePlayerUI = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                if (intent.hasExtra("updateUI")) {
                    if (intent.getExtras().getBoolean("updateUI")) {
                        playImageView.setImageResource(R.mipmap.ic_pause_black_48dp);
                    } else {
                        playImageView.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
                    }
                }
            }
        }
    };

    private void setViewPadding() {
        if (((MainActivity) context).showMiniPlayer(true)) {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playImageView:
                // play/pause functionality
                if (((MainActivity) context).youTubePlayer != null) {
                    if (((MainActivity) context).youTubePlayer.isPlaying()) {
                        playImageView.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
                        ((MainActivity) context).youTubePlayer.pause();
                    } else {
                        playImageView.setImageResource(R.mipmap.ic_pause_black_48dp);
                        ((MainActivity) context).youTubePlayer.play();
                    }
                }
                break;
            case R.id.forwardImageView:
                if (songs.size() > (selectedPosition + 1)) {
                    selectedPosition = selectedPosition + 1;
                } else {
                    selectedPosition = 0;
                }
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", songs.get(selectedPosition).getVideo_code()).putExtra("isPlay", true));
                performanceAdapter.setNewIndex(selectedPosition);
                recyclerViewPerformance.smoothScrollToPosition(selectedPosition);
                break;
            case R.id.rewindImageView:
                if (songs.size() > 0 && selectedPosition > 0) {
                    selectedPosition = selectedPosition - 1;
                    // Play the first song in the list
                } else {
                    selectedPosition = 0;
                }
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", songs.get(selectedPosition).getVideo_code()).putExtra("isPlay", true));
                performanceAdapter.setNewIndex(selectedPosition);
                recyclerViewPerformance.smoothScrollToPosition(selectedPosition);
                break;
        }
    }
}
