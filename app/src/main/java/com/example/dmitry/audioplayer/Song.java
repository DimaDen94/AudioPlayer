package com.example.dmitry.audioplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dmitry on 07.04.2016.
 */
public class Song {
    private long time;
    private Date dateTime;
    private String title;
    private String artist;
    private String album;
    private String data;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    public Song(long songID, String songTitle, String songArtist, String songAlbum, String songData) {
        time = songID;
        title = songTitle;
        artist = songArtist;
        album = songAlbum;
        data = songData;
        dateTime = new Date(time);
    }

    public String getTime() {
        return format.format(dateTime);
    }
    public long getTimeInSecond() {
        return time/100;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }
}
