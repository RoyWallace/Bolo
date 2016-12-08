package com.ouj.bolo;

import android.media.MediaPlayer;

/**
 * Created by Administrator on 2016/11/26.
 */

public class OneMediaPlayer {

    private OneMediaPlayer() {
    }

    public static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            synchronized (OneMediaPlayer.class) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
            }
        }
        return mediaPlayer;
    }

    public static void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
