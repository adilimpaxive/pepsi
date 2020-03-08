package com.pepsi.battleofthebands.callback;

import android.media.MediaPlayer;

import com.pepsi.battleofthebands.entity.Song;


public interface OnMusicUpdateListener {
    void onMusicProgressUpdate(MediaPlayer mp);

    void onMusicEnded(MediaPlayer mp);

    void onMusicStarted(MediaPlayer mp, Song song);

    void onPrepared(Song playingSong);

    void onPlayerIconChanged(int drawableId, String buttonStatus);
}
