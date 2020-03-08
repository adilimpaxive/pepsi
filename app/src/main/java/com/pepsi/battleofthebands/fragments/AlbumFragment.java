package com.pepsi.battleofthebands.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.AlbumAdapter;
import com.pepsi.battleofthebands.adapter.SeasonAdapter;
import com.pepsi.battleofthebands.app.PatariSingleton;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.callback.OnMusicUpdateListener;
import com.pepsi.battleofthebands.db.QueueTableDataManager;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Seasons;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.services.MusicService;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.SONGS_QUEUE_TABLE;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.season_id;

public class AlbumFragment extends Fragment implements View.OnClickListener, ResponseApi, OnAlbumItemClickListener, OnMusicUpdateListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
    public static final String PLAYING = "Playing";
    public static final String TAG_ALBUM = "TAG_ALBUM";
    public static final String PAUSE = "Pause";
    public static final String TAG_LOADING = "TAG_LOADING";
    private ArrayList<Song> songs;
    ArrayList<Song> filterSongs;
    ArrayList<String> Category;
    String url = URLManager.GET_SEASONS;
    AlbumAdapter albumAdapter;
    Spinner spinner;
    ProgressBar pb;
    private ArrayList<Seasons> seasons;
    SeasonAdapter adapter;
    private String sesaonid;
    int selectedSeasonPosition = 1;
    RecyclerView recyclerViewSongs;
    private ProgressBar progressBar;
    Context context;
    View rootView;
    private Handler uiHandler;
    private Timer timer;
    private Song currentPlayingSong = new Song();
    private ProgressBar progressBarLoading;
    ImageView imageViewRewind, imageViewPlay, imageViewForward;
    LinearLayout layoutPlayer, layoutPlayerButtons;
    RelativeLayout layoutSearch, layoutSeekbar;
    FrameLayout seasonBtn2017, seasonBtn2018;
    TextView season2017Txt, season2018Txt;
    TextView textViewProgressTime, textViewTotalTime;
    SeekBar seekBarSong;
    EditText editTextSearch;
    ImageView imageViewSearch;

    ArrayList<String> catIdsList = new ArrayList<>();

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("SONGS");
        }

        seasons = new ArrayList<>();

        loadSpinnerData(url);
        uiHandler = new Handler();
        int padding16 = Utils.getDpiFromPixel(context, 16);
        int padding8 = Utils.getDpiFromPixel(context, 3);
        rootView = inflater.inflate(R.layout.fragment_album, container, false);
        setViewPadding();
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBarLoading = rootView.findViewById(R.id.progressBarLoading);

        seekBarSong = rootView.findViewById(R.id.seekBarSong);

        imageViewRewind = rootView.findViewById(R.id.imageViewRewind);
        imageViewPlay = rootView.findViewById(R.id.imageViewPlay);
        imageViewForward = rootView.findViewById(R.id.imageViewForward);

        textViewProgressTime = rootView.findViewById(R.id.textViewProgressTime);
        textViewTotalTime = rootView.findViewById(R.id.textViewTotalTime);

        layoutPlayer = rootView.findViewById(R.id.layoutPlayer);
        //layoutPlayer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 150)));
        RelativeLayout layoutSpinner = rootView.findViewById(R.id.layoutSpinner);
        spinner = rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout


        layoutSearch = rootView.findViewById(R.id.layoutSearch);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 40));
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16), 0);
        layoutSearch.setLayoutParams(layoutParams);

       /* seasonBtn2017 = rootView.findViewById(R.id.season_btn_2017);
        seasonBtn2018 = rootView.findViewById(R.id.season_btn_2018);

        season2017Txt = rootView.findViewById(R.id.season_txt_2017);
        season2018Txt = rootView.findViewById(R.id.season_txt_2018);*/

        layoutSeekbar = rootView.findViewById(R.id.layoutSeekbar);
        layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 50));
        layoutParams.gravity = Gravity.CENTER;
        layoutSeekbar.setLayoutParams(layoutParams);

        layoutPlayerButtons = rootView.findViewById(R.id.layoutPlayerButton);
        layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 50));
        layoutParams.gravity = Gravity.CENTER;
        layoutPlayerButtons.setLayoutParams(layoutParams);

        final MusicService musicService = MusicService.getInstance();
        if (musicService != null) {
            musicService.setOnMusicUpdateListener(this);
        }

        if (MusicService.IS_PLAYING) {
            imageViewPlay.setTag(PLAYING);
            imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
            seekBarSong.setProgress(MusicService.songProgress);
            seekBarSong.setMax(MusicService.songMaxProgress);
        } else {
            imageViewPlay.setTag(PAUSE);
            imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
            seekBarSong.setProgress(MusicService.songProgress);
            seekBarSong.setMax(MusicService.songMaxProgress);
        }
        if (MusicService.IS_PROGRESS) {
            progressBarLoading.setVisibility(View.VISIBLE);
            imageViewPlay.setVisibility(View.INVISIBLE);
            MusicService.IS_PROGRESS = false;
        } else {
            progressBarLoading.setVisibility(View.INVISIBLE);
            imageViewPlay.setVisibility(View.VISIBLE);
        }
        imageViewPlay.setOnClickListener(this);
        imageViewRewind.setOnClickListener(this);
        imageViewForward.setOnClickListener(this);
        seekBarSong.setOnSeekBarChangeListener(this);
        spinner.setOnItemSelectedListener(this);




      /*  seasonBtn2017.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seasonBtn2017.setBackgroundResource(R.mipmap.season_selected);
                seasonBtn2018.setBackgroundResource(R.mipmap.season_unselected);
                season2017Txt.setTextColor(Color.parseColor("#20152D"));
                season2018Txt.setTextColor(Color.parseColor("#ffffff"));

                loadFromInternet("1");
                songs = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewSongs.setVisibility(View.INVISIBLE);
            }
        });

        seasonBtn2018.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seasonBtn2017.setBackgroundResource(R.mipmap.season_unselected);
                seasonBtn2018.setBackgroundResource(R.mipmap.season_selected);
                season2017Txt.setTextColor(Color.parseColor("#ffffff"));
                season2018Txt.setTextColor(Color.parseColor("#20152D"));

                loadFromInternet("2");
                songs = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewSongs.setVisibility(View.INVISIBLE);
            }
        });*/

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgressBar();
            }
        }, 0, 1000);
        context.registerReceiver(br_updatePlayerStatus, new IntentFilter(TAG_LOADING));

        textViewProgressTime.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewTotalTime.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        textViewProgressTime.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);
        textViewTotalTime.setPadding(0, 0, Utils.getDpiFromPixel(context, 16), 0);

        recyclerViewSongs = rootView.findViewById(R.id.recyclerViewSongs);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSongs.setHasFixedSize(true);

        recyclerViewSongs.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != 0)
                    Utils.hideKeyBoard(editTextSearch, context);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        imageViewSearch = rootView.findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(this);

        editTextSearch = rootView.findViewById(R.id.editTextSearch);
        editTextSearch.setCursorVisible(false);
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextSearch.setCursorVisible(true);
                } else {
                    editTextSearch.setCursorVisible(false);
                }
            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    filterSongs = new ArrayList<>();
                    for (int i = 0; i < songs.size(); i++) {
                        if (songs.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterSongs.add(songs.get(i));
                        }
                    }
                    imageViewSearch.setImageResource(R.mipmap.ic_clear_white_48dp);
                    albumAdapter.filterData(filterSongs);
                } else {
                    imageViewSearch.setImageResource(R.mipmap.ic_search_white_48dp);
                    albumAdapter.filterData(songs);
                }
            }
        });


        loadFromInternet(String.valueOf(catIdsList));
        context.registerReceiver(br_updatePlayerUI, new IntentFilter(TAG_ALBUM));
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    /*private void loadFromInternet(String id) {
        if (MusicService.getInstance() != null) {
            songs = QueueTableDataManager.getInstance().getSongsListFromQueue(SONGS_QUEUE_TABLE);
            currentPlayingSong = MusicService.getInstance().currentPlayingSong;
            setUpViewsWithData();
        } else if (songs != null) {
            currentPlayingSong = songs.get(0);
            setUpViewsWithData();
        } else {
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.SERVER_URL+"seasons/"+id+"/songs");
            try {
                PepsiApplication.tracker.setScreenName("SongsScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }


    private void loadFromInternet(String id) {
        Log.d("ALBUM", " id: " + id);

        OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
        okHttpApi.callGetRequest(URLManager.SERVER_URL + "seasons/" + id + "/songs");
        try {

            PepsiApplication.tracker.setScreenName("SongsScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
          //  seasoncall();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    ArrayList<String> catList = new ArrayList<>();
                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        catList.add(jsonobject.getString("name"));
                        catIdsList.add(jsonobject.getString("id"));
                        sesaonid = jsonobject.getString("id");

                        Log.d("ALBUM", "id["+i+"] "+jsonobject.getString("id"));
                        Log.d("ALBUM", "name["+i+"] "+jsonobject.getString("name"));

                        seasons.add(new Seasons(jsonobject.getString("id"), jsonobject.getString("name")));
                    }
                    Log.d("ALBUM", "seasons "+seasons.size());
                    adapter = new SeasonAdapter(context, R.layout.spinner_view_selected, seasons);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    ((MainActivity) context).showMiniPlayer(true);
//                    spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
//                            android.R.layout.simple_spinner_dropdown_item, catList));


                } catch (Exception e) {
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


    private void seasoncall() {
        OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), (ResponseApi) this);
        okHttpApi.setShowProgress(true);
        okHttpApi.showProgressDialogWithTitle("Loading...", context);
        okHttpApi.callGetRequest(URLManager.GET_SEASONS);
        adapter = new SeasonAdapter(context, R.layout.spinner_view_selected, seasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    private void setUpViewsWithData() {
        //progressBar.setVisibility(View.INVISIBLE);
        layoutPlayer.setVisibility(View.VISIBLE);

        ((MainActivity) context).showMiniPlayer(true);
        albumAdapter = new AlbumAdapter(context, songs);

        ((MainActivity) context).showMiniPlayer(true);

        albumAdapter.setItemClickListener(this);
        recyclerViewSongs.setAdapter(albumAdapter);
        setAsCurrentSong(currentPlayingSong);
    }

    private void setAsCurrentSong(final Song playingSong) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    currentPlayingSong = playingSong;
                    seekBarSong.setProgress(MusicService.songProgress);
                    textViewProgressTime.setText(MusicService.currentTime);
                    textViewTotalTime.setText(MusicService.totalTime);
                    if (MusicService.IS_PLAYING) {
                        imageViewPlay.setTag(PLAYING);
                        imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                    } else {
                        imageViewPlay.setTag(PAUSE);
                        imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
        if (timer != null) {
            timer.cancel();
        }
        MusicService musicService = MusicService.getInstance();
        if (musicService != null) {
            musicService.setOnMusicUpdateListener(null);
        }
        try {
            if (mBroadcastReceiver != null) {
                context.unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Utils.hideKeyBoard(editTextSearch, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_updatePlayerUI != null)
                context.unregisterReceiver(br_updatePlayerUI);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            recyclerViewSongs.setVisibility(View.VISIBLE);

            try {
                Gson gson = new Gson();
                songs = gson.fromJson(response, new TypeToken<ArrayList<Song>>() {
                }.getType());

                setUpViewsWithData();
                if (songs.size() > 0) {
                    currentPlayingSong = songs.get(0);
                    setUpViewsWithData();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFailed(String type, String webServiceName) {
        DialogHelper.showDialogError(context, "Something went wrong.");
    }

    @Override
    public void onCrash(String crashMsg) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.BR_ACTION_DB_DATA_SET_CHANGED);
        filter.addAction(Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST);
        filter.addAction(Utils.BR_ACTION_NEW_SONG_PLAYED);
        filter.addAction(Utils.BR_ACTION_SHUFFLE_ON);
        filter.addAction(Utils.BR_ACTION_SHUFFLE_OFF);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null)
                    switch (intent.getAction()) {
                        case Utils.BR_ACTION_DB_DATA_SET_CHANGED:
                        case Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST:
                            songs = QueueTableDataManager.getInstance().getSongsListFromQueue(SONGS_QUEUE_TABLE);
                            break;
                        case Utils.BR_ACTION_NEW_SONG_PLAYED:
                            albumAdapter.notifyDataSetChanged();
                            break;
                    }
                albumAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onItemClicked(int position) {
        if (editTextSearch.getText().toString().length() > 0) {
            int index = getIndexOf(filterSongs.get(position));
            Utils.playSongListAndAddToQueue(context, songs, index);
            albumAdapter.notifyDataSetChanged();
            setAsCurrentSong(filterSongs.get(position));
        } else {
            Utils.playSongListAndAddToQueue(context, songs, position);
            albumAdapter.notifyDataSetChanged();
            setAsCurrentSong(songs.get(position));
        }
    }


    private int getIndexOf(Song selectedSong) {
        int index = 0;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getSongID().equals(selectedSong.getSongID())) {
                // Found at index i. Break or return if necessary.
                index = i;
                break;
            }
        }
        return index;
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
                        imageViewPlay.setImageResource(R.mipmap.ic_pause_black_48dp);
                    } else {
                        imageViewPlay.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
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
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
            ((MainActivity) context).layoutToolbarHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_home_theme);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (MusicService.getInstance() != null) {
            final MediaPlayer mMediaPlayer = PatariSingleton.getInstance().getMediaPlayer();
            // Stop updating Ui until the seek operation is completed
//                    isUiDisplaying = false;
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(final MediaPlayer player) {
                    // Resume updating UI once the seek operation is complete
                    uiHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            player.setOnSeekCompleteListener(null);

                        }
                    }, 2000);
                }
            });
            int songSeekPostion = seekBar.getProgress();
            mMediaPlayer.seekTo(songSeekPostion);
            String totalTime = Utils.getTotalSeekTime(mMediaPlayer.getDuration());
            String currentSeekTime = Utils.getCurrentSeekTime(mMediaPlayer.getCurrentPosition());
            textViewProgressTime.setText(currentSeekTime);
            textViewTotalTime.setText(totalTime);
        }
    }

    @Override
    public void onMusicProgressUpdate(MediaPlayer mp) {

    }

    @Override
    public void onMusicEnded(MediaPlayer mp) {
//        if (isUiDisplaying)
        try {
            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        //                        if (isUiDisplaying) {
//                        resetSeekBarViews();
//                        onPlayerIconChanged(R.drawable.play, PAUSE);
                        seekBarSong.setProgress(MusicService.songProgress);
                        textViewProgressTime.setText(MusicService.currentTime);
                        ((MainActivity) context).imageViewPlayPause.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
                        //                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPrepared(Song playingSong) {
        currentPlayingSong = playingSong;
        setAsCurrentSong(playingSong);
        onPlayerIconChanged(R.mipmap.ic_pause_black_48dp, PLAYING);
    }


    @Override
    public void onMusicStarted(final MediaPlayer mMediaPlayer, final Song song) {
//        if (isUiDisplaying)
        try {
            rootView.post(new Runnable() {

                @Override
                public void run() {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        currentPlayingSong = song;
                        setAsCurrentSong(song);
                        // PLog.showLog("onMusicStarted executed :
                        // Playerfragment");
                        seekBarSong.setProgress(mMediaPlayer.getCurrentPosition());
                        seekBarSong.setMax(mMediaPlayer.getDuration());
//                        String totalTime = Utils.getTotalSeekTime(mMediaPlayer.getDuration());
//                        String currentSeekTime = Utils.getCurrentSeekTime(mMediaPlayer.getCurrentPosition());
//                        progressTimeTextView.setText(currentSeekTime);
//                        totalSongTimeTextView.setText(totalTime);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     */
    @Override
    public void onPlayerIconChanged(final int drawableId, final String buttonStatus) {
        if (context != null && context.getResources() != null) {
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
            rootView.post(new Runnable() {

                @Override
                public void run() {
                    imageViewPlay.setTag(buttonStatus);
                    imageViewPlay.setImageBitmap(bitmap);

                }
            });
        }
    }

    private BroadcastReceiver br_updatePlayerStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras() != null)
                    if (intent.getExtras().getBoolean("status")) {
                        progressBarLoading.setVisibility(View.VISIBLE);
                        imageViewPlay.setVisibility(View.INVISIBLE);
                    } else {
                        progressBarLoading.setVisibility(View.INVISIBLE);
                        imageViewPlay.setVisibility(View.VISIBLE);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View view) {
        MusicService musicService = MusicService.getInstance();
        switch (view.getId()) {
            case R.id.imageViewPlay:
                // play/pause functionality
                if (musicService != null) {
                    if (MusicService.IS_PLAYING) {
                        musicService.pauseCurrentSong();
                        view.setTag(PAUSE);
                        imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
                    } else {
                        musicService.resumeCurrentSong();
                        view.setTag(PLAYING);
                        imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                    }

                } else {
                    if (songs != null && songs.size() > 0) {
                        Utils.playSongListAndAddToQueue(context, songs, 0);
                        albumAdapter.notifyDataSetChanged();
                        setAsCurrentSong(songs.get(0));
                    }
                }
                break;

            case R.id.imageViewForward:
                // next song functionality
                if (musicService != null) {
                    progressBarLoading.setVisibility(View.VISIBLE);
                    imageViewPlay.setVisibility(View.INVISIBLE);
                    MusicService.songProgress = 0;
                    musicService.playNextSong();
                    imageViewPlay.setTag(PLAYING);
                    imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                    resetSeekBarViews();
                } else {
                    if (songs != null && songs.size() > 0) {
                        Utils.playSongListAndAddToQueue(context, songs, 1);
                        albumAdapter.notifyDataSetChanged();
                        setAsCurrentSong(songs.get(1));
                    }
                }
                break;

            case R.id.imageViewRewind:
                // previous song functionality
                if (musicService != null) {
                    musicService.playPreviousSong();
                }
                break;
            case R.id.imageViewSearch:
                // previous song functionality
                if (editTextSearch.getText().toString().length() == 0) {
                    editTextSearch.setCursorVisible(true);
                    Utils.showKeyBoard(editTextSearch, context);
                } else {
                    editTextSearch.setCursorVisible(false);
                    editTextSearch.setText("");
                    Utils.hideKeyBoard(editTextSearch, context);
                }
                break;
        }
    }

    /* @Override
     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    loadFromInternet(String.valueOf(position));
     Toast.makeText(context, "spinner position is  "+sesaonid, Toast.LENGTH_SHORT).show();
     songs = new ArrayList<>();
     progressBar.setVisibility(View.VISIBLE);
     recyclerViewSongs.setVisibility(View.INVISIBLE);



     }

     @Override
     public void onNothingSelected(AdapterView<?> parent) {

     }*/
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (br_updatePlayerStatus != null)
                context.unregisterReceiver(br_updatePlayerStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetSeekBarViews() {
        if (seekBarSong != null && textViewProgressTime != null && textViewTotalTime != null) {
            MusicService.songProgress = 0;
            seekBarSong.setProgress(0);
            textViewProgressTime.setText(context.getString(R.string.progress_time));
            textViewTotalTime.setText(context.getString(R.string.progress_time));
        }
    }

    private void updateProgressBar() {
        try {
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        MediaPlayer mMediaPlayer = PatariSingleton.getInstance().getMediaPlayer();
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            progressBarLoading.setVisibility(View.INVISIBLE);
                            imageViewPlay.setTag(PLAYING);
                            imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                            imageViewPlay.setVisibility(View.VISIBLE);
                            seekBarSong.setProgress(mMediaPlayer.getCurrentPosition());
                            seekBarSong.setMax(mMediaPlayer.getDuration());
                            String totalTime = Utils.getTotalSeekTime(mMediaPlayer.getDuration());
                            String currentSeekTime = Utils.getCurrentSeekTime(mMediaPlayer.getCurrentPosition());
                            textViewProgressTime.setText(currentSeekTime);
                            textViewTotalTime.setText(totalTime);
//                            songSeekBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress));
                            seekBarSong.setThumb(context.getResources().getDrawable(R.drawable.thumb));
                            textViewTotalTime.setVisibility(View.VISIBLE);
                        } else if (MusicService.getInstance() == null) {
                            onPlayerIconChanged(R.mipmap.ic_play_arrow_black_48dp, PAUSE);
                        } else if (MusicService.IS_PAUSED) {
                            imageViewPlay.setTag(PAUSE);
                            imageViewPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
                            progressBarLoading.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // If there was no song playing and player fragment was opened and song
        // was clicked from side drawer. So handle that case
        final MusicService musicService = MusicService.getInstance();
        if (musicService != null && musicService.getMusicUpdateListener() == null) {
            musicService.setOnMusicUpdateListener(this);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    setAsCurrentSong(musicService.getPlayingSong());
                }
            });
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSeasonPosition = position;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        if (progressBar.getVisibility() == View.VISIBLE) {
            Log.d("progressBar", "" + progressBar.getVisibility());
        }

        loadFromInternet(catIdsList.get(position));

        // Toast.makeText(context, "spinner position is  "+position, Toast.LENGTH_SHORT).show();
        songs = new ArrayList<>();

        recyclerViewSongs.setVisibility(View.GONE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
