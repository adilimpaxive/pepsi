package com.pepsi.battleofthebands.app;

import android.media.MediaPlayer;


import java.util.ArrayList;


public class PatariSingleton {

    private static PatariSingleton instance;
    private MediaPlayer mediaPlayer;

    private PatariSingleton() {
    }

    public static PatariSingleton getInstance() {
        if (instance == null)
            instance = new PatariSingleton();
        return instance;

    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        System.gc();
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
