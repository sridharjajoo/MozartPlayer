package com.example.sridhar123.mozart;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<Song> songArrayList;
    private MusicController controller;

    private boolean paused=false, playbackPaused=false;
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null)
        {
            playIntent= new Intent(this , MusicService.class);
            bindService(playIntent,musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;

            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        songArrayList = new ArrayList<Song>();
        getSongList();
        playMusic();

    }

    public void songPicked(View view){
        Log.v("MainActivity","Inside songPicked");
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.PlaySong();
        controller.show();
    }
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection()
    {

         @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songArrayList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList(){

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null);

        if(musicCursor!=null && musicCursor.moveToFirst()){

            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songArrayList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

        ListView listView = (ListView) findViewById(R.id.song_list);
        MusicAdapter musicAdapter = new MusicAdapter(this ,songArrayList);
        listView.setAdapter(musicAdapter);


    }
    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    private void setController()
    {
        Log.v("MainActivity","Inside setController");
        controller = new MusicController(this);
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
        controller.setPrevNextListeners(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            Log.v("MainActivity","Inside onClick of setController");

            playNext();
        }
        }, new View.OnClickListener() {
        @Override

        public void onClick(View view)
        {

                playPrev();
        }
        }
        );

    }

    public void playMusic()
    {
        setController();
        //controller.show(0);
        controller.requestFocus();
    }

    public void playNext()
    {
        Log.e("MainActivity","Inside playNext");
        musicSrv.playNext();
        controller.show(0);

    }

    public void playPrev()
    {
        Log.e("MainActivity","Inside playPrev");
        musicSrv.playPrev();
        controller.show(0);
    }

    @Override
    public void start() {
        Log.e("MainActivity","Inside start()");
        musicSrv.go();

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        Log.e("MainActivity","Inside getDuration");
        if(musicSrv!=null &&musicBound &&musicSrv.isPng())
        return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        Log.e("MainActivity","Inside getCurrentPOsition");
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else
            return 0;
        // /return 0;
    }



    @Override
    public void seekTo(int posn) {

        Log.e("MainActivity","Inside seekTo");
        musicSrv.seek(posn);
    }

    @Override
    public boolean isPlaying() {
        Log.e("MainActivity","Inside isPlaying");
        if(musicSrv!=null && musicBound)
        return musicSrv.isPng();
       else
            return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        Log.e("MainActivity","Inside canSeekBackward");
        return true;
    }

    @Override
    public boolean canSeekForward() {
        Log.e("MainActivity","Inside canSeekForward");
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }


    }
    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }
}
