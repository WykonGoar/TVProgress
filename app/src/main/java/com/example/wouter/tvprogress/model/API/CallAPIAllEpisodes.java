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
    private Context mContext;
    private int mShowId;
    private String urlString;
    private iOnTaskCompleted mCurrentActivity;
    private DatabaseConnection mDatabaseConnection;
    private String mToken;

    public CallAPIAllEpisodes(Context context, iOnTaskCompleted currentActivity, int showId, String resource){
        mContext = context;
        mShowId = showId;
        mCurrentActivity = currentActivity;
        urlString = resource;

        SQLiteDatabase mDatabase = context.openOrCreateDatabase("TVProgressDB", context.MODE_PRIVATE, null);
        mDatabaseConnection = new DatabaseConnection(mDatabase, context);
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        CallAPIAccessToken mCallAPIAccessToken = new CallAPIAccessToken(mContext);
        mToken = mCallAPIAccessToken.GetAccessToken();

        System.out.println("Token = " + mToken);
        if(mToken.isEmpty()){
            System.err.println("Token empty");
            return null;
        }

        return getEpisodesArray("1");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mCurrentActivity.onTaskCompleted(true);
    }

    public boolean getEpisodesArray(String page){
        BufferedReader reader = null;
        ArrayList<Episode> result = null;
        // HTTP Get
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + mToken);
            urlConnection.setRequestProperty("Page", page);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 401) {
                System.err.println("Get shows : Not Authorized");
                return false;
            }
            else if(responseCode == 404) {
                System.err.println("Get shows : Not found");
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        } catch (Exception e ){
            System.err.println(e);
            System.err.println("Raise exception : " + e.getMessage());
            return false;
        }

        try{
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            readEpisodesArray(responseStrBuilder.toString());
        } catch(Exception ex){
            System.err.println(ex.getMessage());
            return false;
        }

        return true;
    }

    public void readEpisodesArray(String jsonString) {
        ArrayList<Episode> episodes = new ArrayList<Episode>();
        String newPage = null;

        try {
            JSONObject jsonResource = new JSONObject(jsonString);

            newPage = jsonResource.getJSONObject("links").getString("next");

            JSONArray list = jsonResource.getJSONArray("data");

            for (int i = 0; i < list.length(); i++) {
                JSONObject object = list.getJSONObject(i);

                int season = object.getInt("airedSeason");
                int episode = object.getInt("airedEpisodeNumber");
                String release_date = object.getString("firstAired");
                String title = object.getString("episodeName");
                title = title.replace("'", "\'");

                String queryUpdate = "UPDATE episodes SET title = ?, release_date = ? WHERE showId = ? AND season = ? AND episode = ?";
                SQLiteStatement statementUpdate = mDatabaseConnection.getNewStatement(queryUpdate);
                statementUpdate.bindString(1, title);
                statementUpdate.bindString(2, release_date);
                statementUpdate.bindLong(3, mShowId);
                statementUpdate.bindLong(4, season);
                statementUpdate.bindLong(5, episode);
                int updateResult =  mDatabaseConnection.executeInsertQuery(statementUpdate);

                System.out.println("ShowId = " + mShowId + " Season " + season + " Episode " + episode + " Update Result = " + updateResult);

                if(updateResult == -1) {
                    String queryInsert = "INSERT INTO episodes VALUES(?, ?, ?, ?, ?, 0)";
                    SQLiteStatement statementInsert = mDatabaseConnection.getNewStatement(queryInsert);
                    statementInsert.bindLong(1, mShowId);
                    statementInsert.bindLong(2, season);
                    statementInsert.bindLong(3, episode);
                    statementInsert.bindString(4, title);
                    statementInsert.bindString(5, release_date);
                    int insertResult = mDatabaseConnection.executeInsertQuery(statementInsert);

                    System.out.println("ShowId = " + mShowId + " Season " + season + " Episode " + episode + " Insert result = " + insertResult);
                }
            }
        } catch (JSONException | SQLiteException ex){
            System.out.println(ex.getMessage());
        }

        if(newPage != null){
            getEpisodesArray(newPage);
        }
    }
}
