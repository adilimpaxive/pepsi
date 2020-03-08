package com.pepsi.battleofthebands.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.adapter.MenuAdapter;
import com.pepsi.battleofthebands.app.PatariSingleton;
import com.pepsi.battleofthebands.db.DatabaseHelper;
import com.pepsi.battleofthebands.db.QueueTableDataManager;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Bands;
import com.pepsi.battleofthebands.entity.Judges;
import com.pepsi.battleofthebands.entity.MenuItem;
import com.pepsi.battleofthebands.entity.News;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.fragments.AlbumFragment;
import com.pepsi.battleofthebands.fragments.BandsDetailFragment;
import com.pepsi.battleofthebands.fragments.BandsFragment;
import com.pepsi.battleofthebands.fragments.EpisodesFragment;
import com.pepsi.battleofthebands.fragments.FeedbackFragment;
import com.pepsi.battleofthebands.fragments.FollowUsFragment;
import com.pepsi.battleofthebands.fragments.GalleryAlbumFragment;
import com.pepsi.battleofthebands.fragments.GalleryFragment;
import com.pepsi.battleofthebands.fragments.HomeFragment;
import com.pepsi.battleofthebands.fragments.JudgesDetailFragment;
import com.pepsi.battleofthebands.fragments.JudgesFragment;
import com.pepsi.battleofthebands.fragments.NewsDetailFragment;
import com.pepsi.battleofthebands.fragments.NewsFragment;
import com.pepsi.battleofthebands.fragments.PerformanceFragment;
import com.pepsi.battleofthebands.fragments.ProfileFragment;
import com.pepsi.battleofthebands.fragments.ScheduleFragment;
import com.pepsi.battleofthebands.fragments.SeasonFragment;
import com.pepsi.battleofthebands.fragments.TermsAndConditionsFragment;
import com.pepsi.battleofthebands.fragments.VotingFragment;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.services.MusicService;
import com.pepsi.battleofthebands.utils.OnSwipeTouchListener;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.PLog;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pepsi.battleofthebands.fragments.EpisodesFragment.youtubeAPI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ResponseApi, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.OnFullscreenListener {
    //Broadcast
    public static final String TAG_TOKEN = "TAG_TOKEN";
    public static final String TAG_LOADING = "TAG_LOADING";
    public static final String TAG_MINI_PLAYER = "TAG_MINI_PLAYER";
    public static final String TAG_BACK_PRESSED = "TAG_BACK_PRESSED";
    public static final String TAG_LOGOUT = "TAG_LOGOUT";
    public static final String TAG_YOUTUBE_PLAYER = "TAG_YOUTUBE_PLAYER";

    Context context;
    public RelativeLayout layoutToolbarHeader;

    private TextView textViewSongName, textViewSongArtistName;
    public TextView textViewTitle;
    ImageView imageViewSong, imageViewClose;
    private RelativeLayout layoutMiniPlayer;
    public ImageView imageViewPlayPause;
    private ProgressBar loadingProgressbar;
    public ImageView imageViewMenu, imageViewBack, imageViewBackground;
    private Song currentPlayingSong = null;
    Handler mHandler;
    public FrameLayout frameLayout;
    Spinner spinner;
    MenuAdapter menuAdapter;
    String url= URLManager.GET_SETTINGS;
    String anthem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setContentView(R.layout.activity_main);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                //            clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(FLAG_TRANSLUCENT_STATUS);
                //             add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //             finally change the color
                if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
//                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_background_color_top));
                } else {
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_background_color_top));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        context = this;
        try {
            DatabaseHelper.getInstance().getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.pepsi.battleofthebands",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/


        initialize();
        loadSpinnerData(url);
        initializeYoutubePlayer();
        registerReceivers();


        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Log.d("TOKEN", instanceIdResult.getToken());
                    String deviceToken = instanceIdResult.getToken();
                    sendRegistrationToServer(deviceToken);
//                    sendBroadcast(new Intent(MainActivity.TAG_TOKEN).putExtra("token", deviceToken));
                    // Do whatever you want with your token now
                    // i.e. store it on SharedPreferences or DB
                    // or directly send it to server
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isYouTubePlayerFullScreen;
    public LinearLayout layoutYoutubePlayer;
    public TextView textViewAnthemDescription;
    public ImageView imageViewLogo;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    //youtube player to play video when new video selected
    public YouTubePlayer youTubePlayer;

    private void initializeYoutubePlayer() {
        youTubePlayerFragment.initialize(youtubeAPI, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                youTubePlayer = player;
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
//                    if (!videoName.isEmpty())
//                        playYoutubeVideo(videoName);
                } else {
//                    youTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

                //print or show error if initialization failed
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        getMenuItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        System.gc();
        DialogHelper.dismissFullScreenProgressDialog();
        try {
            if (mMediaPlayerBroadcastReceiver != null) {
                unregisterReceiver(mMediaPlayerBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_token != null) {
                unregisterReceiver(br_token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_updatePlayerUI != null) {
                unregisterReceiver(br_updatePlayerUI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_back_pressed != null) {
                unregisterReceiver(br_back_pressed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_mini_player != null) {
                unregisterReceiver(br_mini_player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_logout != null) {
                unregisterReceiver(br_logout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_YoutubePlayer != null) {
                unregisterReceiver(br_YoutubePlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isHome = false;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void setMiniPlayerViews(final Song currentPlayingSong) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                textViewSongName.setText(currentPlayingSong.getName());
                textViewSongArtistName.setText(currentPlayingSong.getSinger().getName());
                Picasso.with(context).load(currentPlayingSong.getThumbnail()).placeholder(ContextCompat.getDrawable(context, R.mipmap.default_image)).into(imageViewSong);
            }
        });
    }

    private void initialize() {
        spinner = findViewById(R.id.spinner);
        spinner.setEnabled(false);
        spinner.setClickable(false);
        getMenuItems();

        frameLayout = findViewById(R.id.container);

        layoutYoutubePlayer = findViewById(R.id.layoutYoutubePlayer);
        textViewAnthemDescription = findViewById(R.id.textViewAnthemDescription);
        textViewAnthemDescription.setPadding(Utils.getDpiFromPixel(context, 16), Utils.getDpiFromPixel(context, 16), Utils.getDpiFromPixel(context, 16), Utils.getDpiFromPixel(context, 16));
        textViewAnthemDescription.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));

        imageViewLogo = findViewById(R.id.imageViewLogo);

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_player_fragment);

        layoutMiniPlayer = findViewById(R.id.musicControlBottomLinearLayout);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSongName = findViewById(R.id.songNameTextView);
        textViewSongArtistName = findViewById(R.id.songArtistNameTextView);
        imageViewClose = findViewById(R.id.imageViewClose);
        imageViewSong = findViewById(R.id.imageViewSong);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBackground = findViewById(R.id.imageViewBackground);
        imageViewPlayPause = findViewById(R.id.musicControlImageView);
        loadingProgressbar = findViewById(R.id.loadingProgressbar);
        layoutToolbarHeader = findViewById(R.id.headerLinearLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 56));
        layoutToolbarHeader.setLayoutParams(params);
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).layoutToolbarHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        } else {
            ((MainActivity) context).layoutToolbarHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutMiniPlayer.setLayoutParams(layoutParams);

        imageViewMenu.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
        imageViewPlayPause.setOnClickListener(this);
        layoutMiniPlayer.setOnClickListener(this);
        textViewTitle.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_BOLD));
        textViewSongName.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_BOLD));
        textViewSongArtistName.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_BOLD));

        mHandler = new Handler();

        textViewSongName.setPadding(Utils.getDpiFromPixel(this, 12), 0, 0, 0);
        textViewSongArtistName.setPadding(Utils.getDpiFromPixel(this, 12), 0, 0, 0);
        imageViewMenu.setPadding(Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16), 0);
        imageViewBack.setPadding(Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16), 0);

        RelativeLayout layoutSongImage = findViewById(R.id.layoutSongImage);
        layoutSongImage.setPadding(Utils.getDpiFromPixel(this, 16), 0, 0, 0);

        RelativeLayout layoutPlay = findViewById(R.id.layoutPlay);
        layoutPlay.setPadding(0, 0, Utils.getDpiFromPixel(this, 16), 0);

        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 32), Utils.getDpiFromPixel(context, 32));
        imageViewSong.setLayoutParams(paramsImage);

        paramsImage = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 25), MATCH_PARENT);
        paramsImage.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsImage.setMargins(Utils.getDpiFromPixel(this, 10), Utils.getDpiFromPixel(this, 10), Utils.getDpiFromPixel(this, 10), Utils.getDpiFromPixel(this, 10));
        imageViewClose.setLayoutParams(paramsImage);

        paramsImage = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 40), Utils.getDpiFromPixel(context, 40));
        imageViewPlayPause.setLayoutParams(paramsImage);

        layoutMiniPlayer.setOnTouchListener(new OnSwipeTouchListener() {

            @Override
            public boolean onSwipeLeft() {
                // next song functionality
                MusicService musicService1 = MusicService.getInstance();
                if (musicService1 != null) {
                    musicService1.playNextSong();
                }
                return true;
            }

            @Override
            public boolean onSwipeRight() {
                // previous song functionality
                MusicService musicService2 = MusicService.getInstance();
                if (musicService2 != null) {
                    musicService2.playPreviousSong();
                }
                return true;
            }
        });

        if (MusicService.getInstance() != null) {
            layoutMiniPlayer.setVisibility(View.VISIBLE);
            setMiniPlayerViews(MusicService.getInstance().currentPlayingSong);
        }
        FacebookSdk.sdkInitialize(this);
        AppLinkData.fetchDeferredAppLinkData(
                context,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        //process applink data
                    }
                });
        showHomeFragment();
    }

    private void getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        MenuItem item = new MenuItem();
        item.setName("Home");
        item.setImage(R.mipmap.menu_item_home);
        menuItems.add(item);
        if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
            item = new MenuItem();
            item.setName("Profile");
            item.setImage(R.mipmap.menu_item_profile);
            menuItems.add(item);
            item = new MenuItem();
            item.setName("Logout");
            item.setImage(R.mipmap.menu_item_login);
            menuItems.add(item);
        } else {
            item = new MenuItem();
            item.setName("Login");
            item.setImage(R.mipmap.menu_item_login);
            menuItems.add(item);
            item = new MenuItem();
            item.setName("Register");
            item.setImage(R.mipmap.menu_item_register);
            menuItems.add(item);
        }
        item = new MenuItem();
        item.setName("Contact Us");
        item.setImage(R.mipmap.mail_icon);
        menuItems.add(item);
        item = new MenuItem();
        item.setName("Terms & Conditions");
        item.setImage(R.mipmap.mail_icon);
        menuItems.add(item);
        item = new MenuItem();
        item.setName("");
        item.setImage(R.mipmap.mail_icon);
        menuItems.add(item);
        menuAdapter = new MenuAdapter(context, R.layout.spinner_view_selected, menuItems);
        menuAdapter.setDropDownViewResource(R.layout.spinner_view_selected);
        spinner.setAdapter(menuAdapter);
    }

    public boolean showMiniPlayer(final boolean isShow) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof AlbumFragment) {
            layoutMiniPlayer.setVisibility(View.GONE);
            return false;
        } else if (MusicService.SERVICE_IS_RUNNING) {
            if (isShow) {
                layoutMiniPlayer.setVisibility(View.VISIBLE);
                return true;
            } else {
                layoutMiniPlayer.setVisibility(View.GONE);
                return false;
            }
        } else {
            layoutMiniPlayer.setVisibility(View.GONE);
            return false;
        }
    }

    public static boolean isHome = true;

    @Override
    public void onBackPressed() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DialogHelper.dismissFullScreenProgressDialog();
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            // work as other flow
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

            if (currentFragment instanceof HomeFragment) {
                // if back pressed on Home Screen then exit the app
                if (frameLayout.getVisibility() == View.GONE && layoutYoutubePlayer.getVisibility() == View.VISIBLE) {
                    if (isYouTubePlayerFullScreen) {
                        youTubePlayer.setFullscreen(false);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        imageViewBack.setVisibility(View.GONE);
                        youTubePlayer.pause();
                        ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
                        ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
                    }
                    return;
                } else if (youTubePlayer != null && isYouTubePlayerFullScreen) {
                    youTubePlayer.setFullscreen(false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return;
                }
                this.finish();
            } else if (backStackCount > 1 && getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1).getName().equals(getSupportFragmentManager().getBackStackEntryAt(backStackCount - 2).getName())) {
                // remove duplicate fragment additions
                String duplicateFragmentName = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1).getName();
                for (int i = backStackCount; i > 0; i--) {
                    if (getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1).getName().equals(duplicateFragmentName)) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            } else if (layoutYoutubePlayer.getVisibility() == View.VISIBLE) {
                if (isYouTubePlayerFullScreen) {
                    youTubePlayer.setFullscreen(false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (youTubePlayer != null)
                        if (youTubePlayer.isPlaying()) {
                            youTubePlayer.pause();
                        }
                    super.onBackPressed();
                }
                return;
            } else if (youTubePlayer != null && isYouTubePlayerFullScreen) {
                youTubePlayer.setFullscreen(false);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return;
            }
            super.onBackPressed();
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                // backStackCount reload discover fragment but do not add to
                // backstack as it only last fragment left
                //                    showHomeFragment();
                getSupportFragmentManager().popBackStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewMenu:
                spinner.performClick();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.imageViewClose:
                Intent intent = new Intent(this, MusicService.class);
                stopService(intent);
                break;
            case R.id.musicControlImageView:
                // play/pause functionality
                MusicService musicService = MusicService.getInstance();
                if (musicService != null) {
                    MediaPlayer player = PatariSingleton.getInstance().getMediaPlayer();
                    if (player.isPlaying()) {
                        musicService.pauseCurrentSong();
                        imageViewPlayPause.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
                    } else {
                        musicService.resumeCurrentSong();
                        imageViewPlayPause.setImageResource(R.mipmap.ic_pause_black_48dp);
                    }
                }
                break;
            case R.id.musicControlBottomLinearLayout:
                layoutMiniPlayer.setVisibility(View.GONE);
                showAlbumFragment();
                break;
        }
    }

    public void dismissSpinner(int position) {
        spinner.setSelection(position);
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    public void showHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), HomeFragment.class.getName()).commit();
    }

    public void showHome() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void registerUser() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        pauseYoutubeVideo();
    }

    public void loginUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        pauseYoutubeVideo();
    }

    public void logoutUser() {
        showHome();
        Utils.removeUserInfoFromPreferenceAndLogout(context);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMenuItems();
            }
        });
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    AccessToken.setCurrentAccessToken(null);
//                    Profile.setCurrentProfile(null);
//                    LoginManager.getInstance().logOut();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestEmail()
//                            .build();
//                    GoogleSignInClient signInClient = GoogleSignIn.getClient(MainActivity.this, gso);
//                    signInClient.signOut();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//                disconnectFromFacebook();
    }

