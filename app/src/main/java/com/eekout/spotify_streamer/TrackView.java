package com.eekout.spotify_streamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Model backing a track view in spotify streamer.
 */
public class TrackView extends BaseItemView {
    private String album;
    private String previewUrl;

    public TrackView(String name, String smallImageUrl, String largeImageUrl, String album, String previewUrl) {
        super(name, smallImageUrl, largeImageUrl);
        this.album = album;
        this.previewUrl = previewUrl;
    }

    public TrackView(Parcel in) {
        super(in);
        readFromParcel(in);
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(album);
        dest.writeString(previewUrl);
    }

    private void readFromParcel(Parcel in) {
        album = in.readString();
        previewUrl = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<TrackView>() {
        @Override
        public TrackView createFromParcel(Parcel source) {
            return new TrackView(source);
        }

        @Override
        public TrackView[] newArray(int size) {
            return new TrackView[0];
        }
    };
}
