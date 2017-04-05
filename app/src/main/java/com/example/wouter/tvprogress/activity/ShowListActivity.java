package com.example.wouter.tvprogress.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.API.CallAPIAllEpisodes;
import com.example.wouter.tvprogress.model.API.CallAPIAllShows;
import com.example.wouter.tvprogress.model.API.iOnTaskCompleted;
import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Show;
import com.example.wouter.tvprogress.model.ShowListAdapter;

import java.util.LinkedList;

public class ShowListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, iOnTaskCompleted {

    private LinkedList<Show> mShows = new LinkedList<>();

    private ListView mShowListView;
    private SearchView mSearchView;

    private DatabaseConnection mDatabaseConnection;
    private ShowListAdapter mShowListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        //SQLiteDatabase mDatabase = openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(this);

        mShowListView = (ListView) findViewById(R.id.lvShows);
        mShowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                showClicked(position);
            }
        });

        mShowListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeShow(position);
                return true;
            }
        });

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(this);

        loadShows();
        ReloadEpisodeList();
    }

    private void loadShows(){
        mShows = mDatabaseConnection.getShows("SELECT * FROM shows ORDER BY Title");

        mShowListAdapter = new ShowListAdapter(this, mShows);
        mShowListView.setAdapter(mShowListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadShows();
    }

    private void showClicked(int position){
        Show show = (Show) mShowListAdapter.getItem(position);

        Intent mIntent = new Intent(getApplicationContext(), ShowActivity.class);
        mIntent.putExtra("id", show.getId());

        startActivity(mIntent);
    }

    private void removeShow(int position){
        final Show show = (Show) mShowListAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete show '" + show.getTitle() + "'")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String query = "DELETE FROM shows WHERE _id = ?";
                        SQLiteStatement statement = mDatabaseConnection.getNewStatement(query);
                        statement.bindLong(1, show.getId());
                        mDatabaseConnection.executeNonReturn(statement);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
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

        if(id == R.id.action_new)
        {
            Intent mIntent = new Intent(getApplicationContext(), EditShowActivity.class);
            startActivity(mIntent);
        }

        if(id == R.id.action_reload)
        {
            ReloadEpisodeList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void ReloadEpisodeList(){
        for(Show show : mShows){
            if(show.getURL() != "")
            {
                System.out.println(show.getId() + " Get all episodes of " + show.getTitle());
                new CallAPIAllEpisodes(this, this, show.getId(), show.getURL()).execute();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mShowListAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onTaskCompleted(Object values) {

    }
}
