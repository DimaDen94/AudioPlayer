package com.example.dmitry.audioplayer.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.provider.MediaStore;

import com.example.dmitry.audioplayer.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
        songList.clear();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
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
    public ArrayList<String> getFolders(){
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ArrayList<String> folders = new ArrayList<>();
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {

            int dataColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            do {
                String thisData = musicCursor.getString(dataColumn);
                File file = new File(thisData);
                String directory = file.getParent();

                if(!folders.contains(directory)){
                    folders.add(directory);
                }

            }
            while (musicCursor.moveToNext());
        }
        return folders;
    }



    private void getSongList(String folder) {

        songList.clear();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                MediaStore.Audio.Media.DATA + " LIKE " + "'"+folder+ "/%'";


        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
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

                //if true directory
                File file = new File(thisData);
                String directory = file.getParent();
                if(directory.equals(folder))
                    songList.add(new Song(thisDuration, thisTitle, thisArtist,thisAlbum,thisData));
            }
            while (musicCursor.moveToNext());
        }
    }




    public ArrayList<Song> getSongsSortedByTitle(){
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByArtist(){
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getArtist().compareTo(b.getArtist());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByAlbum(){
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getAlbum().compareTo(b.getAlbum());
            }
        });
        return songList;
    }
    public ArrayList<Song> getSongsSortedByDuration(){
        getSongList();
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

    public ArrayList<Song> getSongsSortedByTitle(String folder) {
        getSongList(folder);
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return songList;
    }

    public ArrayList<Song> getSongsSortedByArtist(String folder) {
        getSongList(folder);
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getArtist().compareTo(b.getArtist());
            }
        });
        return songList;
    }

    public ArrayList<Song> getSongsSortedByAlbum(String folder) {
        getSongList(folder);
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getAlbum().compareTo(b.getAlbum());
            }
        });
        return songList;
    }

    public ArrayList<Song> getSongsSortedByDuration(String folder) {
        getSongList(folder);
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                if (a.getTimeInSecond() > b.getTimeInSecond()) {
                    return 1;
                } else if (a.getTimeInSecond() < b.getTimeInSecond()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return songList;
    }





}
