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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.SpacesItemDecoration;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.JudgesAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Judges;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class JudgesFragment extends Fragment implements ResponseApi, OnAlbumItemClickListener {
    private ArrayList<Judges> judges;
    JudgesAdapter artistAlbumAdapter;
    RecyclerView recyclerViewAlbums;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    TextView textViewComingSoon;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static JudgesFragment newInstance() {
        return new JudgesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("JUDGES");
            context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", false));
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        rootView = inflater.inflate(R.layout.fragment_judges, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        setViewPadding();
        textViewComingSoon = rootView.findViewById(R.id.textViewComingSoon);
        recyclerViewAlbums = rootView.findViewById(R.id.recyclerViewJudges);
        recyclerViewAlbums.addItemDecoration(new SpacesItemDecoration(2, Utils.getDpiFromPixel(context, 10), true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewAlbums.setHasFixedSize(false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewAlbums.setLayoutManager(gridLayoutManager);
        recyclerViewAlbums.setNestedScrollingEnabled(false);
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
        if (judges == null) {
            try {
                PepsiApplication.tracker.setScreenName("JudgesScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.BANDS_JUDGES_URL);
        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        artistAlbumAdapter = new JudgesAdapter(context, judges);
        artistAlbumAdapter.setItemClickListener(this);
        recyclerViewAlbums.setAdapter(artistAlbumAdapter);
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
                judges = gson.fromJson(response, new TypeToken<ArrayList<Judges>>() {
                }.getType());
                Log.d("judjes",response);



                if (judges.size() > 0) {
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
        Utils.setSelectedBand(judges.get(position).getSongs());
        ((MainActivity) context).showJudgeDetailFragment(judges.get(position));
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
