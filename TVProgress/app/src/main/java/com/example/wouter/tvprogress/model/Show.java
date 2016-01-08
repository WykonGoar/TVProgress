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
    private int mLastSeason = -1;
    private int mLastEpisode = -1;
    private String mImage = "";
    private String mBanner = "";
    private String mURL = "";

    public Show(){}

    public Show(int mId, String mTitle, int mCurrentSeason, int mCurrentEpisode, int mLastSeason, int mLastEpisode, String mImage, String mBanner, String mURL) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mCurrentSeason = mCurrentSeason;
        this.mCurrentEpisode = mCurrentEpisode;
        this.mLastSeason = mLastSeason;
        this.mLastEpisode = mLastEpisode;
        this.mImage = mImage;
        this.mBanner = mBanner;
        this.mURL = mURL;
    }

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public int getCurrentSeason() { return mCurrentSeason;}

    public int getCurrentEpisode() { return mCurrentEpisode; }

    public int getLastSeason() { return mLastSeason; }

    public int getLastEpisode() { return mLastEpisode; }

    public Bitmap getImageAsImage() {
        if(mImage.isEmpty() || mImage == null)
            return  null;

        File f = new File(mImage);
        if (!f.exists())
        {
            mImage = "";
            return null;
        }
        return BitmapFactory.decodeFile(mImage);

    }

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

    public String getImage() {return mImage;}

    public String getBanner() { return mBanner; }

    public String getURL() { return mURL; }

    public void setId (int id) {mId = id;}

    public void setTitle(String title) {mTitle = title; }

    public void setCurrentSeason(int currentSeason) { mCurrentSeason = currentSeason; }

    public void setCurrentEpisode(int currentEpisode) { mCurrentEpisode = currentEpisode; }

    public void setLastSeason(int lastSeason) { this.mLastSeason = lastSeason; }

    public void setLastEpisode(int lastEpisode) { this.mLastEpisode = lastEpisode; }

    public void setImage(String path) {this.mImage = path; }

    public void setBanner(String path) {this.mBanner = path; }

    public void setURL(String url) { this.mURL = url; }

    public boolean isUpToDate() {
        if (mLastSeason != -1
                && mLastEpisode != -1
                && mCurrentSeason == mLastSeason
                && mCurrentEpisode == mLastEpisode) {
            return true;
        }
        return false;
    }
}
