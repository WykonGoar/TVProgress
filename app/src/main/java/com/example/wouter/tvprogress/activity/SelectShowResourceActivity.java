package com.example.wouter.tvprogress.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.wouter.tvprogress.R;
import com.example.wouter.tvprogress.model.API.CallAPIAllShows;
import com.example.wouter.tvprogress.model.API.ShowResource;
import com.example.wouter.tvprogress.model.API.ShowResourceAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectShowResourceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ArrayList<ShowResource> resources = new ArrayList<ShowResource>();

    private ListView mResourceListView;
    private SearchView mSearchView;

    private ShowResourceAdapter mShowResourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_episodes_resource);

        Intent intent = getIntent();
        resources = intent.getParcelableArrayListExtra("resources");

        mResourceListView = (ListView) findViewById(R.id.lvResources);
        mShowResourceAdapter = new ShowResourceAdapter(this, resources);
        mResourceListView.setAdapter(mShowResourceAdapter);
        mResourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                resourceClicked(position);
            }
        });

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(this);
    }

    private void resourceClicked(int position){
        ShowResource resource = (ShowResource) mShowResourceAdapter.getItem(position);

        Intent intent = new Intent();
        intent.putExtra("resource", resource.getURL());
        intent.putExtra("status", resource.getStatus());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mShowResourceAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
