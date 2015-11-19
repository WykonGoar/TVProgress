package com.example.wouter.tvprogress.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Episode;
import com.example.wouter.tvprogress.model.EpisodeListAdapter;
import com.example.wouter.tvprogress.model.Show;

import java.util.HashMap;
import java.util.LinkedList;

public class ShowActivity extends AppCompatActivity {

    private LinkedList<Integer> mListDataHeader;
    private HashMap<Integer, LinkedList<Episode>> mListDataChild;
    private Show mShow;

    private TextView tvTitle;
    private TextView tvUpToDate;
    private EditText etCurrentSeason;
    private EditText etCurrentEpisode;
    private ExpandableListView  expandableListView;
    private DatabaseConnection mDatabaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent mIntent = getIntent();
        int showId = mIntent.getIntExtra("id", -1);
        if (showId == -1)
            throw new IndexOutOfBoundsException("No id is given.");

        SQLiteDatabase mDatabase = openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, this);

        mShow = getShow(showId);

        prepareListData();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvUpToDate = (TextView) findViewById(R.id.tvUpToDate);
        etCurrentSeason = (EditText) findViewById(R.id.etCurrentSeason);
        etCurrentEpisode = (EditText) findViewById(R.id.etCurrentEpisode);
        expandableListView = (ExpandableListView)  findViewById(R.id.elvEpisodes);
        // Listview on child click listener
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int groupValue = mListDataHeader.get(groupPosition);
                Episode selectedEpisode = mListDataChild.get(groupValue).get(childPosition);

                CheckBox cbSeen = (CheckBox) v.findViewById(R.id.cbSeen);

                if(selectedEpisode.isSeen()) {
                    mDatabaseConnection.executeNonReturn("UPDATE episodes SET seen = 0 WHERE _id = " + selectedEpisode.getId());
                    cbSeen.setChecked(false);
                    selectedEpisode.setmSeen(false);
                }
                else {
                    mDatabaseConnection.executeNonReturn("UPDATE episodes SET seen = 1 WHERE _id = " + selectedEpisode.getId());
                    cbSeen.setChecked(true);
                    selectedEpisode.setmSeen(true);
                }

                return true;
            }
        });

        Button bNextSeason = (Button) findViewById(R.id.bNextSeason);
        bNextSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSeason();
            }
        });

        Button bNextEpisode = (Button) findViewById(R.id.bNextEpisode);
        bNextEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextEpisode();
            }
        });

        loadShow();
    }

    private Show getShow(int showId){
        LinkedList<Show> shows = mDatabaseConnection.getShows("SELECT * FROM shows WHERE _id = " + showId);
        Show selectedShow = null;

        if (shows.size() == 0)
            throw new IndexOutOfBoundsException("Show with id '" + showId + "' not found.");
        else
            selectedShow = shows.get(0);

        return selectedShow;
    }

    private void loadShow(){
        mShow = getShow(mShow.getId());

        tvTitle.setText(mShow.getTitle());
        etCurrentSeason.setText("" + mShow.getCurrentSeason());
        etCurrentEpisode.setText("" + mShow.getCurrentEpisode());

        if(mShow.isUpToDate())
            tvUpToDate.setVisibility(View.VISIBLE);

        prepareListData();
        ExpandableListAdapter expandableListAdapter = new EpisodeListAdapter(getBaseContext(), mListDataHeader, mListDataChild);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.expandGroup(0);
    }

    private void nextSeason(){
        mShow.setCurrentSeason(mShow.getCurrentSeason() + 1);
        mDatabaseConnection.executeNonReturn("UPDATE shows SET currentSeason = " + mShow.getCurrentSeason() + " WHERE _id = " + mShow.getId());

        loadShow();
    }

    private void nextEpisode(){
        mShow.setCurrentEpisode(mShow.getCurrentEpisode() + 1);
        mDatabaseConnection.executeNonReturn("UPDATE shows SET currentEpisode = " + mShow.getCurrentEpisode() + " WHERE _id = " + mShow.getId());

        loadShow();
    }

    private void prepareListData() {
        LinkedList<Episode> episodes = mDatabaseConnection.getEpisodes(mShow.getId());

        mListDataHeader = new LinkedList<Integer>();
        mListDataChild = new HashMap<Integer, LinkedList<Episode>>();

        for(Episode episode : episodes){
            int currentSeason = episode.getSeason();

            LinkedList<Episode> newEpisodesList = null;

            if (!mListDataHeader.contains(currentSeason)){
                mListDataHeader.add(currentSeason);
                newEpisodesList = new LinkedList<Episode>();
            }
            else {
                newEpisodesList = mListDataChild.get(currentSeason);
            }

            newEpisodesList.add(episode);

            mListDataChild.remove(currentSeason);
            mListDataChild.put(currentSeason, newEpisodesList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
