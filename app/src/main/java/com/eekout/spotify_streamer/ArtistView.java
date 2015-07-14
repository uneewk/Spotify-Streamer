package com.eekout.spotify_streamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Model backing an artist view in spotify streamer.
 */
public class ArtistView extends BaseItemView {
    private String spotifyId;

    public ArtistView(String name, String smallImageUrl, String spotifyId) {
        super(name, smallImageUrl);
        this.spotifyId = spotifyId;
    }

    public ArtistView(Parcel in) {
        super(in);
        readFromParcel(in);
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(spotifyId);
    }

    private void readFromParcel(Parcel in) {
        spotifyId = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<ArtistView>() {
        @Override
        public ArtistView createFromParcel(Parcel source) {
            return new ArtistView(source);
        }

        @Override
        public ArtistView[] newArray(int size) {
            return new ArtistView[0];
        }
    };
}
