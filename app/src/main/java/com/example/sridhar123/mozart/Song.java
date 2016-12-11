package com.example.sridhar123.mozart;

/**
 * Created by sridhar123 on 9/12/16.
 */

public class Song
{

    private long id;
    private String artists;
    private String title;

    public Song(long SongId , String SongArtists , String SongTitle)
    {
        id = SongId;
        artists= SongArtists;
        title =SongTitle;
    }


    public long getId(){
        return id;
    }

    public String getArtists(){
        return artists;
    }

    public String getTitle(){
        return title;
    }


}
