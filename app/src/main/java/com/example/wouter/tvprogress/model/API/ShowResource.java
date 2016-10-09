package com.example.wouter.tvprogress.model.API;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wouter on 23-11-2015.
 */
public class ShowResource implements Parcelable {
    private String mTitle;
    private String mURL;
    private String mStatus;

    public ShowResource() {    }

    public ShowResource(Parcel in){
        String[] data = in.createStringArray();
        mTitle = data[0];
        mURL = data[1];
        mStatus = data[2];
    }

    public String getTitle() {
        return mTitle;
    }

    public String getURL() {
        return mURL;
    }

    public String getStatus(){
        return mStatus;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setURL(String url) {
        this.mURL = url;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public static final Parcelable.Creator<ShowResource> CREATOR = new Parcelable.Creator<ShowResource>() {
        public ShowResource createFromParcel(Parcel in) {
            return new ShowResource(in);
        }

        public ShowResource[] newArray(int size) {
            return new ShowResource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[]{mTitle, mURL, mStatus};
        dest.writeStringArray(data);
    }
}
