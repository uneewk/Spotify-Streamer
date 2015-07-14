package com.eekout.spotify_streamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Utility class for retrieving image urls.
 */
public final class ImageUrlUtil {

    public static String largeImage(final List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(getLargeImageIndex(images)).url;
        }
        return null;
    }

    public static String smallImage(final List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(getSmallImageIndex(images)).url;
        }
        return null;
    }

    private static int getSmallImageIndex(List<Image> images) {
        int fourSizes = 4;
        int threeSizes = 3;
        int smallIndex = 2;

        if (images.size() == fourSizes || images.size() == threeSizes) {
            return images.size() - smallIndex;
        }
        return 0;
    }

    private static int getLargeImageIndex(List<Image> images) {
        int fourSizes = 4;
        int threeSizes = 3;
        int largeIndex = 3;

        if (images.size() == fourSizes || images.size() == threeSizes) {
            return images.size() - largeIndex;
        }
        return 0;
    }
}
