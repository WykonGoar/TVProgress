package com.example.wouter.tvprogress.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Show;
import com.example.wouter.tvprogress.model.ShowListAdapter;

public class ShowListActivity extends AppCompatActivity {

    private Show[] mShows = new Show[0];

    private ListView mShowListView;
    private DatabaseConnection mDatabaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        SQLiteDatabase mDatabase = openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, this);

        mShowListView = (ListView) findViewById(R.id.lvShows);
        mShowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                showClicked(position);
            }
        });
    }

    private void loadShows(){
        mShows = mDatabaseConnection.getShows("SELECT * FROM shows");

        ShowListAdapter mShowListAdapter = new ShowListAdapter(this, mShows);
        mShowListView.setAdapter(mShowListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadShows();
    }

    private void showClicked(int position){
        Show show = mShows[position];

        Intent mIntent = new Intent(getApplicationContext(), ShowActivity.class);
        mIntent.putExtra("id", show.getId());

        startActivity(mIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_list, menu);
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

        Show show1 = new Show(1, "Show 1");
        show1.setCurrentSeason(1);
        show1.setCurrentEpisode(5);

        Show show2 = new Show(2, "Show 2");
        show2.setCurrentSeason(2);
        show2.setCurrentEpisode(10);

        Show show3 = new Show(3, "Show 3");
        show3.setCurrentSeason(3);
        show3.setCurrentEpisode(15);

        return new Show[]{show1, show2,show3};
    }
}
