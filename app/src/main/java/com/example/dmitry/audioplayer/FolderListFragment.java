package com.example.dmitry.audioplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.dmitry.audioplayer.model.MusicProvider;

public class FolderListFragment extends Fragment {
    private Context context;
    private GridView foldersView;


    Cursor musiccursor;
    int music_column_index;
    int count;

    public void setContext(Context context) {
        this.context = context;
    }

    public static FolderListFragment newInstance(Context context) {
        FolderListFragment fragment = new FolderListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    public FolderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_folder_list, container, false);
    }
    private void initPhoneMusicGrid() {
        foldersView = (GridView) getActivity().findViewById(R.id.gridView);

        foldersView.setAdapter(new FoldersAdapter(context, new MusicProvider(context).getFolders()));

        foldersView.setNumColumns(GridView.AUTO_FIT);
        /*foldersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
    }
    @Override
    public void onStart() {
        super.onStart();
        initPhoneMusicGrid();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
