package com.example.sridhar123.mozart;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sridhar123 on 9/12/16.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle=false;
    private Random rand;
    private MusicController controller1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.v("MusicService","Inside onBind");
        return  musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        Log.v("MusicService","Inside onPrepared");
        mediaPlayer.start();


        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.android_music_player_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void onCreate(){

     super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        rand=new Random();
        initMusicPlayer();


    }


    public void setList(ArrayList<Song> theSongs){
        Log.v("MusicService","Inside setList");
        songs=theSongs;
    }

    public class MusicBinder extends Binder
    {
        MusicService getService() {
            return MusicService.this;
        }
    }


    public void initMusicPlayer()
    {
            player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);
    }


    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void PlaySong() {

        player.reset();
        Song playSong = songs.get(songPosn);
        long currSong= playSong.getId();
        songTitle = playSong.getArtists();

        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        }

        catch(Exception e)
        {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();


    }

    public void setShuffle(){
        if(shuffle)

            shuffle=false;

        else
            shuffle=true;
    }


    public int getPosn()
    {
        return player.getCurrentPosition();
    }

    public int getDur()
    {
        return player.getDuration();
    }

    public boolean isPng()
    {
        return player.isPlaying();
    }

    public void pausePlayer()
    {
        player.pause();
    }

    public void seek(int posn)
    {
        player.seekTo(posn);
    }

    public void go()
    {
        player.start();
    }

    public void playPrev()
    {
        songPosn--;
        if(songPosn==0) songPosn=songs.size()-1;
        PlaySong();
    }

    //skip to next
    public void playNext(){

            if(shuffle)
            {
                int newSong = songPosn;
                while(newSong==songPosn)
                {
                    newSong=rand.nextInt(songs.size());
                }
                songPosn=newSong;
            }
            else
            {
                songPosn++;

                if(songPosn==songs.size())
                songPosn=0;
            }
            PlaySong();
        }

    }
