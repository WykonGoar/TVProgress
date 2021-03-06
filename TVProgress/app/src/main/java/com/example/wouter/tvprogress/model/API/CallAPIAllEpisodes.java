package com.example.wouter.tvprogress.model.API;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;

import com.example.wouter.tvprogress.model.DatabaseConnection;
import com.example.wouter.tvprogress.model.Episode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Wouter on 24-11-2015.
 */
public class CallAPIAllEpisodes extends AsyncTask<Integer, Integer, Boolean>{

    private int mShowId;
    private String urlString;
    private iOnTaskCompleted mCurrentActivity;
    private DatabaseConnection mDatabaseConnection;

    public CallAPIAllEpisodes(Context context, iOnTaskCompleted currentActivity, int showId, String resource){
        mShowId = showId;
        mCurrentActivity = currentActivity;
        urlString = resource;

        SQLiteDatabase mDatabase = context.openOrCreateDatabase("TVProgressDB", context.MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, context);
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        BufferedReader reader = null;
        ArrayList<Episode> result = null;
        // HTTP Get
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            String jsonString = responseStrBuilder.toString();
            JSONObject root = new JSONObject(jsonString);

            for (Iterator iterator = root.keys(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                JSONArray season = root.getJSONArray(key);
                readEpisodesArray(season);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mCurrentActivity.onTaskCompleted(true);
    }

    public void readEpisodesArray(JSONArray rootObject) {
        ArrayList<Episode> episodes = new ArrayList<Episode>();

        try {
            for(int i=0; i < rootObject.length(); i++) {
                JSONObject jsonResource = rootObject.getJSONObject(i);

                int season = jsonResource.getInt("season");
                int episode = jsonResource.getInt("number");
                String release_date = jsonResource.getString("release_date");
                String title = jsonResource.getString("title");
                title = title.replace("'", "\'");

                String queryUpdate = "UPDATE episodes SET title = ?, release_date = ? WHERE showId = ? AND season = ? AND episode = ?";
                SQLiteStatement statementUpdate = mDatabaseConnection.getNewStatement(queryUpdate);
                statementUpdate.bindString(1, title);
                statementUpdate.bindString(2, release_date);
                statementUpdate.bindLong(3, mShowId);
                statementUpdate.bindLong(4, season);
                statementUpdate.bindLong(5, episode);
                int updateResult =  mDatabaseConnection.executeInsertQuery(statementUpdate);

                System.out.println("ShowId = " + mShowId + " Episode " + season + " Episode " + episode + " Update Result = " + updateResult);

                if(updateResult == -1) {
                    String queryInsert = "INSERT INTO episodes VALUES(?, ?, ?, ?, ?, 0)";
                    SQLiteStatement statementInsert = mDatabaseConnection.getNewStatement(queryInsert);
                    statementInsert.bindLong(1, mShowId);
                    statementInsert.bindLong(2, season);
                    statementInsert.bindLong(3, episode);
                    statementInsert.bindString(4, title);
                    statementInsert.bindString(5, release_date);
                    int insertResult = mDatabaseConnection.executeInsertQuery(statementInsert);

                    System.out.println("ShowId = " + mShowId + " Episode " + season + " Episode " + episode + " Insert result = " + insertResult);
                }
            }
        } catch (JSONException | SQLiteException ex){
            System.out.println(ex.getMessage());
        }
    }
}
