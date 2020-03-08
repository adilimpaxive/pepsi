package com.pepsi.battleofthebands.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PatariSingleton;
import com.pepsi.battleofthebands.callback.OnMusicUpdateListener;
import com.pepsi.battleofthebands.db.QueueTableDataManager;
import com.pepsi.battleofthebands.dialogs.AppDialog;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.fragments.AlbumFragment;
import com.pepsi.battleofthebands.receiver.MediaButtonReceiver;
import com.pepsi.battleofthebands.utils.PLog;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "MusicService";
    public static int songProgress = 0;
    public static int songMaxProgress = 0;
    public static String currentTime = "00:00";
    public static String totalTime = "00:00";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_STOP = 4;
    private static final int REQUEST_CODE_NEXT = 1;
    private static final int REQUEST_CODE_PREVIOUS = 2;
    private static final int REQUEST_CODE_PLAY_PAUSE = 3;
    // song list
    public ArrayList<Song> songsList = null;
    // current position
    public Song currentPlayingSong;
    int currentPlayingSongIndex = 0;

    private static MusicService musicService = null;
    public static boolean IS_SONG_LOADING = false;
    public static boolean IS_PLAYING = false;
    public static boolean IS_PROGRESS = false;
    public static boolean IS_AUDIO_FOCUS_CHANGE = false;
    public static boolean IS_PAUSED = false;
    public static boolean SERVICE_IS_RUNNING = false;
    public static boolean FETCHING_RECOMMENDATIONS = false;
    // Check for checking whether music was being played before call came and it
    // was interrupted
    private static boolean MUSIC_INTERRUPTED = false;
    private Bitmap currentSongImageBitmap = null, defaultSongBitmap = null, currentAdImageBitmap = null;
    private OnMusicUpdateListener musicUpdateListener = null;
    private WifiLock wifiLock;
    private Timer timer, playerTimer;
    boolean isOffline = false;
    long time = 1500;

    private AudioManager mAudioManager;
    private ComponentName mRemoteControlResponder;
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";
    private NotificationManagerCompat mNotificationManager;

    // Audio Ads
    public int previousPlayedAdIndex = 0;
    public int songPlayedInRow = 0;

    public void setOnMusicUpdateListener(OnMusicUpdateListener listner) {
        this.musicUpdateListener = listner;
    }

    public OnMusicUpdateListener getMusicUpdateListener() {
        return musicUpdateListener;
    }

    public static MusicService getInstance() {
        return musicService;
    }

    public void onCreate() {
        // create the service
        super.onCreate();
        PLog.showLog(TAG, "onCreate executed");
        musicService = this;
        defaultSongBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.default_image);
        // Set Audio Ads Value
        previousPlayedAdIndex = 0;
        songPlayedInRow = 0;

        songsList = new ArrayList<>();
        // create player
        // initMusicPlayer();
        initBroadCastReciever();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlResponder = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
        mNotificationManager = NotificationManagerCompat.from(this);
        IS_PROGRESS = false;
        sendBroadcast(new Intent(MainActivity.TAG_MINI_PLAYER).putExtra("show", true));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IS_PROGRESS = false;
        SERVICE_IS_RUNNING = true;
        if (intent.hasExtra("Play") && intent.getStringExtra("Play").equals("Last")) {
            getSongsFromDatabase();
            if (songsList.size() > 0)
                songPlaybackRequest(songsList.get(songsList.size() - 1), true);
        } else if (intent.hasExtra("Play") && intent.getStringExtra("Play").equals("First")) {
            getSongsFromDatabase();
            if (songsList.size() > 0)
                songPlaybackRequest(songsList.get(0), true);
        } else if (intent.hasExtra(Utils.EXTRA_NOTIFICATION_KEY) && intent.getStringExtra(Utils.EXTRA_NOTIFICATION_KEY).equals(Utils.EXTRA_NOTIFICATION_CONTROL_CLOSE_PLAYER_AND_SONG)) {
            if (musicUpdateListener != null) {
                musicUpdateListener.onMusicEnded(PatariSingleton.getInstance().getMediaPlayer());
            }
            stopSelf();
        } else if (intent.hasExtra(Utils.EXTRA_NOTIFICATION_KEY) && intent.getStringExtra(Utils.EXTRA_NOTIFICATION_KEY).equals(Utils.EXTRA_NOTIFICATION_CONTROL_REWIND_SONG)) {
            playPreviousSong();
        } else if (intent.hasExtra(Utils.EXTRA_NOTIFICATION_KEY) && intent.getStringExtra(Utils.EXTRA_NOTIFICATION_KEY).equals(Utils.EXTRA_NOTIFICATION_CONTROL_FORWARD_SONG)) {
            playNextSong();
        } else if (intent.hasExtra(Utils.EXTRA_NOTIFICATION_KEY) && intent.getStringExtra(Utils.EXTRA_NOTIFICATION_KEY).equals(Utils.EXTRA_NOTIFICATION_CONTROL_PAUSE_SONG)) {
            pauseCurrentSong();
        } else if (intent.hasExtra(Utils.EXTRA_NOTIFICATION_KEY) && intent.getStringExtra(Utils.EXTRA_NOTIFICATION_KEY).equals(Utils.EXTRA_NOTIFICATION_CONTROL_PLAY_SONG)) {
            resumeCurrentSong();
        } else if (intent.hasExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY)) {
            getSongsFromDatabase();
            currentPlayingSongIndex = intent.getIntExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY, 0);
            currentPlayingSong = songsList.get(currentPlayingSongIndex);
            songPlaybackRequest(currentPlayingSong, true);
            intent.removeExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY);
        }
        if (intent.getBooleanExtra(FROM_MEDIA_BUTTON, false)) {
            MediaButtonReceiver.completeWakefulIntent(intent);
        }
        sendBroadcast(new Intent(MainActivity.TAG_MINI_PLAYER).putExtra("show", true));
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (playerTimer != null) {
            playerTimer.cancel();
        }
        PLog.showLog(TAG, "onDestroy executed");
        try {
            unregisterReceiver(mMediaPlayerBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        stopMusicPlayer();
        PatariSingleton.getInstance().setMediaPlayer(null);
        songProgress = 0;
        currentTime = "00:00";
        totalTime = "00:00";
        SERVICE_IS_RUNNING = false;
        IS_SONG_LOADING = false;
        IS_PLAYING = false;
        IS_PROGRESS = false;
        IS_PAUSED = false;
        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
        songsList = null;
        sendBroadcast(new Intent(Utils.BR_ACTION_RESET_PLAYER_LAYOUT));
        sendBroadcast(new Intent(MainActivity.TAG_LOADING).putExtra("status", false));
        musicService = null;
        try {
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAudioManager.unregisterMediaButtonEventReceiver(mRemoteControlResponder);
        System.gc();
        sendBroadcast(new Intent(MainActivity.TAG_MINI_PLAYER).putExtra("show", false));
        mAudioManager.abandonAudioFocus(this);

        // Remove any sound effects
        final Intent audioEffectsIntent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectsIntent);

        // Reset Audio Ads Value
        previousPlayedAdIndex = 0;
        songPlayedInRow = 0;
    }

    public int getAudioSessionId() {
        synchronized (this) {
            return PatariSingleton.getInstance().getMediaPlayer().getAudioSessionId();
        }
    }

    private BroadcastReceiver mMediaPlayerBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            IS_PROGRESS = true;
            String playSession = "";
            if (intent.getExtras() != null && intent.hasExtra("playSessionID"))
                playSession = intent.getExtras().getString("playSessionID");
            // Reset notification Bar so that its controls can be active again
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Utils.BR_RESET_NOTIFICATION_ICONS:
                        FETCHING_RECOMMENDATIONS = false;
                        IS_SONG_LOADING = false;
                        IS_PAUSED = true;
                        showStatusBarNotification();
                        AppDialog.showToast(context, "No Recommendations Found");
                        stopSelf();
                        break;
                    case Utils.BR_ACTION_START_PLAYING_SELECTED_SONG:
                        getSongsFromDatabase();
                        currentPlayingSong = songsList.get(songsList.size() - 1);
                        songPlaybackRequest(currentPlayingSong, true);
                        break;
                    case Utils.BR_ACTION_START_PLAYING_ALL_ALBUM_SONG:
                        getSongsFromDatabase();
                        currentPlayingSong = songsList.get(0);
                        songPlaybackRequest(songsList.get(0), true);
                        break;
                    case Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST:
                        getSongsFromDatabase();
                        break;
                    case Utils.BR_ACTION_DB_DATA_SET_CHANGED:
                        getSongsFromDatabase();
                        break;
                    case Utils.BR_ACTION_START_PLAYING_QUEUE_AT_SPECIFIC_POSITION: {
                        getSongsFromDatabase();
                        currentPlayingSongIndex = intent.getIntExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY, 0);
                        currentPlayingSong = songsList.get(currentPlayingSongIndex);
                        songPlaybackRequest(currentPlayingSong, true);
                        intent.removeExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY);
                        break;
                    }
                }
                // /Head Set receiver
                if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            pauseCurrentSong();
                            Log.d(TAG, "Headset is unplugged");
                            break;
                        case 1:
                            Log.d(TAG, "Headset is plugged");
