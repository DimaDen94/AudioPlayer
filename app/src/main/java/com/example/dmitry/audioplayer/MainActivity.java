package com.example.dmitry.audioplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.example.dmitry.audioplayer.model.MusicProvider;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private AlertDialog alertDialog;
    private PlayListFragment playListFragment;
    private FolderListFragment folderListFragment;
    private MusicProvider provider;
    private SearchView mSearchView;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded playListFragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDialog();
        initToolbar();
        initPagers();
        playListFragment = PlayListFragment.newInstance(this);
        folderListFragment = FolderListFragment.newInstance(this);
        setupSearchView();
    }
    private void setupSearchView()
    {
        mSearchView=(SearchView) findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(this);
        //mSearchView.setSubmitButtonEnabled(true);

    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_sort);
        toolbar.setTitle("My player");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
    }
    private void createDialog(){
        final String[] sort ={"Title", "Artist", "Album","Running time"};
        provider = new MusicProvider(this);
        provider.getFolders();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        //builder.setMessage("Sort by");
        builder.setItems(sort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        onQueryTextChange("");
                        mSearchView.clearFocus();
                        playListFragment.setDataToAdapter(provider.getSongsSortedByTitle());
                        break;
                    case 1:
                        onQueryTextChange("");
                        mSearchView.clearFocus();
                        playListFragment.setDataToAdapter(provider.getSongsSortedByArtist());
                        break;
                    case 2:
                        onQueryTextChange("");
                        mSearchView.clearFocus();
                        playListFragment.setDataToAdapter(provider.getSongsSortedByAlbum());
                        break;
                    case 3:
                        onQueryTextChange("");
                        mSearchView.clearFocus();
                        playListFragment.setDataToAdapter(provider.getSongsSortedByDuration());
                        break;
                }
            }
        });
        alertDialog = builder.create();
    }


    private void initPagers(){
        // Create the adapter that will return a playListFragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }





    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            playListFragment.getPlayList().clearTextFilter();
        } else {
            playListFragment.getPlayList().setFilterText(newText);
        }
        return true;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a playListFragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Context context;
        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the playListFragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return playListFragment;
                default:
                    return folderListFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "List".toUpperCase(l);
                case 1:
                    return "browser".toUpperCase(l);
            }
            return null;
        }
    }
}
