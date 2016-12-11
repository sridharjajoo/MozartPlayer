package com.example.sridhar123.mozart;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by sridhar123 on 9/12/16.
 */

public class MusicAdapter extends BaseAdapter {

    private ArrayList<Song> songs_in_adapter;
    private LayoutInflater songsInflate;

    public MusicAdapter(Context context , ArrayList<Song> songs_from_mainactivity) {

        songs_in_adapter = songs_from_mainactivity;
        songsInflate = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return songs_in_adapter.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {

        LinearLayout songLay = (LinearLayout) songsInflate.inflate(R.layout.song_item,parent,false);
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);

        //get song using position
        Song currSong = songs_in_adapter.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtists());
        //set position as tag
        songLay.setTag(position);
        return songLay;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
