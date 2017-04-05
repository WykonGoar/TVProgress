package com.example.wouter.tvprogress.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

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

    //public DatabaseConnection(SQLiteDatabase database, Context context){
    public DatabaseConnection(Context context){
        //mDatabase = database;
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

    private void createConnection(){
        mDatabase = mContext.openOrCreateDatabase("TVProgressDB", MODE_PRIVATE, null);
    }

    public SQLiteStatement getNewStatement(String query){
        createConnection();
        return mDatabase.compileStatement(query);
    }

    public void executeNonReturn(String query) throws  SQLiteException{
        createConnection();
        mDatabase.execSQL(query);
        mDatabase.close();
    }

    public void executeNonReturn(SQLiteStatement statement){
        statement.execute();
        mDatabase.close();
    }

    public int executeInsertQuery(SQLiteStatement statement){
        int result = (int) statement.executeInsert();
        mDatabase.close();
        return result;
    }

    public int executeUpdateQuery(SQLiteStatement statement){
        int result = statement.executeUpdateDelete();
        mDatabase.close();
        return result;
    }

    public Cursor executeReturn(String query) throws SQLiteException {
        createConnection();
        Cursor mCursor = mDatabase.rawQuery(query, null);
        return mCursor;
    }

    public LinkedList<Show> getShows(String query){
        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
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
            // banner
            String banner = mCursor.getString(mCursor.getColumnIndex("banner"));
            //url
            String url = mCursor.getString(mCursor.getColumnIndex("url"));
            //status
            String status = mCursor.getString(mCursor.getColumnIndex("status"));

            Show show = new Show(id, title, currentSeason, currentEpisode, banner, url, status);
            shows.add(show);

            mCursor.moveToNext();
        }

        mDatabase.close();

        return  shows;
    }

    public Episode getNextpisode(int showId){
        String query = "SELECT * FROM episodes WHERE showId = " + showId + " AND seen = 0 ORDER BY season, episode";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
        mCursor.moveToFirst();

        if(mCursor.getCount() == 0)
            return  null;

        //showId
        int currentShowId = mCursor.getInt(mCursor.getColumnIndex("showId"));
        //season
        int season = mCursor.getInt(mCursor.getColumnIndex("season"));
        //episode
        int episode = mCursor.getInt(mCursor.getColumnIndex("episode"));
        //title
        String title = mCursor.getString(mCursor.getColumnIndex("title"));
        //release date
        String releaseDate = mCursor.getString(mCursor.getColumnIndex("release_date"));
        //seen
        int iSeen = mCursor.getInt(mCursor.getColumnIndex("seen"));
        Boolean seen = false;
        if(iSeen == 1)
            seen = true;

        mDatabase.close();

        Episode nextEpisode = new Episode(currentShowId, season, episode, title, releaseDate, seen);
        return nextEpisode;
    }

    public Episode getLastSeenEpisode(int showId){
        String query = "SELECT * FROM episodes WHERE showId = " + showId + " AND seen = 1 ORDER BY season DESC, episode DESC";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
        mCursor.moveToFirst();

        if(mCursor.getCount() == 0)
            return  null;

        //showId
        int currentShowId = mCursor.getInt(mCursor.getColumnIndex("showId"));
        //season
        int season = mCursor.getInt(mCursor.getColumnIndex("season"));
        //episode
        int episode = mCursor.getInt(mCursor.getColumnIndex("episode"));
        //title
        String title = mCursor.getString(mCursor.getColumnIndex("title"));
        //release date
        String releaseDate = mCursor.getString(mCursor.getColumnIndex("release_date"));
        //seen
        int iSeen = mCursor.getInt(mCursor.getColumnIndex("seen"));
        Boolean seen = false;
        if(iSeen == 1)
            seen = true;

        mDatabase.close();

        Episode nextEpisode = new Episode(currentShowId, season, episode, title, releaseDate, seen);
        return nextEpisode;
    }

    public LinkedList<Episode> getEpisodes(int showId)
    {
        String query = "SELECT * FROM episodes WHERE showId = " + showId + " ORDER BY season DESC, episode DESC";
        Cursor mCursor = null;

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        mCursor.moveToFirst();

        LinkedList<Episode> episodes = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            //showId
            int currentShowId = mCursor.getInt(mCursor.getColumnIndex("showId"));
            //season
            int season = mCursor.getInt(mCursor.getColumnIndex("season"));
            //episode
            int episode = mCursor.getInt(mCursor.getColumnIndex("episode"));
            //title
            String title = mCursor.getString(mCursor.getColumnIndex("title"));
            //release date
            String releaseDate = mCursor.getString(mCursor.getColumnIndex("release_date"));
            //seen
            int iSeen = mCursor.getInt(mCursor.getColumnIndex("seen"));
            Boolean seen = false;
            if(iSeen == 1)
                seen = true;

            Episode newEpisode = new Episode(currentShowId, season, episode, title, releaseDate, seen);
            episodes.add(newEpisode);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return  episodes;
    }
}

