package com.example.dmitry.audioplayer;

/**
 * Created by Dmitry on 07.04.2016.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private String data;
    public Song(long songID, String songTitle, String songArtist, String songAlbum, String songData){
        id=songID;
        title=songTitle;
        artist=songArtist;
        album = songAlbum;
        data = songData;
    }

    public long getID(){return id;}

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }
}
