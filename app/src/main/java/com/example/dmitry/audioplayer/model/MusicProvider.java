package com.example.dmitry.audioplayer.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.dmitry.audioplayer.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dmitry on 11.04.2016.
 */
public class MusicProvider {

    private Context context;
    private ArrayList<Song> songList = new ArrayList<>();
    public MusicProvider(Context context) {
        this.context = context;
        getSongList();
    }

    private void getSongList() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);
        //iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int dataColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisDuration= musicCursor.getLong(idColumn);
                String thisData = musicCursor.getString(dataColumn);

                songList.add(new Song(thisDuration, thisTitle, thisArtist,thisAlbum,thisData));
            }
            while (musicCursor.moveToNext());
        }
    }
    public ArrayList<Song> getSongsSortedByTitle(){
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByArtist(){
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getArtist().compareTo(b.getArtist());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByAlbum(){
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getAlbum().compareTo(b.getAlbum());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByDuration(){
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                if(a.getTimeInSecond() > b.getTimeInSecond()) {
                    return 1;
                }
                else if(a.getTimeInSecond() < b.getTimeInSecond()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        return songList;
    }

}