//                        playNexus = "play";
//                        resumeCurrentSong();
                            break;
                        default:
                            Log.d(TAG, "I have no idea what the headset state is :State " + state);
                    }
                }
                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction()) || BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equalsIgnoreCase(intent.getAction())) {
                    //Device has disconnected
                    pauseCurrentSong();
                }
            }
        }
    };

    public void initMusicPlayer() {
//        System.gc();
        // set player properties
        PatariSingleton.getInstance().getMediaPlayer().setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        PatariSingleton.getInstance().getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
        PatariSingleton.getInstance().getMediaPlayer().setOnPreparedListener(this);
        PatariSingleton.getInstance().getMediaPlayer().setOnCompletionListener(this);
        PatariSingleton.getInstance().getMediaPlayer().setOnErrorListener(this);
        PatariSingleton.getInstance().getMediaPlayer().setOnBufferingUpdateListener(this);
        try {
            if (wifiLock != null && wifiLock.isHeld()) {
                wifiLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            wifiLock = ((WifiManager) getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
            wifiLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IS_PROGRESS = false;
    }

    private void initBroadCastReciever() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.BR_RESET_NOTIFICATION_ICONS);
        filter.addAction(Utils.BR_ACTION_START_PLAYING_SELECTED_SONG);
        filter.addAction(Utils.BR_ACTION_START_PLAYING_ALL_ALBUM_SONG);
        filter.addAction(Utils.BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST);
        filter.addAction(Utils.BR_ACTION_START_PLAYING_ALREADY_ADDED);
        filter.addAction(Utils.BR_ACTION_DB_DATA_SET_CHANGED);
        filter.addAction("ACTION_STOP");
        // Home Screen Playlist/Album Broadcast
        filter.addAction(Utils.BR_ACTION_START_PLAYING_QUEUE_AT_SPECIFIC_POSITION);
        // Head Phone receiver;
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        // Bluetooth receiver
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        // registerReceiver
        registerReceiver(mMediaPlayerBroadcastReceiver, filter);
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // Incoming call and Music is Playing: Pause music
                if (IS_PLAYING) {
                    pauseCurrentSong();
                    MUSIC_INTERRUPTED = true;
                    PLog.showLog("Call State", "Incoming call and Music is Playing: Pause music");
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                // Not in call and Music was Interrupted: Resume music
                if (MUSIC_INTERRUPTED || IS_AUDIO_FOCUS_CHANGE) {
                    IS_AUDIO_FOCUS_CHANGE = false;
                    resumeCurrentSong();
                    PLog.showLog("Call State", " Not in call and Music was Interrupted: Resume music");
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                // A call is dialing, active or on hold and Music is Playing:
                // Pause music
                if (IS_PLAYING) {
                    pauseCurrentSong();
                    MUSIC_INTERRUPTED = true;
                    PLog.showLog("Call State", "A call is dialing and Music is Playing: Pause music");
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private void getSongsFromDatabase() {
        songsList = QueueTableDataManager.getInstance().getSongsListFromQueue(QueueTableDataManager.SONGS_QUEUE_TABLE);
    }

    boolean errorProcess = true;

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (MusicService.getInstance() != null || MusicService.SERVICE_IS_RUNNING) {
            IS_PROGRESS = false;
            PLog.showLog(TAG, "onError executed");
            PLog.showLog(TAG, "onError : " + String.format("Error(%s%s)", what, extra));
            // AppDialog.showToast(getApplicationContext(),
            // String.format("Error(%s%s)", what, extra));
            if (errorProcess) {
                errorProcess = false;
                Handler mHandler = new Handler();
                mHandler.postDelayed(new TimerTask() {
                    @Override
                    public void run() {
                        errorProcess = true;
                        if (PatariSingleton.getInstance().getMediaPlayer() != null && !PatariSingleton.getInstance().getMediaPlayer().isPlaying()) {
                            getSongsFromDatabase();
                            if (songsList.size() > 0) {
                                songPlaybackRequest(songsList.get(currentPlayingSongIndex), false);
                            }
                            if (timer != null)
                                timer.cancel();
                        }
                    }
                }, time);
            }
        }
        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        PatariSingleton.getInstance().setMediaPlayer(mp);
        PLog.showLog(TAG, "onPrepared executed : ");
        showStatusBarNotification();
        IS_PROGRESS = false;
        sendBroadcast(new Intent(MainActivity.TAG_LOADING).putExtra("status", false));
//                PLog.showLog(TAG, "onPrepared executed : ");
//                This might throw an illegal state exception. So in order to solve it the try block will try to prepare the song again for playback
        if (isOffline) {
            IS_PLAYING = true;
            mp.start();
            IS_PAUSED = false;
            Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED);
            sendBroadcast(updateSongsUIntent);
            // update UI songs
            if (musicUpdateListener != null)
                musicUpdateListener.onPlayerIconChanged(R.mipmap.ic_pause_black_48dp, AlbumFragment.PLAYING);
        } else {
            IS_PLAYING = true;
            mp.start();
            IS_PAUSED = false;
            Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED);
            sendBroadcast(updateSongsUIntent);
            // update UI songs
            if (musicUpdateListener != null)
                musicUpdateListener.onPlayerIconChanged(R.mipmap.ic_pause_black_48dp, AlbumFragment.PLAYING);
        }

        IS_SONG_LOADING = false;

        if (musicUpdateListener != null)
            musicUpdateListener.onMusicStarted(mp, currentPlayingSong);
        // Incase the thumbnail image was not loaded try again
        downloadAndShowSongThumbnail(currentPlayingSong.thumbnail);
        playerTimer = new Timer();
        playerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mp.isPlaying()) {
                    totalTime = Utils.getTotalSeekTime(mp.getDuration());
                    currentTime = Utils.getCurrentSeekTime(mp.getCurrentPosition());
                    songProgress = mp.getCurrentPosition();
                    songMaxProgress = mp.getDuration();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        try {
            if (mp.isPlaying()) {
//                Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED);
//                sendBroadcast(updateSongsUIntent);
                PLog.showLog("onBufferingUpdate", "" + percent);
//                PLog.showLog("CurrentPosition", "" + Utils.getCurrentSeekTime(PatariSingleton.getInstance().getMediaPlayer().getCurrentPosition()));
//                PLog.showLog(TAG, "Buffering percentage : " + percent);
//                PLog.showLog(TAG, "SongPlayed Percentage : " + MediaPlayerUtilities.getSongPlayedPercentage(mp));
//                PLog.showLog(TAG, "onBufferingUpdate percentage : " + mp.getCurrentPosition());
//                PLog.showLog(TAG, "onBufferingUpdate percentage : " + mp.getDuration());
                songProgress = mp.getCurrentPosition();
                songMaxProgress = mp.getDuration();
            }
        } catch (Exception e) {
//            PLog.showLog("onBufferingUpdate", e.getMessage());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        String repeatStatus = Prefs.getString(MusicService.this, Prefs.KEY_IS_SONG_REPEAT_STATUS, Prefs.REPEAT_OFF);
        if (repeatStatus.equals(Prefs.REPEAT_SONG)) {
            mp.seekTo(0);
            mp.start();
        } else {
            IS_PROGRESS = false;
            IS_PLAYING = false;
            IS_PAUSED = false;
            PLog.showLog(TAG, "onCompletion executed");
            if (musicUpdateListener != null)
                musicUpdateListener.onMusicEnded(mp);
            playNextSong();
            if (timer != null)
                timer.cancel();
        }
    }

    /**
     * Start the song playback Timer, We needed the timer because we would only like the song to switch if 1 second had passed since last playback request
     *
     * @param song    that will be played
     * @param isError analytics not send again if MediaPlayer has thrown an error
     */
    public void songPlaybackRequest(final Song song, boolean isError) {
        // Reset Player Value
        if (playerTimer != null) {
            playerTimer.cancel();
        }
        currentTime = "00:00";
        totalTime = "00:00";
        // Add Value to play Ads
        songPlayedInRow++;
        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

        update(song);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mAudioManager.isMusicActive()) {
                Intent i = new Intent(SERVICECMD);
                i.putExtra(CMDNAME, CMDSTOP);
                this.sendBroadcast(i);
            }
        }
        sendBroadcast(new Intent(MainActivity.TAG_LOADING).putExtra("status", true));
        MUSIC_INTERRUPTED = false;
        IS_SONG_LOADING = true;
        IS_PAUSED = false;
        File songFile = new File(MusicService.this.getExternalFilesDir("kashan") + File.separator + song.getSongID());
        if (songFile.exists()) {
            currentSongImageBitmap = defaultSongBitmap;
            pauseCurrentSong();
            isOffline = true;
            time = 1500;
            PLog.showLog("Play Saved Song", " Offline");
        } else {
            time = 2500;
            isOffline = false;
            startPlayingSong(song, null);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (IS_PLAYING)
                    IS_AUDIO_FOCUS_CHANGE = true;
                if (IS_PLAYING) {
                    pauseCurrentSong();// Pause your media player here
                }
                break;
        }
    }


    /**
     * Start the song playback,
     *
     * @param selectedSong id
     */
    public synchronized void startPlayingSong(final Song selectedSong, final String path) {
        try {
            if (path != null) {
                PatariSingleton.getInstance().getMediaPlayer().setDataSource(path);
                MusicService.this.sendBroadcast(new Intent(Utils.BR_ACTION_NEW_SONG_PLAYED));
                PatariSingleton.getInstance().getMediaPlayer().prepareAsync();
            } else if (Utils.isOnline(this)) {
                PatariSingleton.getInstance().getMediaPlayer().setDataSource(selectedSong.getAudio());
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                IS_PROGRESS = true;
                PatariSingleton.getInstance().getMediaPlayer().prepareAsync();
                MusicService.this.sendBroadcast(new Intent(Utils.BR_ACTION_NEW_SONG_PLAYED));
            } else {
                sendBroadcast(new Intent(MainActivity.TAG_LOADING).putExtra("status", false));
                stopSelf();
                AppDialog.showToast(getApplicationContext(), getString(R.string.internet_required_alert));
            }
        } catch (Exception e) {
            PLog.showLog("PlaySong()", e.getMessage());
        }
    }

    private void update(Song selectedSong) {
        try {
            downloadAndShowSongThumbnail(selectedSong.thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
            if (PatariSingleton.getInstance().getMediaPlayer().isPlaying()) {
                PatariSingleton.getInstance().getMediaPlayer().stop();
            }
            PatariSingleton.getInstance().getMediaPlayer().reset();
        }
        PatariSingleton.getInstance().setMediaPlayer(null);
        initMusicPlayer();
        if (musicUpdateListener != null)
            musicUpdateListener.onPrepared(selectedSong);
        IS_SONG_LOADING = true;
        IS_PAUSED = false;
        IS_PROGRESS = false;
    }

    private void downloadAndShowSongThumbnail(final String imageUrl) {
        Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                currentSongImageBitmap = defaultSongBitmap;
                showStatusBarNotification();
            }

            @Override
            public void onBitmapLoaded(Bitmap songImagebitmap, LoadedFrom arg1) {
                currentSongImageBitmap = songImagebitmap;
                showStatusBarNotification();

            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                currentSongImageBitmap = defaultSongBitmap;
                showStatusBarNotification();
            }
        };
        Picasso.with(MusicService.this).load(imageUrl).into(target);
    }

    private RemoteViews smallRemoteViews;
    private RemoteViews bigRemoteViews;
    private Notification notification;

    @SuppressLint("NewApi")
    private synchronized void showStatusBarNotification() {
        if (songsList != null && songsList.size() > 0) {
            NotificationCompat.Builder mBuilder;
            if (IS_PLAYING)
                mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.play_sea_green_icon).setOngoing(true).setVisibility(Notification.VISIBILITY_PUBLIC);
            else
                mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.play_sea_green_icon).setOngoing(false).setVisibility(Notification.VISIBILITY_PUBLIC);

            // set the button listeners
            setUpSmallRemoteViewNotification();
            setUpBigRemoteViewNotification();
            mBuilder.setContent(smallRemoteViews);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("From", "Service");
            resultIntent.putExtra("Song", currentPlayingSong);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            try {
                mBuilder.setCustomContentView(smallRemoteViews);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mBuilder.setCustomBigContentView(bigRemoteViews);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                notification = mBuilder.build();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                if (notification != null) {
//                    if (bigRemoteViews != null)
//                        notification.bigContentView = bigRemoteViews;
            try {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
//                startForeground(NOTIFICATION_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private synchronized void setUpBigRemoteViewNotification() {
        if (songsList != null && songsList.size() > 0) {
            try {
                bigRemoteViews = new RemoteViews(getPackageName(), R.layout.status_bar_big_notification_layout);
                bigRemoteViews.setImageViewBitmap(R.id.previousSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_fast_rewind_black_48dp));
                bigRemoteViews.setImageViewBitmap(R.id.nextSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_fast_forward_black_48dp));
                bigRemoteViews.setTextViewText(R.id.songNameTextView, currentPlayingSong.getName());
                bigRemoteViews.setTextViewText(R.id.artistNameTextView, currentPlayingSong.getSinger().getName());
                bigRemoteViews.setImageViewBitmap(R.id.songImageView, currentSongImageBitmap);
                // Handle Play Pause Button
                if (IS_PAUSED) {
                    bigRemoteViews.setImageViewBitmap(R.id.playSongImageView,
                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
                    // Play Song.
                    Intent playIntent = new Intent(this, MusicService.class);
                    playIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_PLAY_SONG);
                    PendingIntent pendingPlayIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PLAY_PAUSE,
                            playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    bigRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, pendingPlayIntent);

                } else {
                    bigRemoteViews.setImageViewBitmap(R.id.playSongImageView,
                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                    // Pause Song.
                    Intent pauseIntent = new Intent(this, MusicService.class);
                    pauseIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_PAUSE_SONG);
                    PendingIntent pendingPauseIntent = PendingIntent.getService(getApplicationContext(),
                            REQUEST_CODE_PLAY_PAUSE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    bigRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, pendingPauseIntent);

                }

                // destroy music service and end songs
                Intent endIntent = new Intent(this, MusicService.class);
                endIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_CLOSE_PLAYER_AND_SONG);
                PendingIntent pendingEndIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_STOP, endIntent,
                        0);
                bigRemoteViews.setOnClickPendingIntent(R.id.closeImageView, pendingEndIntent);

                // next Songs.
                Intent nextIntent = new Intent(this, MusicService.class);
                nextIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_FORWARD_SONG);
                PendingIntent pendingPlayNextIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_NEXT,
                        nextIntent, 0);
                bigRemoteViews.setOnClickPendingIntent(R.id.nextSongImageView, pendingPlayNextIntent);
                // rewind Songs.
                Intent rewindIntent = new Intent(this, MusicService.class);
                rewindIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_REWIND_SONG);
                PendingIntent pendingPlayPreviousIntent = PendingIntent.getService(getApplicationContext(),
                        REQUEST_CODE_PREVIOUS, rewindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                bigRemoteViews.setOnClickPendingIntent(R.id.previousSongImageView, pendingPlayPreviousIntent);
                if (FETCHING_RECOMMENDATIONS && IS_SONG_LOADING) {
                    bigRemoteViews.setOnClickPendingIntent(R.id.previousSongImageView, null);
                    bigRemoteViews.setOnClickPendingIntent(R.id.nextSongImageView, null);
                    bigRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, null);
                    bigRemoteViews.setOnClickPendingIntent(R.id.closeImageView, null);
                }
                if (Prefs.getInt(MusicService.this, Prefs.KEY_THEME, 0) == 0) {
                    smallRemoteViews.setInt(R.id.notificationControlMainLayout, "setBackgroundColor", ContextCompat.getColor(MusicService.this, R.color.player_background_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void setUpSmallRemoteViewNotification() {
        if (songsList != null && songsList.size() > 0) {
            try {
                smallRemoteViews = new RemoteViews(getPackageName(), R.layout.status_bar_notification_layout);
                smallRemoteViews.setImageViewBitmap(R.id.previousSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_fast_rewind_black_48dp));
                smallRemoteViews.setImageViewBitmap(R.id.nextSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_fast_forward_black_48dp));
                smallRemoteViews.setTextViewText(R.id.songNameTextView, currentPlayingSong.getName());
                smallRemoteViews.setTextViewText(R.id.artistNameTextView, currentPlayingSong.getSinger().getName());
                smallRemoteViews.setImageViewBitmap(R.id.songImageView, currentSongImageBitmap);
                // Handle Play Pause Button
                if (IS_PAUSED) {
                    smallRemoteViews.setImageViewBitmap(R.id.playSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_arrow_black_48dp));
                    // Play Song.
                    Intent playIntent = new Intent(this, MusicService.class);
                    playIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_PLAY_SONG);
                    PendingIntent pendingPlayIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PLAY_PAUSE, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    smallRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, pendingPlayIntent);
                } else {
                    smallRemoteViews.setImageViewBitmap(R.id.playSongImageView, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pause_black_48dp));
                    // Pause Song.
                    Intent pauseIntent = new Intent(this, MusicService.class);
                    pauseIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_PAUSE_SONG);
                    PendingIntent pendingStopIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_PLAY_PAUSE,
                            pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    smallRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, pendingStopIntent);
                }