//    public void disconnectFromFacebook() {
//
//        if (AccessToken.getCurrentAccessToken() == null) {
//            return; // already logged out
//        }
//
//        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
//                .Callback() {
//            @Override
//            public void onCompleted(GraphResponse graphResponse) {
//
//                LoginManager.getInstance().logOut();
//
//            }
//        }).executeAsync();
//    }

    public void profileUser() {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance(), ProfileFragment.class.getName()).addToBackStack(ProfileFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show The Album Fragment with the list of all songs in the current Album
     *
     * @param season id
     */
    public void showSeasonFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, SeasonFragment.newInstance(), SeasonFragment.class.getName()).addToBackStack(SeasonFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGalleryFragment() {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, GalleryFragment.newInstance(), GalleryFragment.class.getName()).addToBackStack(GalleryFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGalleryAlbumFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, GalleryAlbumFragment.newInstance(season), GalleryAlbumFragment.class.getName()).addToBackStack(GalleryAlbumFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNewsFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, NewsFragment.newInstance(season), NewsFragment.class.getName()).addToBackStack(NewsFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEpisodeFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, EpisodesFragment.newInstance(season), EpisodesFragment.class.getName()).addToBackStack(EpisodesFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showScheduleFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, ScheduleFragment.newInstance(season), ScheduleFragment.class.getName()).addToBackStack(ScheduleFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBandFragment(String season) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, BandsFragment.newInstance(season), BandsFragment.class.getName()).addToBackStack(BandsFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPerformanceFragment(String title) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, PerformanceFragment.newInstance(title), PerformanceFragment.class.getName()).addToBackStack(PerformanceFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFollowUsFragment() {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, FollowUsFragment.newInstance(), FollowUsFragment.class.getName()).addToBackStack(FollowUsFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBandDetailFragment(Bands bands) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, BandsDetailFragment.newInstance(bands.getId(), bands.getName(), bands.getDescription(), bands.getBanner()), BandsDetailFragment.class.getName()).addToBackStack(BandsDetailFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showJudgeDetailFragment(Judges judges) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, JudgesDetailFragment.newInstance(judges.getId(), judges.getName(), judges.getDesignation(), judges.getDescription(), judges.getBanner()), JudgesDetailFragment.class.getName()).addToBackStack(JudgesDetailFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNewsDetailFragment(News news) {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, NewsDetailFragment.newInstance(news.getId(), news.getTitle(), news.getDescription(), news.getThumbnail(), news.getBanner()), NewsDetailFragment.class.getName()).addToBackStack(NewsDetailFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showJudgesFragment() {
        // update the main content by replacing fragments
        isHome = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, JudgesFragment.newInstance(), JudgesFragment.class.getName()).addToBackStack(JudgesFragment.class.getName()).commit();
    }

    public void showVotingFragment() {
        // update the main content by replacing fragments
        isHome = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, VotingFragment.newInstance(), VotingFragment.class.getName()).addToBackStack(VotingFragment.class.getName()).commit();
    }

    public void showAlbumFragment() {
        // update the main content by replacing fragments
        isHome = false;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, AlbumFragment.newInstance(), AlbumFragment.class.getName()).addToBackStack(AlbumFragment.class.getName()).commit();
    }

    public void showFeedbackFragment() {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, FeedbackFragment.newInstance(), FeedbackFragment.class.getName()).addToBackStack(FeedbackFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTermsAndConditionsFragment() {
        try {
            isHome = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, TermsAndConditionsFragment.newInstance(), TermsAndConditionsFragment.class.getName()).addToBackStack(TermsAndConditionsFragment.class.getName()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.BR_ACTION_PLAY_SONG);
        filter.addAction(Utils.BR_ACTION_PLAY_ALL_ALBUM);
        filter.addAction(Utils.BR_ACTION_SHOW_MINI_PLAYER);
        filter.addAction(Utils.BR_ACTION_RESET_PLAYER_LAYOUT);
        filter.addAction(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED);
        filter.addAction(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_STOPPED);
        filter.addAction(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED);
        // Home Screen Playlist/Album Broadcast
        filter.addAction(Utils.BR_ACTION_UPDATE_SONGS_LIST_UI_AND_SET_SPECIFIC_SONG_AS_SELECTED);
        registerReceiver(mMediaPlayerBroadcastReceiver, filter);

        registerReceiver(br_token, new IntentFilter(TAG_TOKEN));
        registerReceiver(br_updatePlayerUI, new IntentFilter(TAG_LOADING));
        registerReceiver(br_back_pressed, new IntentFilter(TAG_BACK_PRESSED));
        registerReceiver(br_mini_player, new IntentFilter(TAG_MINI_PLAYER));
        registerReceiver(br_logout, new IntentFilter(TAG_LOGOUT));
        registerReceiver(br_YoutubePlayer, new IntentFilter(TAG_YOUTUBE_PLAYER));
    }

    private BroadcastReceiver mMediaPlayerBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public synchronized void onReceive(Context context, final Intent intent) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment instanceof AlbumFragment) {
                showMiniPlayer(false);
            }
            String playSession = "";
            if (intent.getExtras() != null && intent.hasExtra("playSessionID"))
                playSession = intent.getExtras().getString("playSessionID");
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Utils.BR_ACTION_PLAY_SONG:
                        currentPlayingSong = (Song) intent.getSerializableExtra("song");
                        if (currentPlayingSong != null) {
                            QueueTableDataManager dataDoa = QueueTableDataManager.getInstance();
                            if (dataDoa != null) {
                                int result = dataDoa.insertSongInToQueue(currentPlayingSong, QueueTableDataManager.SONGS_QUEUE_TABLE);
                                if (result != -1) {
                                    // new song added to queue so play last
                                    if (!MusicService.SERVICE_IS_RUNNING) {
                                        Intent serviceIntent = new Intent(context, MusicService.class);
                                        // play last song that is added into queue
                                        serviceIntent.putExtra("Play", "Last");
                                        serviceIntent.putExtra("playSessionID", playSession);
                                        // Start the Service
                                        startService(serviceIntent);
                                    } else {
                                        Intent serviceBr = new Intent(Utils.BR_ACTION_START_PLAYING_SELECTED_SONG);
                                        serviceBr.putExtra("playSessionID", playSession);
                                        sendBroadcast(serviceBr);
                                    }
                                } else {
                                    // already added song selected so fetch its index
                                    // from service songs list
                                    if (!MusicService.SERVICE_IS_RUNNING) {
                                        Intent serviceIntent = new Intent(context, MusicService.class);
                                        // play selected song that is already added into
                                        // queue
                                        serviceIntent.putExtra("Play", "Already_Added");
                                        serviceIntent.putExtra("Song", currentPlayingSong);
                                        serviceIntent.putExtra("playSessionID", playSession);
                                        // Start the Service
                                        startService(serviceIntent);
                                    } else {
                                        Intent serviceBr = new Intent(Utils.BR_ACTION_START_PLAYING_ALREADY_ADDED);
                                        serviceBr.putExtra("Song", currentPlayingSong);
                                        serviceBr.putExtra("playSessionID", playSession);
                                        sendBroadcast(serviceBr);
                                    }
                                }
                                // update player fragment if opened at back
                                setMiniPlayerViews(currentPlayingSong);
                                // Update The navigation Fragment UI
                                context.sendBroadcast(new Intent(Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST));
                            }
                        }
                        break;
                    case Utils.BR_ACTION_PLAY_ALL_ALBUM:
                        QueueTableDataManager queueTableDataManager = QueueTableDataManager.getInstance();
                        currentPlayingSong = queueTableDataManager.getFirstSongFromQueue(QueueTableDataManager.SONGS_QUEUE_TABLE);
                        setMiniPlayerViews(currentPlayingSong);
                        if (!MusicService.SERVICE_IS_RUNNING) {
                            // startMusicService();
                            // Create an Explicit Intent
                            Intent serviceIntent = new Intent(context, MusicService.class);
                            // play first song of the album which is added to
                            // the queue
                            serviceIntent.putExtra("Play", "First");
                            serviceIntent.putExtra("playSessionID", playSession);
                            // Start the Service
                            startService(serviceIntent);
                        } else {
                            Intent serviceBr = new Intent(Utils.BR_ACTION_START_PLAYING_ALL_ALBUM_SONG);
                            serviceBr.putExtra("playSessionID", playSession);
                            sendBroadcast(serviceBr);
                        }
                        // Update The navigation Fragment UI
                        context.sendBroadcast(new Intent(Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST));
                        showMiniPlayer(true);
                        break;
                    case Utils.BR_ACTION_UPDATE_SONGS_LIST_UI_AND_SET_SPECIFIC_SONG_AS_SELECTED:
                        int selectedSongIndex = intent.getIntExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY, 0);
                        // Get the Selected Song from database
                        QueueTableDataManager mQueueTableDataManager = QueueTableDataManager.getInstance();
                        ArrayList<Song> fullSongList = mQueueTableDataManager.getSongsListFromQueue(QueueTableDataManager.SONGS_QUEUE_TABLE);
                        currentPlayingSong = fullSongList.get(selectedSongIndex);
                        // update player fragment if opened at back
                        setMiniPlayerViews(currentPlayingSong);
                        // startMusicService();
                        // Create an Explicit Intent
                        Intent serviceIntent = new Intent(context, MusicService.class);
                        // play specific song in the Queue
                        serviceIntent.putExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY, selectedSongIndex);
                        // Start the Service
                        startService(serviceIntent);

                        break;
                    case Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED:
                        MusicService musicService = MusicService.getInstance();
                        if (musicService != null && musicService.getPlayingSong() != null) {
                            // find player fragment if added and update its
                            // content
                            currentPlayingSong = musicService.getPlayingSong();
                            setMiniPlayerViews(currentPlayingSong);
                        }
                        break;
                    case Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_STOPPED:
                        imageViewPlayPause.setImageResource(R.mipmap.ic_play_arrow_black_48dp);
                        break;
                    case Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED:
                        imageViewPlayPause.setImageResource(R.mipmap.ic_pause_black_48dp);
                        break;
                    case Utils.BR_ACTION_SHOW_MINI_PLAYER:
                        showMiniPlayer(true);
                        break;
                    case Utils.BR_ACTION_RESET_PLAYER_LAYOUT:
                        sendBroadcast(new Intent(Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST));
                        layoutMiniPlayer.setVisibility(View.GONE);
                        break;
                }
            }
            System.gc();
        }
    };

    public BroadcastReceiver br_logout = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
    public BroadcastReceiver br_YoutubePlayer = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean isPlay = false;
                if (intent.getExtras() != null) {
                    isPlay = intent.getExtras().getBoolean("isPlay");
                }
                if (intent.getExtras() != null && intent.hasExtra("playOfficialVideo")) {
                    textViewAnthemDescription.setVisibility(View.VISIBLE);
                    textViewAnthemDescription.setText(anthem);
                    imageViewLogo.setVisibility(View.VISIBLE);
                    imageViewBack.setVisibility(View.VISIBLE);
                    layoutYoutubePlayer.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                    para.setMargins(0, Utils.getDpiFromPixel(context, 60), 0, 0);
                    layoutYoutubePlayer.setLayoutParams(para);
                    frameLayout.setVisibility(View.GONE);
                    playYoutubeVideo(intent.getExtras().getString("videoName"), true);
                } else if (intent.getExtras() != null && intent.hasExtra("showPlayer")) {

                    textViewAnthemDescription.setVisibility(View.GONE);
                    imageViewLogo.setVisibility(View.GONE);
                    if (intent.getExtras().getBoolean("showPlayer")) {
                        layoutYoutubePlayer.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                        para.setMargins(0, Utils.getDpiFromPixel(context, 60), 0, 0);
                        layoutYoutubePlayer.setLayoutParams(para);
                        playYoutubeVideo(intent.getExtras().getString("videoName"), isPlay);
                    } else {
                        layoutYoutubePlayer.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void playYoutubeVideo(String videoName, boolean playVideo) {
        try {
            if (youTubePlayer != null) {
                if (playVideo) {
                    youTubePlayer.loadVideo(videoName);
                } else {
                    youTubePlayer.cueVideo(videoName);
                }
                youTubePlayer.setPlaybackEventListener(MainActivity.this);
                youTubePlayer.setPlaybackEventListener(MainActivity.this);
                youTubePlayer.setOnFullscreenListener(MainActivity.this);
            } else {
                initializeYoutubePlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            initializeYoutubePlayer();
        }
    }

    public BroadcastReceiver br_updatePlayerUI = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras() != null) {
                    if (intent.getExtras().getBoolean("status")) {
                        loadingProgressbar.setVisibility(View.VISIBLE);
                        imageViewPlayPause.setVisibility(View.INVISIBLE);
                    } else {
                        loadingProgressbar.setVisibility(View.INVISIBLE);
                        imageViewPlayPause.setImageResource(R.mipmap.ic_pause_black_48dp);
                        imageViewPlayPause.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public BroadcastReceiver br_token = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null)
                sendRegistrationToServer(intent.getExtras().getString("token"));
        }
    };

    private void sendRegistrationToServer(String token) {
        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(MainActivity.this, this);
        okHttpApi.callPostRequest(URLManager.GET_TOKEN_URL, formBody);
    }

    public BroadcastReceiver br_back_pressed = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onBackPressed();
            showMiniPlayer(true);
        }
    };
    public BroadcastReceiver br_mini_player = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                showMiniPlayer(intent.getExtras().getBoolean("show", false));
            }
        }
    };

    @Override
    public void onNetworkNotAvailable() {

    }

    @Override
    public void onResponse(String response, String webServiceName) {
//        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String type, String webServiceName) {
        Log.e("", "");
    }

    @Override
    public void onCrash(String crashMsg) {
    }

    @Override
    public void onPlaying() {
        sendBroadcast(new Intent(EpisodesFragment.TAG_EPISODE).putExtra("updateUI", true));
        sendBroadcast(new Intent(PerformanceFragment.TAG_PERFORMANCE).putExtra("updateUI", true));
        PLog.showLog("Youtube", "onPlaying");
    }

    @Override
    public void onPaused() {
        sendBroadcast(new Intent(EpisodesFragment.TAG_EPISODE).putExtra("updateUI", false));
        sendBroadcast(new Intent(PerformanceFragment.TAG_PERFORMANCE).putExtra("updateUI", false));
        PLog.showLog("Youtube", "onPaused");
    }

    @Override
    public void onStopped() {
        sendBroadcast(new Intent(EpisodesFragment.TAG_EPISODE).putExtra("updateUI", false));
        sendBroadcast(new Intent(PerformanceFragment.TAG_PERFORMANCE).putExtra("updateUI", false));
        PLog.showLog("Youtube", "onStopped");
    }

    @Override
    public void onBuffering(boolean b) {
        PLog.showLog("Youtube", "onBuffering");
    }

    @Override
    public void onSeekTo(int i) {
        PLog.showLog("Youtube", "onSeekTo");
    }

    @Override
    public void onLoading() {
        PLog.showLog("Youtube", "onLoading");
    }

    @Override
    public void onLoaded(String s) {
        PLog.showLog("Youtube", "onLoaded");
    }

    @Override
    public void onAdStarted() {
        PLog.showLog("Youtube", "onAdStarted");
    }

    @Override
    public void onVideoStarted() {
        PLog.showLog("Youtube", "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
        PLog.showLog("Youtube", "onVideoEnded");
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        PLog.showLog("Youtube", errorReason.toString());
    }

    @Override
    public void onFullscreen(boolean b) {
        if (b)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isYouTubePlayerFullScreen = b;
    }

    public void pauseYoutubeVideo() {
        imageViewBack.setVisibility(View.GONE);
        if (youTubePlayer != null)
            youTubePlayer.pause();
        ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
        ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
    }


    private void loadSpinnerData(String url) {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonarray = new JSONArray(response);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        //votingmesage=jsonobject.getString("voting_message");

                        anthem=jsonobject.getString("tvc_description");


                    }


                }catch (Exception e){
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
}