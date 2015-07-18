package com.eekout.spotify_streamer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A fragment implementing the top ten tracks view.
 */
public class TopTenTracksActivityFragment extends Fragment {
    private SpotifyStreamerListAdapter mTopTenSpotifyAdapter;
    private List<ViewInfo> viewInfos = new ArrayList<ViewInfo>();
    private ProgressBar progressBar;

    public TopTenTracksActivityFragment() {
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ViewInfo[] values = (ViewInfo[]) savedInstanceState.getParcelableArray(CommonsUtil.VIEW_INFOS_KEY);
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

        progressBar = (ProgressBar) rootView.findViewById(R.id.top_ten_tracks_progress_bar);

        Intent topTenTracksIntent = getActivity().getIntent();
        String spotifyId = topTenTracksIntent.getStringExtra(Intent.EXTRA_TEXT);
        String artist = topTenTracksIntent.getStringExtra(CommonsUtil.TOP_TEN_TRACKS_SUBTITLE_KEY);

        if (!CommonsUtil.hasNetworkConnectivity(getActivity())) {
            notifyUnavailableNetworkConnection();
        } else {
            new TopTenTracksSpotifyTask().execute(spotifyId, artist);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        ViewInfo[] values = new ViewInfo[mTopTenSpotifyAdapter.getCount()];
        for (int i = 0; i < mTopTenSpotifyAdapter.getCount(); i++) {
            values[i] = mTopTenSpotifyAdapter.getItem(i);
        }
        savedState.putParcelableArray(CommonsUtil.VIEW_INFOS_KEY, values);
    }

    private void notifyUnavailableNetworkConnection() {
        Toast.makeText(getActivity(), CommonsUtil.NETWORK_CONNECTION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }

    private class TopTenTracksSpotifyTask extends AsyncTask<String, Void, Tracks> {
        private final String LOG_TAG = TopTenTracksSpotifyTask.class.getSimpleName();
        private static final String ARTIST_COUNTRY_CODE = "US";

        private String artist;
        private boolean apiCommunicationFailed;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Tracks doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String artistId = params[0];
            artist = params[1];
            Log.d(LOG_TAG, "Getting top tracks for artist: " + artist);

            Map<String, Object> queryMap = new HashMap<String, Object>();

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                queryMap.put(CommonsUtil.ARTIST_COUNTRY_KEY, ARTIST_COUNTRY_CODE);
                return spotify.getArtistTopTrack(artistId, queryMap);
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, spotifyError.getMessage());
                apiCommunicationFailed = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            progressBar.setVisibility(View.GONE);

            viewInfos = toViewInfos(tracks);

            if (apiCommunicationFailed) {
                notifyApiCommunicationFailed();
            } else if (viewInfos.isEmpty()) {
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

        private void notifyApiCommunicationFailed() {
            Toast.makeText(getActivity(), CommonsUtil.SPOTIFY_API_COMMUNICATION_FAILED, Toast.LENGTH_SHORT).show();
        }

        private void notifyEmptyTopTenTracks() {
            Toast.makeText(getActivity(), String.format(CommonsUtil.EMPTY_TOP_TEN_RESULTS, artist), Toast.LENGTH_SHORT).show();
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
            String name = track.name;
            String smallImageUrl = CommonsUrlUtil.smallImage(track.album.images);
            String largeImageUrl =  CommonsUrlUtil.largeImage(track.album.images);
            String album = track.album.name;
            String previewUrl = CommonsUrlUtil.sanitizeUrl(track.preview_url);

            Log.d(LOG_TAG, "name: " + name + " smallImageUrl: " + smallImageUrl
                    + " largeImageUrl: " + largeImageUrl + " album: " + album);
            return new TrackView(name, smallImageUrl, largeImageUrl, album, previewUrl);
        }
    }
}
