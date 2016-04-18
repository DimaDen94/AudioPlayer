package com.example.dmitry.audioplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dmitry.audioplayer.R;

import java.util.ArrayList;

/**
 * Created by Dmitry on 13.04.2016.
 */
public class FoldersAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> arrayList;
    private LayoutInflater foldersInf;
    public FoldersAdapter(Context c, ArrayList<String> arrayList) {
        mContext = c;
        foldersInf=LayoutInflater.from(c);
        this.arrayList = arrayList;
    }

    public int getCount() {
        return arrayList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //map to song layout
        LinearLayout songLay = (LinearLayout) foldersInf.inflate
                (R.layout.grid_item, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.textView);

        //get song using position
        String currSong = arrayList.get(position);
        //get title and artist strings
        songView.setText(currSong.substring(currSong.lastIndexOf('/')+1,currSong.length()));
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
