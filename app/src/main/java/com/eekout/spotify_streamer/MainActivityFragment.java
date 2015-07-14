package com.eekout.spotify_streamer;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private SpotifyStreamerListAdapter mSpotifyAdapter;
    private static final String ARTIST_RESULTS_KEY = "artistResultsKey";
    public static final String TOP_TEN_TRACKS_SUBTITLE_KEY = "topTenTracksSubtitleKey";

    public MainActivityFragment() {
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ViewInfo[] values = (ViewInfo[]) savedInstanceState.getParcelableArray(ARTIST_RESULTS_KEY);
            if (values != null) {
                mSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), new ArrayList<ViewInfo>(Arrays.asList(values)));
            }
        } else {
            mSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), new ArrayList<ViewInfo>());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        ViewInfo[] values = new ViewInfo[mSpotifyAdapter.getCount()];
        for (int i = 0; i < mSpotifyAdapter.getCount(); i++) {
            values[i] = mSpotifyAdapter.getItem(i);
        }
        savedState.putParcelableArray(ARTIST_RESULTS_KEY, values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.search_results_view);
        listView.setAdapter(mSpotifyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topTenTracksIntent = new Intent(getActivity(), TopTenTracksActivity.class);
                ArtistView artistView = (ArtistView) mSpotifyAdapter.getItem(i);
                topTenTracksIntent.putExtra(Intent.EXTRA_TEXT, artistView.getSpotifyId());
                topTenTracksIntent.putExtra(TOP_TEN_TRACKS_SUBTITLE_KEY, artistView.getName());

                startActivity(topTenTracksIntent);
            }
        });

        EditText editText = (EditText) rootView.findViewById(R.id.artist_search_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new SearchSpotifyTask().execute(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

        return rootView;
    }

    private class SearchSpotifyTask extends AsyncTask<String, Void, ArtistsPager> {
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();
        private static final String EMPTY_SEARCH_RESULTS = "No artist were found with name: %s";
        private String artistToSearch;

        @Override
        protected ArtistsPager doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            artistToSearch = params[0];
            Log.d(LOG_TAG, "Searching for artist: " + artistToSearch);

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            return spotify.searchArtists(artistToSearch);
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            List<ViewInfo> viewInfos = toViewInfos(artistsPager);

            if (viewInfos.isEmpty()) {
                notifyEmptySearchResults(artistToSearch);
            } else {
                if (mSpotifyAdapter == null) {
                    mSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), viewInfos);
                } else {
                    mSpotifyAdapter.clear();
                    mSpotifyAdapter.addAll(viewInfos);
                }
            }
        }

        private void notifyEmptySearchResults(String artist) {
            Toast.makeText(getActivity(), String.format(EMPTY_SEARCH_RESULTS, artist), Toast.LENGTH_SHORT).show();
        }

        private List<ViewInfo> toViewInfos(ArtistsPager artistsPager) {
            List<ViewInfo> viewInfos = new ArrayList<ViewInfo>();

            if (artistsPager != null) {
                for (Artist artist : artistsPager.artists.items) {
                    viewInfos.add(toViewInfo(artist));
                }
            }

            return viewInfos;
        }

        private ViewInfo toViewInfo(Artist artist) {
            Log.d(LOG_TAG, "name: " + artist.name + " imageUrl: " + ImageUrlUtil.smallImage(artist.images) + " artistId: " + artist.id);
            return new ArtistView(artist.name, ImageUrlUtil.smallImage(artist.images), artist.id);
        }
    }
}
