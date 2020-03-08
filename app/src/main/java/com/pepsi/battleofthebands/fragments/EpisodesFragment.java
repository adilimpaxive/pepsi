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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.EpisodesAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Episode;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class EpisodesFragment extends Fragment implements ResponseApi, OnAlbumItemClickListener, View.OnClickListener {
    public static String youtubeAPI = "AIzaSyAB-PQo_hMjG9bhL8oLXnvbG4wx1bRVZ7w";
    public static final String TAG_EPISODE = "TAG_EPISODE";
    private ArrayList<Episode> episodes;
    EpisodesAdapter episodesAdapter;
    RecyclerView recyclerViewEpisodes;
    private String season;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    int selectedPosition = 0;
    LinearLayout layoutPlayButton;
    ImageView playImageView, forwardImageView, rewindImageView;
    TextView textViewComingSoon;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static EpisodesFragment newInstance(String season) {
        EpisodesFragment fragment = new EpisodesFragment();
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
            ((MainActivity) context).textViewTitle.setText("EPISODES");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.episodes_background_theme);
        }
        rootView = inflater.inflate(R.layout.fragment_episodes, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        setViewPadding();
        recyclerViewEpisodes = rootView.findViewById(R.id.recyclerViewEpisode);
        recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewEpisodes.setHasFixedSize(true);

        rewindImageView = rootView.findViewById(R.id.rewindImageView);
        playImageView = rootView.findViewById(R.id.playImageView);
        forwardImageView = rootView.findViewById(R.id.forwardImageView);

        rewindImageView.setOnClickListener(this);
        playImageView.setOnClickListener(this);
        forwardImageView.setOnClickListener(this);

        textViewComingSoon = rootView.findViewById(R.id.textViewComingSoon);

        LinearLayout.LayoutParams params;
        layoutPlayButton = rootView.findViewById(R.id.layoutPlayButton);
        params = new LinearLayout.LayoutParams(WRAP_CONTENT, Utils.getDpiFromPixel(context, 56));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16));
        layoutPlayButton.setLayoutParams(params);

        loadFromInternet();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        context.registerReceiver(br_updatePlayerUI, new IntentFilter(TAG_EPISODE));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
        try {
            context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", episodes.get(selectedPosition).getLink()).putExtra("isPlay", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromInternet() {
        if (episodes == null) {
            try {
                PepsiApplication.tracker.setScreenName("EpisodesScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.GET_SEASONS_DATA + season + "/episodes");

        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        layoutPlayButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        episodesAdapter = new EpisodesAdapter(context, episodes, selectedPosition);
        episodesAdapter.setItemClickListener(this);
        recyclerViewEpisodes.setAdapter(episodesAdapter);
        context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", episodes.get(selectedPosition).getLink()).putExtra("isPlay", false));
        playImageView.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
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
                episodes = gson.fromJson(response, new TypeToken<ArrayList<Episode>>() {
                }.getType());
                if (episodes.size() > 0) {
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
        selectedPosition = position;
        context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", episodes.get(position).getLink()).putExtra("isPlay", true));
        episodesAdapter.notifyDataSetChanged();
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
                if (episodes.size() > (selectedPosition + 1)) {
                    selectedPosition = selectedPosition + 1;
                } else {
                    selectedPosition = 0;
                }
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", episodes.get(selectedPosition).getLink()).putExtra("isPlay", true));
                episodesAdapter.setNewIndex(selectedPosition);
                recyclerViewEpisodes.smoothScrollToPosition(selectedPosition);
                break;
            case R.id.rewindImageView:
                if (episodes.size() > 0 && selectedPosition > 0) {
                    selectedPosition = selectedPosition - 1;
                    // Play the first song in the list
                } else {
                    selectedPosition = 0;
                }
                context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", true).putExtra("videoName", episodes.get(selectedPosition).getLink()).putExtra("isPlay", true));
                episodesAdapter.setNewIndex(selectedPosition);
                recyclerViewEpisodes.smoothScrollToPosition(selectedPosition);
                break;
        }
    }
}
