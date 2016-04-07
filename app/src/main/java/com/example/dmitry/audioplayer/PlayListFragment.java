package com.example.dmitry.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PlayListFragment extends Fragment implements View.OnClickListener {
    private ImageButton buttonPlayStop;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    int i = 1;
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private ArrayList<Song> songList;
    private ListView playList;



    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public static PlayListFragment newInstance(Context context) {

        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        //retrieve list view
        //instantiate list
        songList = new ArrayList<Song>();
        //get songs from device
        getSongList();
        //sort alphabetically by title
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        //create and set adapter
        SongAdapter songAdt = new SongAdapter(context, songList);
        playList.setAdapter(songAdt);

        //setup controller

    }


    private void initViews() {
        tvTitle = (TextView) getActivity().findViewById(R.id.tvSongTitle);
        tvArtist = (TextView) getActivity().findViewById(R.id.tvArtist);
        tvAlbum = (TextView) getActivity().findViewById(R.id.tvAlbum);
        buttonPlayStop = (ImageButton) getActivity().findViewById(R.id.btn_play_and_pause);
        buttonPlayStop.setOnClickListener(this);
        mediaPlayer = MediaPlayer.create(context, R.raw.test_song);
        playList= (ListView) getActivity().findViewById(R.id.audioList);

        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }
    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }
    public void playAndStop(){
        tvTitle.setText("");
        if (i ==1) {
            try{
                mediaPlayer.start();
                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                mediaPlayer.pause();
            }
            i = 0;
        }else {
            mediaPlayer.pause();
            i = 1;
        }
    }

    private void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            i =1;
            seekBar.setProgress(0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        seekBar.setMax(mediaPlayer.getDuration());
        playAndStop();
    }



    public void getSongList(){
        //query external audio
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }


}
