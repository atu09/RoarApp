package com.atirek.alm.roarapp;

/**
 * Created by Alm on 7/12/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class Constants {

    public static MediaPlayer mediaPlayer;
    public static ArrayList<SongRow> arrayList = new ArrayList<>();
    public static int position = -1;
    //public static String baseUrl = null;
    public static boolean isRunning = false;
    public static boolean isPlaying = false;
    public static boolean isClosed = false;
    public static boolean isService = false;
    public static boolean isBatMode = false;
    public static int[] widgetIds;
    public static double timeLeft;

    public static void setIsBatMode(boolean isBatMode) {
        Constants.isBatMode = isBatMode;
        if (isBatMode) {
            NewMediaPlayer.iv_open_drawer.setColorFilter(ContextCompat.getColor(NewMediaPlayer.context, R.color.white));
        } else {
            NewMediaPlayer.iv_open_drawer.clearColorFilter();
        }
    }

    public interface ACTION {
        String MAIN_ACTION = "com.atirek.alm.roarapp.action.main";
        String NOTIFY_ACTION = "com.atirek.alm.roarapp.action.notify";
        String PREV_ACTION = "com.atirek.alm.roarapp.action.prev";
        String PLAY_ACTION = "com.atirek.alm.roarapp.action.play";
        String NEXT_ACTION = "com.atirek.alm.roarapp.action.next";
        String STARTFOREGROUND_ACTION = "com.atirek.alm.roarapp.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.atirek.alm.roarapp.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public static void callService(Context context, String action) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

}
