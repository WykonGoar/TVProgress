package com.example.wouter.tvprogress.model;

/**
 * Created by Wouter on 17-11-2015.
 */
public class Episode {

    private int mShowId;
    private int mSeason;
    private int mEpisode;
    private String mTitle;
    private String mReleaseDate;
    private boolean mSeen = false;

    public Episode(int showId){
        mShowId = showId;
    }

    public Episode(int showId, int season, int episode, String title, String releaseDate, boolean seen) {
        mShowId = showId;
        mSeason = season;
        mEpisode = episode;
        mTitle = title;
        mReleaseDate = releaseDate;
        mSeen = seen;
    }

    public int getShowId() { return mShowId; }

    public int getSeason() { return mSeason; }

    public int getEpisode() { return mEpisode; }

    public String getTitle() { return mTitle; }

    public String getReleaseDate() {return  mReleaseDate; }

    public boolean isSeen() { return mSeen; }

    public void setSeason(int season) { mSeason = season; }

    public void setEpisode(int episode) { mEpisode = episode; }

    public void setTitle(String title) { mTitle = title; }

    public void setReleaseDate(String releaseDate){ mReleaseDate = releaseDate; }

    public void setmSeen(boolean seen) { mSeen = seen; }
}
