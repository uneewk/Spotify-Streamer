package com.eekout.spotify_streamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Base model backing an item view in spotify streamer.
 */
public class BaseItemView implements ViewInfo {
    private String name;
    private String largeImageUrl;
    private String smallImageUrl;

    public BaseItemView(String name, String smallImageUrl) {
        this.name = name;
        this.smallImageUrl = smallImageUrl;
    }

    public BaseItemView(String name, String smallImageUrl, String largeImageUrl) {
        this.name = name;
        this.smallImageUrl = smallImageUrl;
        this.largeImageUrl = largeImageUrl;
    }

    public BaseItemView(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    @Override
    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    @Override
    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    @Override
    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(smallImageUrl);
        dest.writeString(largeImageUrl);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        smallImageUrl = in.readString();
        largeImageUrl = in.readString();
    }

}
