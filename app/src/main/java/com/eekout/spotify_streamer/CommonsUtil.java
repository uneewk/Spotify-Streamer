package com.eekout.spotify_streamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class with common methods.
 */
public final class CommonsUtil {

    // App keys
    public static final String ARTIST_RESULTS_KEY = "artistResultsKey";
    public static final String TOP_TEN_TRACKS_SUBTITLE_KEY = "topTenTracksSubtitleKey";
    public static final String VIEW_INFOS_KEY = "viewInfosKey";
    public static final String ARTIST_COUNTRY_KEY = "country";

    // App messages
    public static final String EMPTY_SEARCH_RESULTS = "No artist were found with name: %s.";
    public static final String EMPTY_TOP_TEN_RESULTS = "No tracks found for artist: %s. Refine your search.";
    public static final String SPOTIFY_API_COMMUNICATION_FAILED = "Error occurred communicating with the spotify API. Try again later.";
    public static final String NETWORK_CONNECTION_UNAVAILABLE = "No Network Connectivity. Connect your device and try again";

    private CommonsUtil() {
        // No Instantiation
    }

    public static boolean hasNetworkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
