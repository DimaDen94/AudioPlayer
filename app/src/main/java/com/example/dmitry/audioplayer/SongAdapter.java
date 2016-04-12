package com.example.dmitry.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 08.04.2016.
 */
public class SongAdapter extends BaseAdapter implements Filterable {

    //song list and layout
    private ArrayList<Song> songs;
    private ArrayList<Song> origSongs;

    private LayoutInflater songInf;

    public ArrayList<Song> getSongs() {
        return origSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    //constructor
    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.item, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.tv_item_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.tv_item_artist);
        TextView durationView = (TextView)songLay.findViewById(R.id.tv_item_duration);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        durationView.setText(currSong.getTime());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Song> results = new ArrayList<>();
                if (origSongs == null)
                    origSongs = songs;
                if (constraint != null) {
                    if (origSongs != null && origSongs.size() > 0) {
                        for (final Song g : origSongs) {
                            if (g.getTitle().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                songs = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
