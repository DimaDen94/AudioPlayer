package com.example.dmitry.audioplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.dmitry.audioplayer.model.MusicProvider;

public class FoldersActivity extends AppCompatActivity {
    private GridView foldersView;
    private MusicProvider provider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        provider = new MusicProvider(this);
        initPhoneMusicGrid();
    }

    private void initPhoneMusicGrid() {
        foldersView = (GridView) findViewById(R.id.gridView);

        foldersView.setAdapter(new FoldersAdapter(this, new MusicProvider(this).getFolders()));

        foldersView.setNumColumns(GridView.AUTO_FIT);
        foldersView.setColumnWidth(180);
        foldersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("folder", provider.getFolders().get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
