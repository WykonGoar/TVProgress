package com.example.wouter.tvprogress.model;

import android.media.Image;

/**
 * Created by Wouter on 11-11-2015.
 */
public class Show {

    private int mId;
    private String mTitle;
    private int mCurrentSeason = 1;
    private int mCurrentEpisode = 0;
    private int mLastSeason = -1;
    private int mLastEpisode = -1;
    private Image mShowImage;
    private Image mShowBanner;
    private String mURL;

    public Show(int id, String title)
    {
        mId = id;
        mTitle= title;
    }

    public Show(int mId, String mTitle, int mCurrentSeason, int mCurrentEpisode, int mLastSeason, int mLastEpisode, Image mShowImage, Image mShowBanner, String mURL) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mCurrentSeason = mCurrentSeason;
        this.mCurrentEpisode = mCurrentEpisode;
        this.mLastSeason = mLastSeason;
        this.mLastEpisode = mLastEpisode;
        this.mShowImage = mShowImage;
        this.mShowBanner = mShowBanner;
        this.mURL = mURL;
    }

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public int getCurrentSeason() { return mCurrentSeason;}

    public int getCurrentEpisode() { return mCurrentEpisode; }

    public int getLastSeason() { return mLastSeason; }

    public int getLastEpisode() { return mLastEpisode; }

    public Image getShowImage() { return mShowImage; }

    public Image getShowBanner() { return mShowBanner; }

    public String getURL() { return mURL; }

    public void setTitle(String title) {mTitle = title; }

    public void setCurrentSeason(int currentSeason) { mCurrentSeason = currentSeason; }

    public void setCurrentEpisode(int currentEpisode) { mCurrentEpisode = currentEpisode; }

    public void setLastSeason(int lastSeason) { this.mLastSeason = lastSeason; }

    public void setLastEpisode(int lastEpisode) { this.mLastEpisode = lastEpisode; }

    public void setURL(String url) { this.mURL = url; }

    public boolean isUpToDate(){
        if(mLastSeason != -1
                && mLastEpisode != -1
                && mCurrentSeason == mLastSeason
                && mCurrentEpisode == mLastEpisode)
        {
                return true;
        }
        return false;
    }
}
