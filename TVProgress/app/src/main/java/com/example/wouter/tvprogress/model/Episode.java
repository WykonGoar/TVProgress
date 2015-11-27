package com.example.wouter.tvprogress.model;

/**
 * Created by Wouter on 17-11-2015.
 */
public class Episode {

    private int mShowId;
    private int mSeason;
    private int mEpisode;
    private String mTitle;
    private boolean mSeen = false;

    public Episode(int showId){
        mShowId = showId;
    }

    public Episode(int showId, int season, int episode, String title, boolean seen) {
        this.mShowId = showId;
        this.mSeason = season;
        this.mEpisode = episode;
        this.mTitle = title;
        this.mSeen = seen;
    }

    public int getShowId() { return mShowId; }

    public int getSeason() { return mSeason; }

    public int getEpisode() { return mEpisode; }

    public String getTitle() { return mTitle; }

    public boolean isSeen() { return mSeen; }

    public void setSeason(int season) { this.mSeason = season; }

    public void setEpisode(int episode) { this.mEpisode = episode; }

    public void setTitle(String title) { this.mTitle = title; }

    public void setmSeen(boolean seen) { this.mSeen = seen; }
}
