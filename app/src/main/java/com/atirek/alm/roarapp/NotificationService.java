package com.atirek.alm.roarapp;

/**
 * Created by Alm on 7/12/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service implements AudioManager.OnAudioFocusChangeListener {

    public static Notification status;
    private final String LOG_TAG = "NotificationService";
    public static RemoteViews remoteViews_big;
    public static RemoteViews remoteViews_small;
    public static NotificationService service;
    public static NotificationManager notificationManager;

    public void showNotification() {

        try {

            remoteViews_big = new RemoteViews(getPackageName(), R.layout.big_notification_layout);
            remoteViews_small = new RemoteViews(getPackageName(), R.layout.notification_layout);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //********************************************************************************


            Intent notificationIntent = new Intent(this, NewMediaPlayer.class);

            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent previousIntent = new Intent(this, NotificationService.class);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

            Intent playIntent = new Intent(this, NotificationService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

            Intent nextIntent = new Intent(this, NotificationService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

            Intent closeIntent = new Intent(this, NotificationService.class);
            closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);


            //********************************************************************************


            remoteViews_big.setOnClickPendingIntent(R.id.btnPlay09, pplayIntent);

            remoteViews_big.setOnClickPendingIntent(R.id.btnForward09, pnextIntent);

            remoteViews_big.setOnClickPendingIntent(R.id.btnBackward09, ppreviousIntent);

            remoteViews_big.setOnClickPendingIntent(R.id.btn_close_drawer09, pcloseIntent);


            remoteViews_small.setOnClickPendingIntent(R.id.btnPlay09, pplayIntent);

            remoteViews_small.setOnClickPendingIntent(R.id.btnForward09, pnextIntent);

            remoteViews_small.setOnClickPendingIntent(R.id.btnBackward09, ppreviousIntent);

            remoteViews_small.setOnClickPendingIntent(R.id.btn_close_drawer09, pcloseIntent);



            //********************************************************************************


            try {
                Bitmap bitmap = BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl());
                Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

                if (!bitmap.sameAs(emptyBitmap)) {
                    remoteViews_big.setImageViewBitmap(R.id.civ_user_profile09, bitmap);
                    remoteViews_small.setImageViewBitmap(R.id.civ_user_profile09, bitmap);
                } else {
                    remoteViews_big.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
                    remoteViews_small.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
                }
            } catch (Exception e) {
                remoteViews_big.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
                remoteViews_small.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
            }


/*
            if (Constants.arrayList.get(Constants.position).getProfileUrl().equals("") ||
                    Constants.arrayList.get(Constants.position).getProfileUrl().isEmpty() ||
                    Constants.arrayList.get(Constants.position).getProfileUrl().equals("null") ||
                    Constants.arrayList.get(Constants.position).getProfileUrl().equals(null)) {

                remoteViews_big.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);

            } else {
                loadBitmap(Constants.arrayList.get(Constants.position).getProfileUrl());
            }
*/

            remoteViews_big.setTextViewText(R.id.tv_userName09, Constants.arrayList.get(Constants.position).getArtistName());

            remoteViews_big.setTextViewText(R.id.tv_voiceTitle09, Constants.arrayList.get(Constants.position).getSongsName());

            remoteViews_big.setTextViewText(R.id.tv_categoryName09, Constants.arrayList.get(Constants.position).getSongCategory());


            remoteViews_small.setTextViewText(R.id.tv_userName09, Constants.arrayList.get(Constants.position).getArtistName());

            remoteViews_small.setTextViewText(R.id.tv_voiceTitle09, Constants.arrayList.get(Constants.position).getSongsName());

            remoteViews_small.setTextViewText(R.id.tv_categoryName09, Constants.arrayList.get(Constants.position).getSongCategory());


            //********************************************************************************


            remoteViews_big.setTextViewText(R.id.tv_currentDuration09, String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft),
                    TimeUnit.MILLISECONDS.toSeconds((long) Constants.timeLeft)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft))));

            remoteViews_small.setTextViewText(R.id.tv_currentDuration09, String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft),
                    TimeUnit.MILLISECONDS.toSeconds((long) Constants.timeLeft)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft))));


            if (Constants.arrayList.get(Constants.position).isBuffer() || Constants.arrayList.get(Constants.position).isPlaying()) {
                remoteViews_big.setImageViewResource(R.id.btnPlay09, R.drawable.home_pause);
                remoteViews_small.setImageViewResource(R.id.btnPlay09, R.drawable.home_pause);
                status = new Notification.Builder(this).setSmallIcon(android.R.drawable.ic_media_pause).build();
            } else {
                remoteViews_big.setImageViewResource(R.id.btnPlay09, R.drawable.home_play);
                remoteViews_small.setImageViewResource(R.id.btnPlay09, R.drawable.home_play);
                status = new Notification.Builder(this).setSmallIcon(android.R.drawable.ic_media_play).build();
            }

            status.bigContentView = remoteViews_big;
            status.contentView = remoteViews_small;

            if (Constants.isRunning) {
                status.contentIntent = pendingIntent;
            }

            Intent intentWidget = new Intent(this, WidgetActivity.class);
            intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, Constants.widgetIds);
            sendBroadcast(intentWidget);

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception>>", e.getMessage());
        }

    }

    public void updateNotification() {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl());
            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            if (!bitmap.sameAs(emptyBitmap)) {
                remoteViews_big.setImageViewBitmap(R.id.civ_user_profile09, bitmap);
                remoteViews_small.setImageViewBitmap(R.id.civ_user_profile09, bitmap);
            } else {
                remoteViews_big.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
                remoteViews_small.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
            }
        } catch (Exception e) {
            remoteViews_big.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
            remoteViews_small.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);
        }

        remoteViews_big.setTextViewText(R.id.tv_currentDuration09, String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft),
                TimeUnit.MILLISECONDS.toSeconds((long) Constants.timeLeft)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft))));

        remoteViews_small.setTextViewText(R.id.tv_currentDuration09, String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft),
                TimeUnit.MILLISECONDS.toSeconds((long) Constants.timeLeft)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) Constants.timeLeft))));


        if (Constants.arrayList.get(Constants.position).isBuffer() || Constants.arrayList.get(Constants.position).isPlaying()) {
            remoteViews_big.setImageViewResource(R.id.btnPlay09, R.drawable.home_pause);
            remoteViews_small.setImageViewResource(R.id.btnPlay09, R.drawable.home_pause);

        } else {
            remoteViews_big.setImageViewResource(R.id.btnPlay09, R.drawable.home_play);
            remoteViews_small.setImageViewResource(R.id.btnPlay09, R.drawable.home_play);
        }

        notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);

    }
    
    private static Target loadtarget;

    public void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                remoteViews_big.setImageViewBitmap(R.id.civ_user_profile09, bitmap);
                remoteViews_small.setImageViewBitmap(R.id.civ_user_profile09, bitmap);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                remoteViews_small.setImageViewResource(R.id.civ_user_profile09, R.drawable.logo);

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(this).load(url).resize(50, 50).into(loadtarget);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(closeIntent);

        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

                service = this;
                Constants.isService = true;

            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {

                saver();

                Constants.position = Constants.position - 1;
                Constants.isPlaying = true;
                stopMedia();

                if (Constants.position >= 0 && Constants.position <= Constants.arrayList.size() - 1) {
                    play(Constants.position);
                } else {
                    Constants.position = Constants.arrayList.size() - 1;
                    play(Constants.position);
                }

                Log.i(LOG_TAG, "Previous");

            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {

                saver();

                Log.d("Position>>>>", Constants.position + "");


                if (!Constants.arrayList.isEmpty() && !Constants.arrayList.get(Constants.position).isBuffer()) {


                    if (Constants.position < 0 || Constants.position > Constants.arrayList.size() - 1) {
                        Constants.position = 0;
                    }

                    Constants.arrayList.get(Constants.position).setPlaying(false);
                    Constants.arrayList.get(Constants.position).setBuffer(false);

                    if (Constants.isPlaying) {

                        if (Constants.isRunning) {
                            NewMediaPlayer.changeComplete();
                        }

                        Constants.isPlaying = false;

                        //CODE START - For Play & Stop from NotificationBar
/*
                        Constants.arrayList.get(Constants.position).setPlaying(false);
                        Constants.arrayList.get(Constants.position).setBuffer(false);
                        Constants.arrayList.get(Constants.position).setPaused(false);
                        Constants.timeLeft = Constants.mediaPlayer.getDuration();
                        stopMedia();
*/
                        //CODE END - For Play & Stop from NotificationBar

                        //CODE START - For Play & Pause from NotificationBar
                        Constants.isPlaying = false;
                        Constants.mediaPlayer.pause();
                        Constants.arrayList.get(Constants.position).setPaused(true);
                        //CODE END - For Play & Pause from NotificationBar

                        if (Constants.isRunning) {
                            NewMediaPlayer.songsAdapter.notifyDataSetChanged();
                            NewMediaPlayer.btn_play2.setImageResource(R.drawable.home_play);
                            NewMediaPlayer.handle_play.setImageResource(android.R.drawable.ic_media_play);
                        }
                        showNotification();
                        //stopForeground(true);

                    } else {
                        play(Constants.position);
                    }

                    Log.i(LOG_TAG, "Play");
                }

            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

                saver();

                Log.i(LOG_TAG, "Next");

                Constants.position = Constants.position + 1;
                Constants.isPlaying = true;
                stopMedia();

                if (Constants.position >= 0 && Constants.position <= Constants.arrayList.size() - 1) {
                    play(Constants.position);
                } else {
                    Constants.position = 0;
                    play(Constants.position);
                }


            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {

                Log.i(LOG_TAG, "Stopped");

                if (Constants.isRunning) {
                    NewMediaPlayer.changeComplete();
                }

                Constants.isPlaying = false;

                Constants.arrayList.get(Constants.position).setPlaying(false);
                Constants.arrayList.get(Constants.position).setBuffer(false);
                Constants.arrayList.get(Constants.position).setPaused(false);

                Constants.isClosed = true;
                //stopMedia();
                NewMediaPlayer.songProgress();
                Constants.mediaPlayer.reset();
                stopForeground(true);

            } else if (intent.getAction().equals(Constants.ACTION.NOTIFY_ACTION)) {

                showNotification();

            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.d("Exception>>>>", e.getMessage());

            stopMedia();
            stopForeground(true);
            //stopSelf();
        }

        return START_STICKY;
    }


    public void saver() {

        if (Constants.position != -1) {
            Constants.arrayList.get(Constants.position).setPlaying(false);
            Constants.arrayList.get(Constants.position).setBuffer(false);

            if (Constants.isRunning) {
                NewMediaPlayer.songsAdapter.notifyDataSetChanged();
            }

        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void stopMedia() {
        if (Constants.mediaPlayer != null) {
            Constants.mediaPlayer.stop();
            Constants.mediaPlayer.reset();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------


    public static void play(final int position) {

        NewMediaPlayer.audioManager.requestAudioFocus(service, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (Constants.arrayList.get(position).isPaused) {

            Constants.isPlaying = true;

            Constants.mediaPlayer.start();

            Constants.arrayList.get(position).setPaused(false);
            Constants.arrayList.get(position).setBuffer(false);
            Constants.arrayList.get(position).setPlaying(true);

            if (Constants.isRunning) {
                NewMediaPlayer.btn_play2.setImageResource(R.drawable.home_pause);
                NewMediaPlayer.handle_play.setImageResource(android.R.drawable.ic_media_pause);
                NewMediaPlayer.mHandler.post(NewMediaPlayer.UpdateSongTime);
                NewMediaPlayer.songsAdapter.notifyDataSetChanged();
            }

            service.showNotification();

            return;
        }

        if (Constants.isRunning) {
            NewMediaPlayer.changeInitial(position);
        } else {
            service.stopMedia();
        }

        Constants.isPlaying = true;

        Constants.arrayList.get(position).setPlaying(true);
        Constants.arrayList.get(position).setBuffer(true);
        Constants.arrayList.get(position).setPaused(false);

        service.showNotification();

        try {

            try {

                //Constants.mediaPlayer.setDataSource(Constants.baseUrl + Constants.arrayList.get(position).getSongsUrl());
                Constants.mediaPlayer.setDataSource(Constants.arrayList.get(position).getSongsUrl());

            } catch (IllegalArgumentException e) {
                Log.d("IllegalException>>", e.getMessage().toString());
                //Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Log.d("SecurityException>>", e.getMessage().toString());
                //Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Log.d("StateException>>", e.getMessage().toString());
                //Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.d("IOException>>", e.getMessage().toString());
                e.printStackTrace();
            }

            Constants.mediaPlayer.prepareAsync();

            //------------------------------------------------------------------------------------------------------------------------------------------------------------

            Constants.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {

                    mediaPlayer.start();
                    Constants.arrayList.get(position).setBuffer(false);
                    if (Constants.isRunning) {
                        NewMediaPlayer.changePrepare();
                    }
                    service.showNotification();

                }
            });


            //------------------------------------------------------------------------------------------------------------------------------------------------------------


            Constants.mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mediaPlayer) {

                    if (Constants.isRunning) {
                        NewMediaPlayer.changeComplete();
                    }

                    Constants.arrayList.get(position).setPlaying(false);
                    Constants.isPlaying = false;

                    if (!Constants.isClosed) {
                        service.showNotification();
                    } else {
                        Constants.isClosed = false;
                    }

                    if (Constants.isBatMode) {

                        Constants.position = position + 1;
                        if (Constants.position >= 0 && Constants.position <= Constants.arrayList.size() - 1) {
                            play(Constants.position);
                        } else {

                            Constants.position = 0;
                            play(Constants.position);
                        }

                    } else {
                        Constants.isService = false;

                        if (Constants.isRunning) {
                            NewMediaPlayer.songsAdapter.notifyDataSetChanged();
                        }
                        service.stopForeground(true);

                    }

                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }


        //------------------------------------------------------------------------------------------------------------------------------------------------------------


        try {
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl());
            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            if (!bitmap.sameAs(emptyBitmap)) {
                NewMediaPlayer.civ_drawerSongImage.setImageBitmap(BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl()));
                NewMediaPlayer.civ_songImage.setImageBitmap(BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl()));
            } else {
                NewMediaPlayer.civ_drawerSongImage.setImageResource(R.drawable.logo);
                NewMediaPlayer.civ_songImage.setImageResource(R.drawable.logo);
            }
        } catch (Exception e) {
            NewMediaPlayer.civ_drawerSongImage.setImageResource(R.drawable.logo);
            NewMediaPlayer.civ_songImage.setImageResource(R.drawable.logo);
        }

/*
        if (Constants.isRunning) {
            try {

                if (Constants.arrayList.get(Constants.position).getProfileUrl().equals("") ||
                        Constants.arrayList.get(Constants.position).getProfileUrl().isEmpty() ||
                        Constants.arrayList.get(Constants.position).getProfileUrl().equals("null") ||
                        Constants.arrayList.get(Constants.position).getProfileUrl().equals(null)) {

                    Picasso.with(service).load(R.drawable.logo).placeholder(R.drawable.logo).error(R.drawable.logo).resize(NewMediaPlayer.civ_drawerSongImage.getWidth(), NewMediaPlayer.civ_drawerSongImage.getHeight()).into(NewMediaPlayer.civ_drawerSongImage);

                } else {
                    Picasso.with(service).load(Constants.arrayList.get(position).getProfileUrl()).placeholder(R.drawable.logo).error(R.drawable.logo).resize(NewMediaPlayer.civ_drawerSongImage.getWidth(), NewMediaPlayer.civ_drawerSongImage.getHeight()).into(NewMediaPlayer.civ_drawerSongImage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
*/

    }


    @Override
    public void onAudioFocusChange(int focusChange) {

    }

}
