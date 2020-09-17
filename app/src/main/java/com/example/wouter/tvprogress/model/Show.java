package com.example.wouter.tvprogress.model;import android.content.Context;import android.database.sqlite.SQLiteStatement;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.widget.Toast;import java.io.File;/** * Created by Wouter on 11-11-2015. */public class Show {    private int mId = -1;    private String mTitle = "";    private int mCurrentSeason = 1;    private int mCurrentEpisode = 0;    private String mBanner = "";    private String mURL = "";    private String mStatus = "";    private float mRating = 0.0f;    private Boolean mWatchedAll = false;    public Show(){}    public Show(int id_, String title) {        this.mId = id_;        this.mTitle = title;    }    public Show(int mId, String mTitle, int mCurrentSeason, int mCurrentEpisode, String mBanner, String mURL, String mStatus) {        this.mId = mId;        this.mTitle = mTitle;        this.mCurrentSeason = mCurrentSeason;        this.mCurrentEpisode = mCurrentEpisode;        this.mBanner = mBanner;        this.mURL = mURL;        this.mStatus = mStatus;    }    public int getId() { return mId; }    public String getTitle() { return mTitle; }    public int getCurrentSeason() { return mCurrentSeason;}    public int getCurrentEpisode() { return mCurrentEpisode; }    public Bitmap getBannerAsImage() {        if (mBanner.isEmpty() || mBanner == null)            return null;        File f = new File(mBanner);        if (!f.exists())        {            mBanner = "";            return null;        }        return BitmapFactory.decodeFile(mBanner);    }    public String getBanner() { return mBanner; }    public String getURL() { return mURL; }    public String getStatus() { return mStatus; }    public float getRating() {        return mRating;    }    public Boolean getWatchedAll() {        return mWatchedAll;    }    public void setId (int id) {mId = id;}    public void setTitle(String title) {mTitle = title; }    public void setCurrentSeason(int currentSeason) { mCurrentSeason = currentSeason; }    public void setCurrentEpisode(int currentEpisode) { mCurrentEpisode = currentEpisode; }    public void setBanner(String path) {this.mBanner = path; }    public void setURL(String url) { this.mURL = url; }    public void setStatus(String status) { this.mStatus = status; }    public void setRating(float rating) {        this.mRating = rating;    }    public void setWatchedAll(Boolean watchedAll) {        this.mWatchedAll = watchedAll;    }    public Boolean save(DatabaseConnection databaseConnection) {        if (mId == -1) {            insertShow(databaseConnection);        }        else {            updateShow(databaseConnection);        }        return true;    }    public boolean delete(DatabaseConnection databaseConnection) {        SQLiteStatement statement = databaseConnection.getNewStatement(                "DELETE FROM shows WHERE _id = ?;"        );        statement.bindDouble(1, mId);        databaseConnection.executeUpdateQuery(statement);        return true;    }    private void insertShow(DatabaseConnection databaseConnection) {        SQLiteStatement statement = databaseConnection.getNewStatement(                "INSERT INTO shows(" +                        "title, " + // Index: 1                        "rating, " + // Index: 2                        "watched_all " + // Index: 3                        ")" +                        "VALUES (?, ?, ?);"        );        statement.bindString(1, mTitle);        statement.bindDouble(2, mRating);        statement.bindDouble(3, 0);        if (mWatchedAll){            statement.bindDouble(3, 1);        }        mId = databaseConnection.executeInsertQuery(statement);        System.out.println("New id " + mId);    }    private void updateShow(DatabaseConnection databaseConnection) {        SQLiteStatement statement = databaseConnection.getNewStatement(                "UPDATE shows SET " +                        "title = ?, " + // Index: 1                        "rating = ?, " + // Index: 2                        "watched_all = ?, " + // Index: 3                        "currentSeason = ?, " + // Index: 4                        "currentEpisode = ? " + // Index: 5                        "WHERE _id = ?" // Index: 6        );        statement.bindDouble(6, mId);        statement.bindString(1, mTitle);        statement.bindDouble(2, mRating);        statement.bindDouble(3, 0);        if (mWatchedAll){            statement.bindDouble(3, 1);        }        statement.bindDouble(4, mCurrentSeason);        statement.bindDouble(5, mCurrentEpisode);        databaseConnection.executeUpdateQuery(statement);    }}