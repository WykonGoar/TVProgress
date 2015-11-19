package com.example.wouter.tvprogress.model;

/**
 * Created by Wouter on 17-11-2015.
 */
public class Episode {

    private int mId;
    private int mShowId;
    private int mSeason;
    private int mEpisode;
    private String mTitle;
    private boolean mSeen = false;

    public Episode(int showId){
        mShowId = showId;
    }

    public Episode(int id, int showId, int season, int episode, String title, boolean seen) {
        this.mId = id;
        this.mShowId = showId;
        this.mSeason = season;
        this.mEpisode = episode;
        this.mTitle = title;
        this.mSeen = seen;
    }

    public int getId() { return mId; }

    public int getSeason() { return mSeason; }

    public int getEpisode() { return mEpisode; }

    public String getTitle() { return mTitle; }

    public boolean isSeen() { return mSeen; }

    public void setmSeason(int season) { this.mSeason = season; }

    public void setmEpisode(int episode) { this.mEpisode = episode; }

    public void setmTitle(String title) { this.mTitle = title; }

    public void setmSeen(boolean seen) { this.mSeen = seen; }
}
