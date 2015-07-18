package com.eekout.spotify_streamer;

import android.net.Uri;
import android.util.Patterns;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Utility class for retrieving image urls.
 */
public final class CommonsUrlUtil {

    private CommonsUrlUtil() {
        // No Instantiation
    }

    public static String largeImage(final List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return sanitizeUrl(images.get(getLargeImageIndex(images)).url);
        }
        return null;
    }

    public static String smallImage(final List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return sanitizeUrl(images.get(getSmallImageIndex(images)).url);
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

    public static String sanitizeUrl(String url) {
        if (url == null
                || url.isEmpty()
                || !Patterns.WEB_URL.matcher(url).matches()) {
            return null;
        }

        return url;
    }
}
