package com.atirek.alm.roarapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atirek.alm.roarapp.CustomClasses.MyListView;
import com.atirek.alm.roarapp.CustomClasses.MySlidingDrawer;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alm on 6/30/2016.
 */


public class NewMediaPlayer extends AppCompatActivity implements ImageButton.OnClickListener {

    public static Handler mHandler = new Handler();

    public static MyListView listView;

    public static SongsAdapter_New songsAdapter;

    String[] projections = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};
    String order = MediaStore.Audio.Media.TITLE + " ASC";
    String[] selectionArgs = {"%mp3"};
    String selection = MediaStore.Audio.Media.DATA + " like ?";

    public static ImageButton btn_play2, btn_next2, btn_prev2;
    public static TextView tv_songName;
    public static TextView tv_voiceTotalDuration;
    public static TextView tv_currentVoiceProgress;
    public static TextView tv_songCategory;
    public static TextView tv_userName;
    public static ImageButton btn_close_drawer;
    public static CircleImageView civ_userProfile;

    public static LinearLayout linearLayout_back;

    public static double startTime = 0;

    public static double finalTime = 0;

    public static double timeRemaining = 0;

    static MySlidingDrawer mySlidingDrawer;
    static CircleImageView civ_open_drawer;

    public static ViewHolder holder;

    Thread updateThread;

    public static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        context = this;

        if (Constants.mediaPlayer == null) {
            Constants.mediaPlayer = new MediaPlayer();
            Constants.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        listView = (MyListView) findViewById(R.id.listView_songs);
        mySlidingDrawer = (MySlidingDrawer) findViewById(R.id.slidingDrawerNew);

        civ_open_drawer = (CircleImageView) findViewById(R.id.civ_OpenDrawer);

        btn_play2 = (ImageButton) findViewById(R.id.btnPlay2);
        btn_next2 = (ImageButton) findViewById(R.id.btnForward2);
        btn_prev2 = (ImageButton) findViewById(R.id.btnBackward2);
        tv_songName = (TextView) findViewById(R.id.tv_voiceTitle);
        tv_voiceTotalDuration = (TextView) findViewById(R.id.tv_voiceDuration);
        btn_close_drawer = (ImageButton) findViewById(R.id.btn_close_drawer);

        civ_userProfile = (CircleImageView) findViewById(R.id.civ_user_profile);
        linearLayout_back = (LinearLayout) findViewById(R.id.content_background);
        tv_currentVoiceProgress = (TextView) findViewById(R.id.tv_currentVoiceProgress);
        tv_songCategory = (TextView) findViewById(R.id.tv_categoryName);
        tv_userName = (TextView) findViewById(R.id.tv_userName);


        btn_play2.setOnClickListener(this);
        btn_next2.setOnClickListener(this);
        btn_prev2.setOnClickListener(this);

        btn_close_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.isCarMode) {
                    stopService();
                    mySlidingDrawer.close();
                    Constants.isCarMode = false;
                }
            }
        });

        civ_open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constants.isCarMode) {
                    stopService();
                    mySlidingDrawer.close();
                    Constants.isCarMode = false;
                } else {
                    mySlidingDrawer.open();
                    Constants.isCarMode = true;
                }

            }
        });

        songsAdapter = new SongsAdapter_New();
        listView.setAdapter(songsAdapter);

        requestPermission(1);

    }

    public void startService() {
        Intent serviceIntent = new Intent(NewMediaPlayer.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    public static void notifyService() {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.NOTIFY_ACTION);
        context.startService(serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(NewMediaPlayer.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(serviceIntent);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (Constants.arrayList.isEmpty()) {
            return;
        }

        if (Constants.pos < 0 || Constants.pos > Constants.arrayList.size() - 1) {
            Constants.pos = 0;
        }

        Constants.arrayList.get(Constants.pos).setPlaying(false);
        Constants.arrayList.get(Constants.pos).setBuffer(false);

        switch (id) {
            case R.id.btnPlay2:
                if (Constants.isPlaying) {

                    Constants.isPlaying = false;
                    Constants.isClicked = true;
                    Constants.mediaPlayer.pause();

                    Constants.arrayList.get(Constants.pos).setPaused(true);

                    btn_play2.setImageResource(R.drawable.home_play);
                    mHandler.removeCallbacks(UpdateSongTime);
                    songsAdapter.notifyDataSetChanged();
                    notifyService();


                } else {
                    NotificationService.play(Constants.pos, true);
                }
                break;

            case R.id.btnForward2:
            case R.id.btnBackward2:

                if (id == R.id.btnForward2) {
                    Constants.pos = Constants.pos + 1;
                    if (Constants.pos > (Constants.arrayList.size() - 1)) {
                        Constants.pos = 0;
                    }

                } else if (id == R.id.btnBackward2) {
                    Constants.pos = Constants.pos - 1;

                    if (Constants.pos < 0) {
                        Constants.pos = Constants.arrayList.size() - 1;
                    }

                }

                notifyService();
                NotificationService.play(Constants.pos, true);

                break;
            default:
                break;
        }

    }

    public static void changeInitial(int position) {

        mHandler.removeCallbacks(UpdateSongTime);
        Constants.mediaPlayer.stop();
        Constants.mediaPlayer.reset();

        btn_play2.setEnabled(false);
        linearLayout_back.setBackgroundColor(Color.parseColor(Constants.arrayList.get(position).getColorCode()));
        btn_play2.setImageResource(R.drawable.home_pause);
        tv_songName.setText(Constants.arrayList.get(position).getSongsName());
        tv_songCategory.setText(Constants.arrayList.get(position).getSongCategory());
        tv_userName.setText(Constants.arrayList.get(position).getUserName());
        songsAdapter.notifyDataSetChanged();
    }

    public static void changePrepare() {

        finalTime = Constants.mediaPlayer.getDuration();
        startTime = Constants.mediaPlayer.getCurrentPosition();
        btn_play2.setEnabled(true);

        songProgress();
        songsAdapter.notifyDataSetChanged();
        mHandler.post(UpdateSongTime);

    }

    public static void changeComplete() {

        mHandler.removeCallbacks(UpdateSongTime);
        btn_play2.setImageResource(R.drawable.home_play);
        btn_play2.setEnabled(true);
        songsAdapter.notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView imageButtonPlay;
        TextView songName, songUrl, songTotalDuration, songCurrentDuration;
        CircularProgressView circularProgressView;
        LinearLayout layout;

    }

    public static void songProgress() {

        tv_voiceTotalDuration.setText(String.format("(%02d:%02d)",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

        tv_currentVoiceProgress.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

    }

    public static Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = Constants.mediaPlayer.getCurrentPosition();
            timeRemaining = finalTime - startTime;
            Constants.timeLeft = timeRemaining;

            tv_currentVoiceProgress.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            songsAdapter.updateView(Constants.pos);
            notifyService();

            mHandler.postDelayed(this, 100);
        }
    };

    private void startAnimationThreadStuff(long delay, final CircularProgressView progressView) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        // Start animation after a delay so there's no missed frames while the app loads up
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!progressView.isIndeterminate()) {
                    progressView.setProgress(0f);
                    // Run thread to update progress every quarter second until full
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
                                // Must set progress in UI thread
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setProgress(progressView.getProgress() + 20);
                                    }
                                });
                                SystemClock.sleep(50);
                            }
                        }
                    });
                    updateThread.start();
                }
                // Alias for resetAnimation, it's all the same
                progressView.startAnimation();
            }
        }, delay);
    }

    public class SongsAdapter_New extends BaseAdapter {

        LayoutInflater inflater = getLayoutInflater();

        @Override
        public int getCount() {
            return Constants.arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return Constants.arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void updateView(int index) {
            View view = listView.getChildAt(index - listView.getFirstVisiblePosition());

            if (view == null)
                return;

            ((TextView) view.findViewById(R.id.songCurrentDuration)).setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {

            holder = new ViewHolder();
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.song_listing_item, null);

                holder.songName = (TextView) convertView.findViewById(R.id.songName);
                holder.songUrl = (TextView) convertView.findViewById(R.id.songUrl);
                holder.imageButtonPlay = (ImageView) convertView.findViewById(R.id.play);
                holder.songTotalDuration = (TextView) convertView.findViewById(R.id.songTotalDuration);
                holder.songCurrentDuration = (TextView) convertView.findViewById(R.id.songCurrentDuration);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.listing_item_back);
                holder.circularProgressView = (CircularProgressView) convertView.findViewById(R.id.circularProgress);
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d("viewPos>>", i + "");

            holder.imageButtonPlay.bringToFront();
            final SongRow songRow = Constants.arrayList.get(i);

            holder.songName.setText(songRow.getSongsName());
            holder.songUrl.setText(songRow.getSongsUrl());

            double duration = Double.parseDouble(songRow.getSongDuration());
            holder.songTotalDuration.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) duration),
                    TimeUnit.MILLISECONDS.toSeconds((long) duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration))));

            tv_voiceTotalDuration.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) duration),
                    TimeUnit.MILLISECONDS.toSeconds((long) duration)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration))));
            holder.layout.setBackgroundColor(Color.parseColor(songRow.getColorCode()));
            holder.imageButtonPlay.bringToFront();

            //---------------------------------------------------------------------------------------------------------------------------------------------------------

            if (Constants.pos == i && songRow.isPlaying() && Constants.arrayList.get(Constants.pos) == songRow) {

                holder.imageButtonPlay.setImageResource(R.drawable.home_pause);
                btn_play2.setImageResource(R.drawable.home_pause);

                holder.songCurrentDuration.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));


                if (songRow.isBuffer()) {
                    holder.circularProgressView.setVisibility(View.VISIBLE);
                    holder.circularProgressView.setIndeterminate(true);
                    startAnimationThreadStuff(0, holder.circularProgressView);
                    holder.imageButtonPlay.setEnabled(false);

                } else {
                    if (holder.circularProgressView.getVisibility() == View.VISIBLE) {
                        holder.circularProgressView.stopAnimation();
                        holder.circularProgressView.setVisibility(View.INVISIBLE);
                        holder.imageButtonPlay.setEnabled(true);

                    }

                }

            } else {

                holder.imageButtonPlay.setImageResource(R.drawable.home_play);
                holder.imageButtonPlay.setEnabled(true);

                if (!songRow.isPlaying() && songRow.isPaused() && i == Constants.pos) {
                    btn_play2.setImageResource(R.drawable.home_play);
                    holder.songCurrentDuration.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                            TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

                }

                if (!songRow.isPlaying() && !songRow.isPaused()) {
                    holder.songCurrentDuration.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) duration),
                            TimeUnit.MILLISECONDS.toSeconds((long) duration)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration))));
                }

                if (holder.circularProgressView.getVisibility() == View.VISIBLE) {
                    holder.circularProgressView.stopAnimation();
                    holder.circularProgressView.setVisibility(View.INVISIBLE);
                }


            }


            //---------------------------------------------------------------------------------------------------------------------------------------------------------


            holder.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (i == Constants.pos && Constants.arrayList.get(Constants.pos) == songRow) {

                        if (Constants.arrayList.get(Constants.pos).isPaused() || !Constants.arrayList.get(Constants.pos).isPlaying()) {

                            NotificationService.play(Constants.pos, false);

                        } else {

                            Constants.isPlaying = false;
                            Constants.isClicked = true;

                            Constants.mediaPlayer.pause();

                            mHandler.removeCallbacks(UpdateSongTime);

                            Constants.arrayList.get(Constants.pos).setPaused(true);
                            Constants.arrayList.get(Constants.pos).setBuffer(false);
                            Constants.arrayList.get(Constants.pos).setPlaying(false);


                            notifyDataSetChanged();
                            notifyService();

                        }

                    } else {

                        if (Constants.pos != -1) {
                            Constants.isPlaying = false;
                            Constants.isClicked = false;
                            Constants.arrayList.get(Constants.pos).setPaused(false);
                            Constants.arrayList.get(Constants.pos).setPlaying(false);
                            Constants.arrayList.get(Constants.pos).setBuffer(false);
                            notifyDataSetChanged();
                        }

                        Constants.pos = i;
                        NotificationService.play(Constants.pos, false);
                        notifyService();

                    }
                }
            });

            return convertView;
        }


    }

    @Override
    public void onBackPressed() {

        if (Constants.isCarMode) {
            stopService();
            mySlidingDrawer.close();
            Constants.isCarMode = false;
        } else {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.isRunning = true;
        if (Constants.pos != -1 && Constants.arrayList.get(Constants.pos).isPlaying() && !Constants.arrayList.get(Constants.pos).isPaused()) {
            linearLayout_back.setBackgroundColor(Color.parseColor(Constants.arrayList.get(Constants.pos).getColorCode()));
            btn_play2.setImageResource(R.drawable.home_pause);
            tv_songName.setText(Constants.arrayList.get(Constants.pos).getSongsName());
            tv_songCategory.setText(Constants.arrayList.get(Constants.pos).getSongCategory());
            tv_userName.setText(Constants.arrayList.get(Constants.pos).getUserName());

            finalTime = Constants.mediaPlayer.getDuration();
            songProgress();
            mHandler.post(UpdateSongTime);


        } else if (Constants.pos != -1 && Constants.arrayList.get(Constants.pos).isPlaying() && Constants.arrayList.get(Constants.pos).isPaused()) {
            linearLayout_back.setBackgroundColor(Color.parseColor(Constants.arrayList.get(Constants.pos).getColorCode()));
            btn_play2.setImageResource(R.drawable.home_play);
            tv_songName.setText(Constants.arrayList.get(Constants.pos).getSongsName());
            tv_songCategory.setText(Constants.arrayList.get(Constants.pos).getSongCategory());
            tv_userName.setText(Constants.arrayList.get(Constants.pos).getUserName());

            finalTime = Constants.mediaPlayer.getDuration();
            songProgress();
            mHandler.post(UpdateSongTime);

        }

        songsAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();

        Constants.isRunning = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Constants.isRunning = true;

    }

    private void requestPermission(int permit) {

        switch (permit) {
            case 1:
                ActivityCompat.requestPermissions(NewMediaPlayer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                break;
            case 2:
                ActivityCompat.requestPermissions(NewMediaPlayer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                break;
        }

    }

    public void loadData() {

        if (!Constants.isService) {
            try {

                Constants.arrayList.clear();

                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projections, selection, selectionArgs, order);

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    String songsUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String songsName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String profileUrl = getAlbumArt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    String songDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String songCategory = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String colorCode = "#2473aa";

                    SongRow songRow = new SongRow(songsUrl, songsName, profileUrl, songDuration, userName, songCategory, colorCode, false, false, false);
                    Constants.arrayList.add(songRow);
                }

                songsAdapter.notifyDataSetChanged();

                startService();

            } catch (Exception e) {
                Toast.makeText(NewMediaPlayer.this, "Error" + e, Toast.LENGTH_SHORT).show();

            }
        }

    }

    public String getAlbumArt(String albumId) {

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null);

        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }

        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermission(2);
                } else {
                    Toast.makeText(NewMediaPlayer.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    requestPermission(2);
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loadData();

                } else {
                    Toast.makeText(NewMediaPlayer.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    requestPermission(1);
                }
                break;
        }

    }
}


class SongRow {

    String songsUrl;
    String songsName;
    String profileUrl;
    String songDuration;
    String userName;
    String songCategory;
    String colorCode;
    boolean isPlaying;
    boolean isBuffer;
    boolean isPaused;

    public SongRow(String songsUrl, String songsName, String profileUrl, String songDuration, String userName, String songCategory, String colorCode, boolean isPlaying, boolean isBuffer, boolean isPaused) {
        this.songsUrl = songsUrl;
        this.songsName = songsName;
        this.profileUrl = profileUrl;
        this.songDuration = songDuration;
        this.userName = userName;
        this.songCategory = songCategory;
        this.colorCode = colorCode;
        this.isPlaying = isPlaying;
        this.isBuffer = isBuffer;
        this.isPaused = isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isBuffer() {
        return isBuffer;
    }

    public void setBuffer(boolean buffer) {
        isBuffer = buffer;
    }

    public String getSongsUrl() {
        return songsUrl;
    }

    public void setSongsUrl(String songsUrl) {
        this.songsUrl = songsUrl;
    }

    public String getSongsName() {
        return songsName;
    }

    public void setSongsName(String songsName) {
        this.songsName = songsName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}