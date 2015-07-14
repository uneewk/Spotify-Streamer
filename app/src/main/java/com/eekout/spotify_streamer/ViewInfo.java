package com.eekout.spotify_streamer;

import android.os.Parcelable;

import java.util.List;

/**
 * Interface for various spotify list views.
 */
public interface ViewInfo extends Parcelable {

    /**
     * Returns name of the view item.
     * @return
     */
    String getName();

    /**
     * Sets the name of the view item.
     * @param name of the view
     */
    void setName(String name);

    /**
     * Returns the url of a large image for this view.
     * This is the large image version to use in the view when a large image is required.
     * @return
     */
    String getLargeImageUrl();

    /**
     * Sets large image url for this view.
     * @param imageUrl the large image url
     */
    void setLargeImageUrl(String imageUrl);

    /**
     * Returns the url of a small image for this view.
     * This is the thumbnail version of the image
     * @return
     */
    String getSmallImageUrl();

    /**
     * Sets small image url for this view.
     * @param imageUrl the small image url
     */
    void setSmallImageUrl(String imageUrl);

}
