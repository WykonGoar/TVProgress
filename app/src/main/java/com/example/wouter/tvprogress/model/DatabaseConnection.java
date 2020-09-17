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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by WykonGoar on 11-09-2020.
 */
public class DatabaseConnection extends Activity {

    SQLiteDatabase mDatabase;
    Context mContext;

    public DatabaseConnection(Context context){
        mContext = context;

        Cursor cursor = null;
        try {
            String check = "SELECT version FROM db_version";

            executeReturn(check);

        }
        catch (SQLiteException ex) {
            updateDatabase(0);
            return;
        }

        String check = "SELECT version FROM db_version";

        cursor = executeReturn(check);

        cursor.moveToFirst();

        if(cursor.isAfterLast()) {
            updateDatabase(0);
        }
        int version = cursor.getInt(cursor.getColumnIndex("version"));

        updateDatabase(version);
    }

    private void updateDatabase(int lastVersion) {
        List<String> filesNames;
        try {
            filesNames = Arrays.asList(mContext.getAssets().list("database"));
        }
        catch (IOException ex) {
            throw new Error(ex.getMessage());
        }

        HashMap<Integer, List<String>> sqlStatements = new HashMap<>();
        int versionCounter = lastVersion + 1;
        while (filesNames.contains(String.format("v%d.sql", versionCounter))) {
            sqlStatements.put(versionCounter, readSQLFile(String.format("v%d.sql", versionCounter)));
            versionCounter ++;
        }

        int updateVersion = lastVersion + 1;
        while (sqlStatements.containsKey(updateVersion)) {
            List<String> statements = sqlStatements.get(updateVersion);

            for (int i = 0; i < statements.size(); i++) {
                String statement = statements.get(i);

                System.out.println("Execute: " + statement);

                if (statement.startsWith("INSERT")) {
                    executeInsertQuery(getNewStatement(statement));
                } else {
                    executeNonReturn(statement);
                }
            }

            //Update version
            if (1 == updateVersion) {
                executeInsertQuery(getNewStatement(
                        String.format("INSERT INTO db_version VALUES (%d);", updateVersion)
                ));
            }
            else {
                executeNonReturn(String.format("UPDATE db_version SET version = %d;", updateVersion));
            }

            updateVersion ++;
        }
    }

    private List<String> readSQLFile(String fileName) {
        String fileContent = null;

        try {
            InputStream mInputStream = mContext.getAssets().open("database/" + fileName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\n", "");
                line = line.replaceAll("\r", "");

                if (fileContent == null) {
                    fileContent = line;
                }
                else {
                    fileContent += " " + line;
                }
            }
        } catch (IOException ex){
            throw new Error(ex.toString());
        }

        ArrayList<String> sqlStatements = new ArrayList<>();
        if (fileContent == null) {
            return sqlStatements;
        }

        for (String query : fileContent.split(";")) {
            sqlStatements.add(query + ";");
        }

        return sqlStatements;
    }

    private void createConnection(){
        mDatabase = mContext.openOrCreateDatabase("tvprogressdb", MODE_PRIVATE, null);
    }

    public SQLiteStatement getNewStatement(String query){
        createConnection();
        return mDatabase.compileStatement(query);
    }

    public void executeNonReturn(String query) throws SQLiteException {
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
        System.out.println("Update result " + result);
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

            float rating = mCursor.getFloat(mCursor.getColumnIndex("rating"));
            int iWatchedAll = mCursor.getInt(mCursor.getColumnIndex("watched_all"));

            Show show = new Show(id, title);
            show.setCurrentSeason(currentSeason);
            show.setCurrentEpisode(currentEpisode);
            show.setRating(rating);
            show.setWatchedAll(iWatchedAll == 1);
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

