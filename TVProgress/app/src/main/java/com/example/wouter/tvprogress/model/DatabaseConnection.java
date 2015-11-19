package com.example.wouter.tvprogress.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by Wouter on 19-11-2015.
 */
public class DatabaseConnection extends Activity {

    SQLiteDatabase mDatabase;
    Context mContext;
    Logger log;

    public DatabaseConnection(SQLiteDatabase database, Context context){
        mDatabase = database;
        mContext = context;

        try{
            String check = "SELECT * FROM shows";

            executeReturn(check);
        } catch (SQLiteException ex)
        {
            importDatabaseTables();
            importShows();
        }

    }

    private void importDatabaseTables(){
        try {
            InputStream mInputStream = mContext.getAssets().open("TVProgressTables.sql");

            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            String line = null;
            do {
                line = mBufferedReader.readLine();
                if (line != null) {
                    executeNonReturn(line);
                }
            } while (line != null);
        }catch (IOException ex){
            throw new Error(ex.getMessage());
        }
    }

    private void importShows(){
        try {
            InputStream mInputStream = mContext.getAssets().open("NewShows.sql");

            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            String line = null;
            do {
                line = mBufferedReader.readLine();
                if (line != null) {
                    try {
                        executeNonReturn(line);
                    }
                    catch (SQLiteException ex) {
                        throw new Error(ex.getMessage());
                    }
                }
            } while (line != null);
        }catch (IOException ex){
            throw new Error(ex.getMessage());
        }
    }

    public void executeNonReturn(String query) throws  SQLiteException{
        mDatabase.execSQL(query);
    }

    public Cursor executeReturn(String query) throws SQLiteException{
        return mDatabase.rawQuery(query, null);
    }

    public LinkedList<Show> getShows(String query){
        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        mCursor.moveToFirst();

        LinkedList<Show> shows = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //Id
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //title
            String title = mCursor.getString(mCursor.getColumnIndex("title"));
            //currentSeason
            int currentSeason = mCursor.getInt(mCursor.getColumnIndex("currentSeason"));
            //currentEpisode
            int currentEpisode = mCursor.getInt(mCursor.getColumnIndex("currentEpisode"));
            //lastSeason
            int lastSeason = mCursor.getInt(mCursor.getColumnIndex("lastSeason"));
            //lastEpisode
            int lastEpisode = mCursor.getInt(mCursor.getColumnIndex("lastEpisode"));
            //image
            //int image = mCursor.getInt(mCursor.getColumnIndex("image"));
            // banner
            //int banner = mCursor.getInt(mCursor.getColumnIndex("banner"));
            //url
            String url = mCursor.getString(mCursor.getColumnIndex("url"));

            Show show = new Show(id, title, currentSeason, currentEpisode,  lastSeason, lastEpisode, null, null, url);
            shows.add(show);

            mCursor.moveToNext();
        }

        return  shows;
    }

    public LinkedList<Episode> getEpisodes(int showId)
    {
        String query = "SELECT * FROM episodes WHERE showId = " + showId + " ORDER BY season DESC, episode DESC";
        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        mCursor.moveToFirst();

        LinkedList<Episode> episodes = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //id
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            //showId
            int currentShowId = mCursor.getInt(mCursor.getColumnIndex("showId"));
            //season
            int season = mCursor.getInt(mCursor.getColumnIndex("season"));
            //episode
            int episode = mCursor.getInt(mCursor.getColumnIndex("episode"));
            //title
            String title = mCursor.getString(mCursor.getColumnIndex("title"));
            //seen
            int iSeen = mCursor.getInt(mCursor.getColumnIndex("seen"));
            Boolean seen = false;
            if(iSeen == 1)
                seen = true;

            Episode newEpisode = new Episode(id, currentShowId, season, episode, title, seen);
            episodes.add(newEpisode);

            mCursor.moveToNext();
        }

        return  episodes;
    }
}

