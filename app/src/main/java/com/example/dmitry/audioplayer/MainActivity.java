package com.example.dmitry.audioplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dmitry.audioplayer.adapters.SongsAdapter;
import com.example.dmitry.audioplayer.model.MusicProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {


    //btn
    private ImageButton buttonPlayPause;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    private ImageButton buttonStop;
    private Button buttonChooseTheFolder;

    //create format
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    //tv
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private TextView tvRunningTime;
    private TextView tvTotalTime;
    private ImageView imgNoteOrPlay;

    //lists
    private volatile ArrayList<Song> songsList;
    private volatile ArrayList<Song> fSongsList;
    private volatile ArrayList<Song> aSongsList;
    private ListView playList;
    private SongsAdapter adapter;
    private String folder;

    //service
    private MusicService musicService;
    private Intent playIntent;

    //check
    private boolean isListSet = false;
    private boolean isFolder = false;
    private boolean isBound = false;

    private SwitchCompat switchCompat;
    private SeekBar seekBar;
    private Toolbar toolbar;
    private AlertDialog alertDialog;
    private SearchView mSearchView;

    private Handler handler;
    private Runnable runnable;
    private MusicProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initList();
        initHandlers();
        createDialog();
        initToolbar();
        initSearchView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //bind and start service
        if (playIntent == null && !isBound) {
            try {
                playIntent = new Intent(this, MusicService.class);
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                startService(playIntent);
            } catch (Exception e) {
                Log.e("myLog", "bind error");

            }
        }
        //update seek bar and other
        progressUpdater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (folder != null && switchCompat.isChecked()) {
            setDataToAdapter(provider.getSongsSortedByTitle(folder));
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("folder", folder);
        outState.putBoolean("isBound", isBound);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        folder = savedInstanceState.getString("folder");
        isBound = savedInstanceState.getBoolean("isBound");
    }

    private void progressUpdater() {

        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                if (isBound == true && musicService.isStarted()) { // Check if service bounded

                    //get time
                    int pos = musicService.getPosn();
                    int dur = musicService.getDur();
                    Date datePos = new Date(pos);
                    Date dateDur = new Date(dur);

                    if (musicService.isPng())
                        buttonPlayPause.setImageResource(R.mipmap.ic_pause);
                    else
                        buttonPlayPause.setImageResource(R.mipmap.ic_play);


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

                    //logic fo image note or play
                    int position = -1;

                    Song currSong = musicService.getSong();
                    for (int i = 0; i < songsList.size(); i++) {
                        if (songsList.get(i).equals(currSong))
                            position = i;
                    }
                    for (int i = 0; i < songsList.size(); i++) {
                        try {
                            imgNoteOrPlay = (ImageView) playList.getChildAt(i).findViewById(R.id.img_note_play);
                        } catch (Exception e) {
                        }
                        imgNoteOrPlay.setImageResource(R.mipmap.ic_note_black);
                        int nPos = position - playList.getFirstVisiblePosition();
                        if (i == nPos)
                            imgNoteOrPlay.setImageResource(R.mipmap.ic_play);

                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void initViews() {
        switchCompat = (SwitchCompat) findViewById(R.id.switch_compat);
        //buttons
        buttonPlayPause = (ImageButton) findViewById(R.id.btn_play_and_pause);
        buttonNext = (ImageButton) findViewById(R.id.btn_next);
        buttonPrevious = (ImageButton) findViewById(R.id.btn_previous);
        buttonStop = (ImageButton) findViewById(R.id.btn_stop);

        buttonPlayPause.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);
        if (buttonStop != null)
            buttonStop.setOnClickListener(this);

        buttonChooseTheFolder = (Button) findViewById(R.id.btn_choose_the_folder);

        //TV
        tvTitle = (TextView) findViewById(R.id.tvSongTitle);
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        tvAlbum = (TextView) findViewById(R.id.tvAlbum);
        tvRunningTime = (TextView) findViewById(R.id.tvTimePlayed);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalRunningTime);

        //play list and seek bar
        playList = (ListView) findViewById(R.id.audioList);


        seekBar = (SeekBar) findViewById(R.id.seekBar);

        buttonPlayPause.setImageResource(R.mipmap.ic_play);
    }

    private void initHandlers() {
        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicService.setList(songsList);

                ArrayList<Song> filteredSongs = adapter.getFilteredSongs();
                if (filteredSongs != null)
                    musicService.setList(filteredSongs);

                musicService.setSong(position);

            }
        });
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (musicService.isPng()) {
                    SeekBar sb = (SeekBar) v;
                    musicService.seek(sb.getProgress());
                }
                return false;
            }
        });

        buttonChooseTheFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoldersActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDataToAdapter(provider.getSongsSortedByTitle(folder));
                    isFolder = true;
                } else {
                    setDataToAdapter(provider.getSongsSortedByTitle());
                    isFolder = false;
                }
            }
        });
    }

    private void initList() {
        //instantiate list
        provider = new MusicProvider(this);
        songsList = provider.getSongsSortedByTitle();

        //create and set adapter
        adapter = new SongsAdapter(this, songsList);
        playList.setAdapter(adapter);
        playList.setTextFilterEnabled(true);
        adapter.notifyDataSetChanged();
    }

    private void initSearchView() {
        mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_sort);
        toolbar.setTitle("My player");
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicService = binder.getService();
            //pass list
            if (!isListSet) {
                musicService.setList(songsList);
                isListSet = true;
            }

            //after open with
            if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                File file = new File(getIntent().getData().getPath());
                for (int i = 0; i < songsList.size(); i++) {
                    if (songsList.get(i).getData().equals(file.getAbsolutePath())) {
                        musicService.setSong(i);
                    }
                }
            }
            //check
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            isListSet = false;
            isBound = false;
        }
    };

    public void setDataToAdapter(ArrayList<Song> data) {

        songsList = data;
        adapter.setSongs(data);
        adapter.notifyDataSetChanged();
    }

    public void playAndStop() {
        if (!musicService.isPng()) {
            try {
                if (!musicService.isStarted())
                    musicService.setSong(0);
                else
                    musicService.go();
                buttonPlayPause.setImageResource(R.mipmap.ic_pause);
            } catch (IllegalStateException e) {
                musicService.pausePlayer();
                buttonPlayPause.setImageResource(R.mipmap.ic_play);
            }
        } else {
            musicService.pausePlayer();
            buttonPlayPause.setImageResource(R.mipmap.ic_play);
        }
    }

    private void createDialog() {
        final String[] sort = {"Title", "Artist", "Album", "Running time"};
        provider = new MusicProvider(this);
        provider.getFolders();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setItems(sort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (isFolder) {
                            setDataToAdapter(provider.getSongsSortedByTitle(folder));
                        } else
                            setDataToAdapter(provider.getSongsSortedByTitle());
                        break;
                    case 1:
                        if (isFolder) {
                            setDataToAdapter(provider.getSongsSortedByArtist(folder));
                        } else
                            setDataToAdapter(provider.getSongsSortedByArtist());
                        break;
                    case 2:
                        if (isFolder) {
                            setDataToAdapter(provider.getSongsSortedByAlbum(folder));
                        } else
                            setDataToAdapter(provider.getSongsSortedByAlbum());
                        break;
                    case 3:
                        if (isFolder) {
                            setDataToAdapter(provider.getSongsSortedByDuration(folder));
                        } else
                            setDataToAdapter(provider.getSongsSortedByDuration());
                        break;
                }
            }
        });
        alertDialog = builder.create();
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
                musicService.stop();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        folder = data.getStringExtra("folder");
        switchCompat.setChecked(false);
        switchCompat.setChecked(true);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            playList.clearTextFilter();
        } else {
            playList.setFilterText(newText);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (musicService != null && !musicService.isPng()) {
            stopService(playIntent);
        }
    }


}
