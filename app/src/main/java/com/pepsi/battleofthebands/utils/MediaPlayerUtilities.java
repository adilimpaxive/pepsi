package com.pepsi.battleofthebands.utils;

import android.media.MediaPlayer;


public class MediaPlayerUtilities {
    private MediaPlayerUtilities() {
    }

    /**
     * Get the percentage of how much song is played
     *
     * @param mediaPlayer player
     * @return percentage as integer
     */
    public static int getSongPlayedPercentage(MediaPlayer mediaPlayer) {
        int soundPlayedPercentage = 0;
        try {
            if (mediaPlayer.isPlaying())
                soundPlayedPercentage = (100 * mediaPlayer.getCurrentPosition()) / mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soundPlayedPercentage;
    }
}