//                     destroy music service and end songs
                Intent endIntent = new Intent(this, MusicService.class);
                endIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_CLOSE_PLAYER_AND_SONG);
                PendingIntent pendingEndIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_STOP, endIntent, 0);
                smallRemoteViews.setOnClickPendingIntent(R.id.closeImageView, pendingEndIntent);

                // next Songs.
                Intent nextIntent = new Intent(this, MusicService.class);
                nextIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_FORWARD_SONG);
                PendingIntent pendingPlayNextIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE_NEXT,
                        nextIntent, 0);
                smallRemoteViews.setOnClickPendingIntent(R.id.nextSongImageView, pendingPlayNextIntent);

                // rewind Songs.
                Intent rewindIntent = new Intent(this, MusicService.class);
                rewindIntent.putExtra(Utils.EXTRA_NOTIFICATION_KEY, Utils.EXTRA_NOTIFICATION_CONTROL_REWIND_SONG);
                PendingIntent pendingPlayPreviousIntent = PendingIntent.getService(getApplicationContext(),
                        REQUEST_CODE_PREVIOUS, rewindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                smallRemoteViews.setOnClickPendingIntent(R.id.previousSongImageView, pendingPlayPreviousIntent);
                if (FETCHING_RECOMMENDATIONS && IS_SONG_LOADING) {
                    smallRemoteViews.setOnClickPendingIntent(R.id.previousSongImageView, null);
                    smallRemoteViews.setOnClickPendingIntent(R.id.nextSongImageView, null);
                    smallRemoteViews.setOnClickPendingIntent(R.id.playSongImageView, null);
                    smallRemoteViews.setOnClickPendingIntent(R.id.closeImageView, null);
                }
                if (Prefs.getInt(MusicService.this, Prefs.KEY_THEME, 0) == 0) {
                    smallRemoteViews.setInt(R.id.notificationControlMainLayout, "setBackgroundColor", ContextCompat.getColor(MusicService.this, R.color.player_background_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public synchronized Song getPlayingSong() {
        if (songsList != null && songsList.size() > 0) {
            return currentPlayingSong;
        }
        return null;
    }

    public synchronized void pauseCurrentSong() {
        IS_PROGRESS = false;
        // pause Current song
//        if (PatariSingleton.getInstance().getMediaPlayer() != null && PatariSingleton.getInstance().getMediaPlayer().isPlaying()) {
        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
            if (PatariSingleton.getInstance().getMediaPlayer().isPlaying())
                PatariSingleton.getInstance().getMediaPlayer().pause();
            IS_SONG_LOADING = false;
            IS_PLAYING = false;
            IS_PAUSED = true;
            // update UI songs
            Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_STOPPED);
            sendBroadcast(updateSongsUIntent);
            if (musicUpdateListener != null)
                musicUpdateListener.onPlayerIconChanged(R.mipmap.ic_play_arrow_black_48dp, AlbumFragment.PAUSE);
            showStatusBarNotification();
        }
    }

    public synchronized void resumeCurrentSong() {
        IS_PROGRESS = false;
//        if (PatariSingleton.getInstance().getMediaPlayer() != null && !PatariSingleton.getInstance().getMediaPlayer().isPlaying()) {
        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDPAUSE);
                    this.sendBroadcast(i);
                }
            }
            PatariSingleton.getInstance().getMediaPlayer().start();
            MUSIC_INTERRUPTED = false;
            IS_PLAYING = true;
            IS_PAUSED = false;
            // update UI songs
            Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED);
            sendBroadcast(updateSongsUIntent);
            if (musicUpdateListener != null && PatariSingleton.getInstance().getMediaPlayer().isPlaying())
                musicUpdateListener.onPlayerIconChanged(R.mipmap.ic_pause_black_48dp, AlbumFragment.PLAYING);
            showStatusBarNotification();
        }
    }

    public synchronized void stopMusicPlayer() {
        IS_PROGRESS = false;
        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
            if (PatariSingleton.getInstance().getMediaPlayer().isPlaying())
                PatariSingleton.getInstance().getMediaPlayer().stop();
            PatariSingleton.getInstance().getMediaPlayer().reset();
        }
        IS_SONG_LOADING = true;
        IS_PLAYING = false;
        showStatusBarNotification();
    }

    public void playNextSong() {
        String repeatStatus = Prefs.getString(MusicService.this, Prefs.KEY_IS_SONG_REPEAT_STATUS, Prefs.REPEAT_OFF);
        if (!repeatStatus.equals(Prefs.REPEAT_SONG)) {
            IS_PROGRESS = false;
            stopMusicPlayer();
            IS_SONG_LOADING = true;
            IS_PLAYING = false;
            IS_PAUSED = false;
        }
        if (Prefs.getBoolean(getApplicationContext(), Prefs.KEY_IS_LIST_ON_SHUFLE, false)) {
            songsList = QueueTableDataManager.getInstance().getSongsListFromQueue(QueueTableDataManager.SONGS_QUEUE_TABLE);
            sendBroadcast(new Intent(Utils.BR_ACTION_DB_DATA_SET_CHANGED));
            if (songsList.size() > 0) {
                currentPlayingSong = songsList.get(0);
                songPlaybackRequest(currentPlayingSong, true);
            } else {
                if (musicUpdateListener != null) {
                    musicUpdateListener.onMusicEnded(PatariSingleton.getInstance().getMediaPlayer());
                }
                stopSelf();
            }
        } else {
            songsList = QueueTableDataManager.getInstance().getSongsListFromQueue(QueueTableDataManager.SONGS_QUEUE_TABLE);
            if (songsList.size() > (currentPlayingSongIndex + 1)) {
                currentPlayingSongIndex = currentPlayingSongIndex + 1;
            } else {
                currentPlayingSongIndex = 0;
            }
            // play next
            currentPlayingSong = songsList.get(currentPlayingSongIndex);
            songPlaybackRequest(currentPlayingSong, true);
            sendBroadcast(new Intent(Utils.BR_ACTION_DB_DATA_SET_CHANGED));
        }
        // update UI songs
        Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED);
        sendBroadcast(updateSongsUIntent);
    }

    private static final long DOUBLE_PRESS_INTERVAL = 1000; // in millis
    private long lastPressTime;

    private boolean mHasDoubleClicked = false;

    public void playPreviousSong() {
        if (currentPlayingSongIndex == 0) {
            PatariSingleton.getInstance().getMediaPlayer().seekTo(0);
            return;
        } else if (songsList.size() > 0 && currentPlayingSongIndex > 0) {
            currentPlayingSongIndex = currentPlayingSongIndex - 1;
            // Play the first song in the list
        } else {
            currentPlayingSongIndex = 0;
        }
        currentPlayingSong = songsList.get(currentPlayingSongIndex);
        songPlaybackRequest(currentPlayingSong, true);
        sendBroadcast(new Intent(Utils.BR_ACTION_DB_DATA_SET_CHANGED));

        // update UI songs
        Intent updateSongsUIntent = new Intent(Utils.BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED);
        sendBroadcast(updateSongsUIntent);
//        // Get current time in nano seconds.
//        long pressTime = System.currentTimeMillis();
//
//        // If double click...
//        if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
//            previousPlayedSong();
//            mHasDoubleClicked = true;
//        } else {     // If not double click....
//            mHasDoubleClicked = false;
//            Handler myHandler = new Handler() {
//                public void handleMessage(Message m) {
//                    if (!mHasDoubleClicked) {
//                        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
//                            if (PatariSingleton.getInstance().getMediaPlayer().getCurrentPosition() < 1500) {
//                                previousPlayedSong();
//                            } else {
//                                PatariSingleton.getInstance().getMediaPlayer().seekTo(0);
//                            }
//                        }
//                    }
//                }
//            };
//            Message m = new Message();
//            myHandler.sendMessageDelayed(m, DOUBLE_PRESS_INTERVAL);
//        }
//        // record the last time the menu button was pressed.
//        lastPressTime = pressTime;
    }

    private void previousPlayedSong() {
        if (PatariSingleton.getInstance().getMediaPlayer() != null) {
            PatariSingleton.getInstance().getMediaPlayer().seekTo(0);
        }
    }
}