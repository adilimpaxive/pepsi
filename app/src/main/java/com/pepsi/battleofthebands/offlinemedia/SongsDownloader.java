package com.pepsi.battleofthebands.offlinemedia;

import android.content.Context;
import android.content.Intent;

import com.pepsi.battleofthebands.db.DownloadedSongsTableDataManager;
import com.pepsi.battleofthebands.entity.DownloadedSong;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.services.DownloadService;

/**
 * Created by Muhammad Kashan on 11/20/2015.
 */
public class SongsDownloader {
    public static final String STATUS_DOWNLOADING = "Downloading";
    public static final String STATUS_QUEUE = "Queued";
    public static final String STATUS_DOWNLOADED = "Downloaded";

    public static void downloadSong(Context mContext, Song song, String status, boolean isInset) {
        final DownloadedSong downloadedSong = song.toDownloadedSong();
        downloadedSong.setDownloadingStatus(status);
        DownloadedSongsTableDataManager downloadSongsManager = DownloadedSongsTableDataManager.getInstance();
        if (isInset) {
            downloadSongsManager.insertSong(downloadedSong);
        } else {
            downloadSongsManager.updateSongDetails(downloadedSong);
        }
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(Song.KEY, song);
        mContext.startService(intent);
    }

    public static void queuedSong(Context mContext, Song song, String status) {
        final DownloadedSong dSong = song.toDownloadedSong();
        dSong.setDownloadingStatus(status);
        DownloadedSongsTableDataManager downloadSongsManager = DownloadedSongsTableDataManager.getInstance();
        int result = downloadSongsManager.insertSong(dSong);
    }
}
