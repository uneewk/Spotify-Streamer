package com.eekout.spotify_streamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTenTracksActivityFragment extends Fragment {
    private SpotifyStreamerListAdapter mTopTenSpotifyAdapter;
    private List<ViewInfo> viewInfos = new ArrayList<ViewInfo>();
    private static final String VIEW_INFOS_KEY = "viewInfosKey";

    public TopTenTracksActivityFragment() {
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ViewInfo[] values = (ViewInfo[]) savedInstanceState.getParcelableArray(VIEW_INFOS_KEY);
            if (values != null) {
                mTopTenSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), new ArrayList<ViewInfo>(Arrays.asList(values)));
            }
        } else {
            mTopTenSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), new ArrayList<ViewInfo>());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.top_ten_view);
        listView.setAdapter(mTopTenSpotifyAdapter);

        Intent topTenTracksIntent = getActivity().getIntent();
        String spotifyId = topTenTracksIntent.getStringExtra(Intent.EXTRA_TEXT);
        String artist = topTenTracksIntent.getStringExtra(MainActivityFragment.TOP_TEN_TRACKS_SUBTITLE_KEY);

        new TopTenTracksSpotifyTask().execute(spotifyId, artist);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        ViewInfo[] values = new ViewInfo[mTopTenSpotifyAdapter.getCount()];
        for (int i = 0; i < mTopTenSpotifyAdapter.getCount(); i++) {
            values[i] = mTopTenSpotifyAdapter.getItem(i);
        }
        savedState.putParcelableArray(VIEW_INFOS_KEY, values);
    }

    private class TopTenTracksSpotifyTask extends AsyncTask<String, Void, Tracks> {
        private final String LOG_TAG = TopTenTracksSpotifyTask.class.getSimpleName();
        private static final String ARTIST_COUNTRY_KEY = "country";
        private static final String ARTIST_COUNTRY_CODE = "US";
        private static final String EMPTY_TOP_TEN_RESULTS = "No tracks found for artist: %s. Refine your search";
        private String artist;

        @Override
        protected Tracks doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String artistId = params[0];
            artist = params[1];
            Log.d(LOG_TAG, "Getting top tracks for artist: " + artist);

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put(ARTIST_COUNTRY_KEY, ARTIST_COUNTRY_CODE);
            return spotify.getArtistTopTrack(artistId, queryMap);
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            viewInfos = toViewInfos(tracks);

            if (viewInfos.isEmpty()) {
                notifyEmptyTopTenTracks();
            } else {
                if (mTopTenSpotifyAdapter == null) {
                    mTopTenSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), viewInfos);
                } else {
                    mTopTenSpotifyAdapter.clear();
                    mTopTenSpotifyAdapter.addAll(viewInfos);
                }
            }
        }

        private void notifyEmptyTopTenTracks() {
            Toast.makeText(getActivity(), String.format(EMPTY_TOP_TEN_RESULTS, artist), Toast.LENGTH_SHORT).show();
        }

        private List<ViewInfo> toViewInfos(Tracks tracks) {
            List<ViewInfo> views = new ArrayList<ViewInfo>();

            if (tracks != null) {
                for (Track track : tracks.tracks) {
                    views.add(toViewInfo(track));
                }
            }
            return views;
        }

        private ViewInfo toViewInfo(Track track) {
            Log.d(LOG_TAG, "name: " + track.name + " imageUrl: " + ImageUrlUtil.largeImage(track.album.images) + " album: " + track.album.name);
            return new TrackView(track.name, ImageUrlUtil.smallImage(track.album.images), ImageUrlUtil.largeImage(track.album.images), track.album.name, track.preview_url);
        }
    }
}
