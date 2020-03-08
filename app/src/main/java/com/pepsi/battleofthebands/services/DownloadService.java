package com.pepsi.battleofthebands.services;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.db.DownloadedSongsTableDataManager;
import com.pepsi.battleofthebands.entity.DownloadedSong;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.offlinemedia.SongsDownloader;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Muhammad Kashan on 11/20/2015.
 */
public class DownloadService extends Service {
    public static final String TAG = "PepsiDownloadService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        downloadService = this;
    }

    private static DownloadService downloadService = null;

    public static DownloadService getInstance() {
        return downloadService;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null)
            if (intent.hasExtra(DownloadedSong.KEY)) {
                try {
                    registerReceiver(br_downloadClicked, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
                    registerReceiver(br_downloadCompleted, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    Song song = (Song) intent.getSerializableExtra(Song.KEY);
                    downloadSong(downloadService, song, null, true);
                    intent.removeExtra(DownloadedSong.KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return Service.START_NOT_STICKY;

    }

    private synchronized void downloadSong(Context mContext, final Song song, final DownloadedSong downloadedSong, boolean startDownload) {
        final ScheduledExecutorService scheduledTask = Executors.newSingleThreadScheduledExecutor();
        if (startDownload) {
            new File(mContext.getExternalFilesDir("kashan") + File.separator + song.getSongID());
            final DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            String downloadUrl = song.getAudio();
            Request dr = new Request(Uri.parse(downloadUrl));
            dr.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
            dr.setVisibleInDownloadsUi(false);
            dr.setTitle("Downloading " + song.getName());
            dr.setDescription("Powered by Pepsi");
            dr.setDestinationInExternalFilesDir(mContext, "kashan", song.getSongID());
            long requestId = 0;
            if (dm != null) {
                requestId = dm.enqueue(dr);
            }
            final DownloadedSong dSong = song.toDownloadedSong();
            dSong.setDownloadingId(requestId);
            dSong.setDownloadingStatus("Downloading");
            DownloadedSongsTableDataManager dataManager = DownloadedSongsTableDataManager.getInstance();
            dataManager.updateSongDetails(dSong);
            scheduledTask.scheduleWithFixedDelay(new Runnable() {

                @Override
                public void run() {
                    KeepUpdatingProgress(dSong, scheduledTask);
                }
            }, 0, 3, TimeUnit.SECONDS);
        } else {
            scheduledTask.scheduleWithFixedDelay(new Runnable() {

                @Override
                public void run() {
                    KeepUpdatingProgress(downloadedSong, scheduledTask);
                }
            }, 0, 3, TimeUnit.SECONDS);
        }
    }

    private synchronized void KeepUpdatingProgress(DownloadedSong song, ScheduledExecutorService scheduledTask) {
        DownloadManager dm = (DownloadManager) downloadService.getSystemService(Context.DOWNLOAD_SERVICE);
        Query query = new Query();
        query.setFilterById(song.getDownloadingId());
        Cursor c = null;
        if (dm != null) {
            c = dm.query(query);
            if (c.moveToFirst()) {
                int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                long totalSize = c.getInt(sizeIndex);
                long downloadedSize = c.getInt(downloadedIndex);
                int progress;
                if (totalSize != -1) {
                    progress = (int) (downloadedSize * 100.0 / totalSize);
                    // At this point you have the progress as a percentage.
                    song.setDownloadProgress(progress);
                    if (progress == 100) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            try {
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(new File(DownloadService.this.getExternalFilesDir("kashan") + File.separator + song.getSongID()));
                                mediaScanIntent.setData(contentUri);
                                DownloadService.this.sendBroadcast(mediaScanIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                DownloadService.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                String path = "file://" + new File(getExternalFilesDir("kashan") + "");
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(path)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        song.setDownloadingStatus(SongsDownloader.STATUS_DOWNLOADED);
                        DownloadedSongsTableDataManager dataManager = DownloadedSongsTableDataManager.getInstance();
                        dataManager.updateSongDetails(song);
                        scheduledTask.shutdownNow();
                    } else if (progress > 80) {
                        song.setDownloadingStatus(SongsDownloader.STATUS_DOWNLOADED);
                        DownloadedSongsTableDataManager dataManager = DownloadedSongsTableDataManager.getInstance();
                        dataManager.updateSongDetails(song);
                    }
                }
            }
        }
        if (c != null) {
            c.close();
        }
    }

    private BroadcastReceiver br_downloadCompleted = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadedSongsTableDataManager dataManager = DownloadedSongsTableDataManager.getInstance();
            if (dataManager.getAllSongs(SongsDownloader.STATUS_QUEUE).size() > 0) {
                Song song = dataManager.getAllSongs(SongsDownloader.STATUS_QUEUE).get(0);
                SongsDownloader.downloadSong(context, song, SongsDownloader.STATUS_DOWNLOADING, false);
            } else if (dataManager.getAllRemainingSongs().size() == 0) {
                downloadService.stopSelf();
            }
        }
    };
    private BroadcastReceiver br_downloadClicked = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra(TAG, true);
            context.startActivity(i);
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create a intent that you want to start again..
        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000, pendingIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        downloadService = null;
        try {
            if (br_downloadCompleted != null)
                unregisterReceiver(br_downloadCompleted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (br_downloadClicked != null)
                unregisterReceiver(br_downloadClicked);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
