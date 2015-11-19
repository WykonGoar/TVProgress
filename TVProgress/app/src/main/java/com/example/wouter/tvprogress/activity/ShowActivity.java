package com.example.wouter.tvprogress.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.Episode;
import com.example.wouter.tvprogress.model.EpisodeListAdapter;
import com.example.wouter.tvprogress.model.Show;

import java.util.HashMap;
import java.util.LinkedList;

public class ShowActivity extends AppCompatActivity {

    private LinkedList<Integer> mListDataHeader;
    private HashMap<Integer, LinkedList<Episode>> mListDataChild;
    private Show mShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent mIntent = getIntent();
        int showId = mIntent.getIntExtra("id", -1);
        if (showId == -1)
            throw new IndexOutOfBoundsException("No id is given.");

        mShow = getTestShows()[showId];

        prepareListData();
        ExpandableListView  expandableListView = (ExpandableListView)  findViewById(R.id.elvEpisodes);
        ExpandableListAdapter expandableListAdapter = new EpisodeListAdapter(getBaseContext(), mListDataHeader, mListDataChild);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.expandGroup(0);
    }

    private void prepareListData() {
        mListDataHeader = new LinkedList<Integer>();
        mListDataChild = new HashMap<Integer, LinkedList<Episode>>();

        mListDataHeader.add(1);
        mListDataHeader.add(2);

        Episode episode1 = new Episode(0);
        episode1.setmSeason(1);
        episode1.setmEpisode(1);
        episode1.setmTitle("Pilot1");

        Episode episode2 = new Episode(1);
        episode2.setmSeason(1);
        episode2.setmEpisode(2);
        episode2.setmTitle("New Ep1");

        LinkedList season1 = new LinkedList<Episode>();
        season1.add(episode1);
        season1.add(episode2);
        mListDataChild.put(1, season1);

        Episode episode3 = new Episode(2);
        episode3.setmSeason(2);
        episode3.setmEpisode(1);
        episode3.setmTitle("Pilot12");

        Episode episode4 = new Episode(3);
        episode4.setmSeason(2);
        episode4.setmEpisode(2);
        episode4.setmTitle("New Ep2");

        LinkedList season2 = new LinkedList<Episode>();
        season2.add(episode3);
        season2.add(episode4);
        mListDataChild.put(2, season2);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Show[] getTestShows(){

        Show show1 = new Show(0, "Show 1");
        show1.setCurrentSeason(1);
        show1.setCurrentEpisode(5);

        Show show2 = new Show(1, "Show 2");
        show2.setCurrentSeason(2);
        show2.setCurrentEpisode(10);

        Show show3 = new Show(2, "Show 3");
        show3.setCurrentSeason(3);
        show3.setCurrentEpisode(15);

        return new Show[]{show1, show2,show3};
    }
}
