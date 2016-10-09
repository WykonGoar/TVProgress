package com.example.wouter.tvprogress.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.API.CallAPIAllEpisodes;
import com.example.wouter.tvprogress.model.API.iOnTaskCompleted;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Episode;
import com.example.wouter.tvprogress.model.EpisodeListAdapter;
import com.example.wouter.tvprogress.model.Show;

import java.util.HashMap;
import java.util.LinkedList;

public class ShowActivity extends AppCompatActivity implements iOnTaskCompleted {

    private Context mContext;
    private LinkedList<Integer> mListDataHeader;
    private HashMap<Integer, LinkedList<Episode>> mListDataChild;
    private Show mShow;

    private TextView tvNextTitle;
    private TextView tvNext;

    private LinearLayout llEditable;
    private LinearLayout llNext;
    private LinearLayout llNextEpisode;

    private TextView tvTitle;
    private TextView tvUpToDate;
    private EditText etCurrentSeason;
    private EditText etCurrentEpisode;
    private ImageView ivBanner;
    private ExpandableListView  expandableListView;
    private DatabaseConnection mDatabaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        mContext = this;

        Intent mIntent = getIntent();
        int showId = mIntent.getIntExtra("id", -1);
        if (showId == -1)
            throw new IndexOutOfBoundsException("No id is given.");

        SQLiteDatabase mDatabase = openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, this);

        tvNextTitle = (TextView) findViewById(R.id.tvNextTitle);
        tvNext = (TextView) findViewById(R.id.tvNext);

        llEditable = (LinearLayout) findViewById(R.id.llEditable);
        llNext = (LinearLayout) findViewById(R.id.llNext);
        llNextEpisode = (LinearLayout) findViewById(R.id.llNextEpisode);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvUpToDate = (TextView) findViewById(R.id.tvUpToDate);
        etCurrentSeason = (EditText) findViewById(R.id.etCurrentSeason);
        etCurrentEpisode = (EditText) findViewById(R.id.etCurrentEpisode);
        ivBanner = (ImageView) findViewById(R.id.ivBanner);
        expandableListView = (ExpandableListView)  findViewById(R.id.elvEpisodes);

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int groupValue = mListDataHeader.get(groupPosition);
                Episode selectedEpisode = mListDataChild.get(groupValue).get(childPosition);

                ImageView ivCheckbox = (ImageView) v.findViewById(R.id.cbSeen);

                String query = "";
                if (selectedEpisode.isSeen()) {
                     query = "UPDATE episodes SET seen = 0 WHERE showId = ? AND season = ? AND episode = ?";

                    ivCheckbox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
                    selectedEpisode.setmSeen(false);
                } else {
                    query = "UPDATE episodes SET seen = 1 WHERE showId = ? AND season = ? AND episode = ?";

                    ivCheckbox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
                    selectedEpisode.setmSeen(true);
                }

                SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
                statement.bindLong(1, selectedEpisode.getShowId());
                statement.bindLong(2, selectedEpisode.getSeason());
                statement.bindLong(3, selectedEpisode.getEpisode());
                mDatabaseConnection.executeNonReturn(statement);

                getNextEpisode();

                return true;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);

                if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    int groupValue = mListDataHeader.get(groupPosition);
                    Episode selectedEpisode = mListDataChild.get(groupValue).get(childPosition);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setMessage("Release date: " + selectedEpisode.getReleaseDate());
                    builder.setTitle("Release date");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return  true;
                }

                return  false;
            }
        });

        Button bSeenNextEpisode = (Button) findViewById(R.id.bSeenNextEpisode);
        bSeenNextEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seenNextEpisode();
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

        mShow = getShow(showId);

        boolean newResource =  mIntent.getBooleanExtra("newResource", false);
        if(newResource)
            System.out.println("Call resourceChanged");
            resourceChanged();

        loadShow();
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        if (mShow.getBannerAsImage() != null)
            ivBanner.setImageBitmap(mShow.getBannerAsImage());

        if(!mShow.getURL().isEmpty()){
            llEditable.setVisibility(View.GONE);
            llNext.setVisibility(View.VISIBLE);
            Episode nextEpisode = getNextEpisode();

            if(nextEpisode == null) {
                llNextEpisode.setVisibility(View.GONE);
                tvUpToDate.setVisibility(View.VISIBLE);
            }

            prepareListData();

            if (mListDataHeader != null) {
                ExpandableListAdapter expandableListAdapter = new EpisodeListAdapter(getBaseContext(), mListDataHeader, mListDataChild);
                expandableListView.setAdapter(expandableListAdapter);

                int index = 0;
                if(nextEpisode != null)
                    index = mListDataHeader.indexOf(nextEpisode.getSeason());
                expandableListView.expandGroup(index);
            }
        }
    }

    private Episode getNextEpisode(){
        Episode nextEpisode = mDatabaseConnection.getNextpisode(mShow.getId());

        if(nextEpisode != null) {
            tvNextTitle.setText(nextEpisode.getTitle());
            tvNext.setText("Season: " + nextEpisode.getSeason() + "   Episode: " + nextEpisode.getEpisode());
        }

        return nextEpisode;
    }

    private void seenNextEpisode(){
        Episode nextEpisode = mDatabaseConnection.getNextpisode(mShow.getId());

        String query = "UPDATE episodes SET seen = 1 WHERE showId = ? AND season = ? AND episode = ?";
        SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
        statement.bindLong(1, nextEpisode.getShowId());
        statement.bindLong(2, nextEpisode.getSeason());
        statement.bindLong(3, nextEpisode.getEpisode());
        mDatabaseConnection.executeNonReturn(statement);

        loadShow();
    }

    private void nextSeason(){
        mShow.setCurrentSeason(mShow.getCurrentSeason() + 1);

        String query = "UPDATE shows SET currentSeason = ? WHERE _id = ?";
        SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
        statement.bindLong(1, mShow.getCurrentSeason());
        statement.bindLong(2, mShow.getId());
        mDatabaseConnection.executeNonReturn(statement);

        loadShow();
    }

    private void nextEpisode(){
        mShow.setCurrentEpisode(mShow.getCurrentEpisode() + 1);

        String query = "UPDATE shows SET currentEpisode = ? WHERE _id = ?";
        SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
        statement.bindLong(1, mShow.getCurrentEpisode());
        statement.bindLong(2, mShow.getId());
        mDatabaseConnection.executeNonReturn(statement);

        loadShow();
    }

    private void prepareListData() {
        LinkedList<Episode> episodes = mDatabaseConnection.getEpisodes(mShow.getId());

        if(episodes.size() == 0)
            return;

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
    public void onTaskCompleted(Object values) {
        loadShow();
    }

    private void resourceChanged(){
        new CallAPIAllEpisodes(this, this, mShow.getId(), mShow.getURL()).execute();
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
        if (id == R.id.action_edit) {
            Intent mIntent = new Intent(getApplicationContext(), EditShowActivity.class);
            mIntent.putExtra("id", mShow.getId());

            startActivity(mIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
