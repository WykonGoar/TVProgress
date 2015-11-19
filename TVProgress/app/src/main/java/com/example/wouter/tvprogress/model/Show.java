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

    public int getId() { return mId; }

    public String getTitle() { return mTitle; }

    public int getCurrentSeason() { return mCurrentSeason;}

    public int getCurrentEpisode() { return mCurrentSeason; }

    public int getmLastSeason() { return mLastSeason; }

    public int getmLastEpisode() { return mLastEpisode; }

    public Image getShowImage() { return mShowImage; }

    public Image getShowBanner() { return mShowBanner; }

    public String getURL() { return mURL; }

    public void setTitle(String title) {mTitle = title; }

    public void setCurrentSeason(int currentSeason) { mCurrentSeason = currentSeason; }

    public void setCurrentEpisode(int currentEpisode) { mCurrentEpisode = currentEpisode; }

    public void setLastSeason(int lastSeason) { this.mLastSeason = lastSeason; }

    public void setLastEpisode(int lastEpisode) { this.mLastEpisode = lastEpisode; }

    public void setURL(String url) { this.mURL = url; }
}
