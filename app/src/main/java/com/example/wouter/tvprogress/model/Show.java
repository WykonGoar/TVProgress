package com.example.wouter.tvprogress.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by Wouter on 11-11-2015.
 */
public class Show {

    private int mId = -1;
    private String mTitle = "";
    private int mCurrentSeason = 1;
    private int mCurrentEpisode = 0;
    private String mBanner = "";
    private String mURL = "";
    private String mStatus = "";

    public Show(){}

    public Show(int mId, String mTitle, int mCurrentSeason, int mCurrentEpisode, String mBanner, String mURL, String mStatus) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mCurrentSeason = mCurrentSeason;
        this.mCurrentEpisode = mCurrentEpisode;
        this.mBanner = mBanner;
        this.mURL = mURL;
        this.mStatus = mStatus;
    }

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public int getCurrentSeason() { return mCurrentSeason;}

    public int getCurrentEpisode() { return mCurrentEpisode; }

    public Bitmap getBannerAsImage() {
        if (mBanner.isEmpty() || mBanner == null)
            return null;

        File f = new File(mBanner);
        if (!f.exists())
        {
            mBanner = "";
            return null;
        }

        return BitmapFactory.decodeFile(mBanner);
    }

    public String getBanner() { return mBanner; }

    public String getURL() { return mURL; }

    public String getStatus() { return mStatus; }

    public void setId (int id) {mId = id;}

    public void setTitle(String title) {mTitle = title; }

    public void setCurrentSeason(int currentSeason) { mCurrentSeason = currentSeason; }

    public void setCurrentEpisode(int currentEpisode) { mCurrentEpisode = currentEpisode; }

    public void setBanner(String path) {this.mBanner = path; }

    public void setURL(String url) { this.mURL = url; }

    public void setStatus(String status) { this.mStatus = status; }
}
