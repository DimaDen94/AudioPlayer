package com.example.dmitry.audioplayer;

import com.example.dmitry.audioplayer.MusicService.MusicBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class PlayListFragment extends Fragment implements View.OnClickListener {
    //btn
    private ImageButton buttonPlayStop;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    //create format
    SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    //is working progress update
    private boolean isProgress =  false;

    private SeekBar seekBar;

    private final Handler handler = new Handler();

    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private TextView tvRunningTime;
    private TextView tvTotalTime;

    private ArrayList<Song> songList;
    private ListView playList;

    private MusicService musicService;
    private Intent playIntent;

    private boolean musicBound = false;
    BroadcastReceiver receiver;

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
        //intent
        if (playIntent == null) {
            playIntent = new Intent(context, MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
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

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicService.setSong(position);
                if(!isProgress)
                    startPlayProgressUpdater();
            }
        });

    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;
            //get service
            musicService = binder.getService();
            //pass list
            musicService.setList(songList);

            musicBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void initViews() {
        //buttons
        buttonPlayStop = (ImageButton) getActivity().findViewById(R.id.btn_play_and_pause);
        buttonNext = (ImageButton) getActivity().findViewById(R.id.btn_next);
        buttonPrevious = (ImageButton) getActivity().findViewById(R.id.btn_previous);

        buttonPlayStop.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);

        //TV
        tvTitle = (TextView) getActivity().findViewById(R.id.tvSongTitle);
        tvArtist = (TextView) getActivity().findViewById(R.id.tvArtist);
        tvAlbum = (TextView) getActivity().findViewById(R.id.tvAlbum);
        tvRunningTime = (TextView) getActivity().findViewById(R.id.tvTimePlayed);
        tvTotalTime = (TextView) getActivity().findViewById(R.id.tvTotalRunningTime);

        //play list and seek bar
        playList = (ListView) getActivity().findViewById(R.id.audioList);

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("myLogs", "click");
                musicService.setSong(position);
                musicService.playSong();
            }
        });




        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }

    private void seekChange(View v) {
        if (musicService.isPng()) {
            SeekBar sb = (SeekBar) v;
            musicService.seek(sb.getProgress());
            if(!isProgress)
                startPlayProgressUpdater();
        }
    }

    public void playAndStop() {
        if (!musicService.isPng()) {
            try {
                musicService.go();
            } catch (IllegalStateException e) {
                musicService.pausePlayer();
            }
        } else {
            musicService.pausePlayer();
        }
    }

    private void startPlayProgressUpdater() {

        //if started
        isProgress = true;

        //get time
        int pos = musicService.getPosn();
        int dur = musicService.getDur();
        Date datePos = new Date(pos);
        Date dateDur = new Date(dur);

        //update time
        tvRunningTime.setText(String.valueOf(format.format(datePos)));
        tvTotalTime.setText(String.valueOf(format.format(dateDur)));


        //update seek bar
        seekBar.setProgress(pos);
        seekBar.setMax(dur);

        //update tv
        tvTitle.setText(musicService.getTitle());
        tvArtist.setText(musicService.getArtist());
        tvAlbum.setText(musicService.getAlbum());

        Runnable notification = new Runnable() {
            public void run() {
                startPlayProgressUpdater();
            }
        };
        handler.postDelayed(notification, 950);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_and_pause:
                playAndStop();
                break;
            case R.id.btn_next:
                musicService.playNext();
                break;
            case R.id.btn_previous:
                musicService.playPrev();
                break;
            case R.id.btn_stop:
                break;
        }
        if(!isProgress)
            startPlayProgressUpdater();
    }

    public void getSongList() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        //query external audio
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);
        //iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int dataColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisData = musicCursor.getString(dataColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist,thisAlbum,thisData));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        getActivity().stopService(playIntent);
    }
}
