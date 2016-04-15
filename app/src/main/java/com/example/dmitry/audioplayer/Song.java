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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;

        Song song = (Song) o;


        if (data != null ? !data.equals(song.data) : song.data != null) return false;

        if (title != null ? !title.equals(song.title) : song.title != null) return false;
        if (artist != null ? !artist.equals(song.artist) : song.artist != null) return false;
        if (album != null ? !album.equals(song.album) : song.album != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = 1;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
