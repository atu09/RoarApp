package com.atirek.alm.roarapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Alm on 7/14/2016.
 */
public class WidgetActivity extends AppWidgetProvider {

    public static RemoteViews remoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Constants.widgetIds = appWidgetIds;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent previousIntent = new Intent(context, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(context, 0, previousIntent, 0);

        Intent playIntent = new Intent(context, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(context, 0, playIntent, 0);

        Intent nextIntent = new Intent(context, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(context, 0, nextIntent, 0);

        //
        //
        //
        //
        //
        //

        remoteViews.setOnClickPendingIntent(R.id.btnPlayWidget, pplayIntent);

        remoteViews.setOnClickPendingIntent(R.id.btnForwardWidget, pnextIntent);

        remoteViews.setOnClickPendingIntent(R.id.btnBackwardWidget, ppreviousIntent);

        //
        //
        //
        //
        //
        //


        try {
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.arrayList.get(Constants.position).getProfileUrl());
            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            if (!bitmap.sameAs(emptyBitmap)) {
                remoteViews.setImageViewBitmap(R.id.civ_user_profileWidget, bitmap);
            } else {
                remoteViews.setImageViewResource(R.id.civ_user_profileWidget, R.drawable.logo);
            }
        } catch (Exception e) {
            remoteViews.setImageViewResource(R.id.civ_user_profileWidget, R.drawable.logo);
        }


/*
        if (Constants.arrayList.get(Constants.position).getProfileUrl().equals("") ||
                Constants.arrayList.get(Constants.position).getProfileUrl().isEmpty() ||
                Constants.arrayList.get(Constants.position).getProfileUrl().equals("null") ||
                Constants.arrayList.get(Constants.position).getProfileUrl().equals(null)) {

            remoteViews.setImageViewResource(R.id.civ_user_profileWidget, R.drawable.logo);

        } else {
            loadBitmap(Constants.arrayList.get(Constants.position).getProfileUrl(), context);
        }
*/

        if(!Constants.arrayList.isEmpty()) {
            if (Constants.arrayList.get(Constants.position).isBuffer() || Constants.arrayList.get(Constants.position).isPlaying()) {
                remoteViews.setImageViewResource(R.id.btnPlayWidget, R.drawable.home_pause);
            } else {
                remoteViews.setImageViewResource(R.id.btnPlayWidget, R.drawable.home_play);
            }
        } else {
            remoteViews.setImageViewResource(R.id.btnPlayWidget, R.drawable.home_play);
        }

        if(!Constants.arrayList.isEmpty()) {
            remoteViews.setTextViewText(R.id.tv_userNameWidget, Constants.arrayList.get(Constants.position).getArtistName());

            remoteViews.setTextViewText(R.id.tv_voiceTitleWidget, Constants.arrayList.get(Constants.position).getSongsName());

            remoteViews.setTextViewText(R.id.tv_categoryNameWidget, Constants.arrayList.get(Constants.position).getSongCategory());
        }

        //
        //
        //
        //
        //
        //
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        //Toast.makeText(context, "Widget Added", Toast.LENGTH_SHORT).show();


    }


    private static Target loadtarget;

    public void loadBitmap(String url, Context context) {

        if (loadtarget == null) loadtarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                remoteViews.setImageViewBitmap(R.id.civ_user_profileWidget, bitmap);



            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                remoteViews.setImageViewResource(R.id.civ_user_profileWidget, R.drawable.logo);

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(context).load(url).resize(50, 50).into(loadtarget);
    }


}
