package com.eekout.spotify_streamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private SpotifyStreamerListAdapter mSpotifyAdapter;
    private ProgressBar progressBar;

    public MainActivityFragment() {
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ViewInfo[] values = (ViewInfo[]) savedInstanceState.getParcelableArray(CommonsUtil.ARTIST_RESULTS_KEY);
            if (values != null) {
                mSpotifyAdapter = new SpotifyStreamerListAdapter(getActivity(), new ArrayList<>(Arrays.asList(values)));
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
        savedState.putParcelableArray(CommonsUtil.ARTIST_RESULTS_KEY, values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set the main view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.search_results_view);
        listView.setAdapter(mSpotifyAdapter);

        // Set the progress bar
        progressBar = (ProgressBar) rootView.findViewById(R.id.artist_search_progress_bar);

        final SearchView searchText = (SearchView) rootView.findViewById(R.id.artist_search_text);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topTenTracksIntent = new Intent(getActivity(), TopTenTracksActivity.class);
                ArtistView artistView = (ArtistView) mSpotifyAdapter.getItem(i);
                topTenTracksIntent.putExtra(Intent.EXTRA_TEXT, artistView.getSpotifyId());
                topTenTracksIntent.putExtra(CommonsUtil.TOP_TEN_TRACKS_SUBTITLE_KEY, artistView.getName());

                startActivity(topTenTracksIntent);
            }
        });

        /**
        EditText editText = (EditText) rootView.findViewById(R.id.artist_search_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = handleSearchSpotifyForArtist(v.getText().toString());
                }
                return handled;
            }
         });**/

        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getResources().getString(R.string.artist_search_hint));
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchKeyword = searchText.getQuery().toString();
                return handleSearchSpotifyForArtist(searchKeyword);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return rootView;
    }

    private boolean handleSearchSpotifyForArtist(String searchText) {
        boolean searchedForArtist = false;

        if (!CommonsUtil.hasNetworkConnectivity(getActivity())) {
            notifyUnavailableNetworkConnection();
        } else {
            new SearchSpotifyTask().execute(searchText);
            searchedForArtist = true;
        }

        return searchedForArtist;
    }

    private void notifyUnavailableNetworkConnection() {
        Toast.makeText(getActivity(), CommonsUtil.NETWORK_CONNECTION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }

    private class SearchSpotifyTask extends AsyncTask<String, Void, ArtistsPager> {
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();
        private String artistToSearch;
        private boolean apiCommunicationFailed;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArtistsPager doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            artistToSearch = params[0];
            Log.d(LOG_TAG, "Searching for artist: " + artistToSearch);

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                return spotify.searchArtists(artistToSearch);
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, spotifyError.getMessage());
                apiCommunicationFailed = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            List<ViewInfo> viewInfos = toViewInfos(artistsPager);

            progressBar.setVisibility(View.GONE);

            if (apiCommunicationFailed) {
                notifyApiCommunicationFailed();
            } else if (viewInfos.isEmpty()) {
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

        private void notifyApiCommunicationFailed() {
            Toast.makeText(getActivity(), CommonsUtil.NETWORK_CONNECTION_UNAVAILABLE, Toast.LENGTH_SHORT).show();
        }

        private void notifyEmptySearchResults(String artist) {
            Toast.makeText(getActivity(), String.format(CommonsUtil.EMPTY_SEARCH_RESULTS, artist), Toast.LENGTH_SHORT).show();
        }

        private List<ViewInfo> toViewInfos(ArtistsPager artistsPager) {
            List<ViewInfo> viewInfos = new ArrayList<>();

            if (artistsPager != null) {
                for (Artist artist : artistsPager.artists.items) {
                    viewInfos.add(toViewInfo(artist));
                }
            }

            return viewInfos;
        }

        private ViewInfo toViewInfo(Artist artist) {
            String name = artist.name;
            String imageUrl = CommonsUrlUtil.smallImage(artist.images);
            String artistId = artist.id;

            Log.d(LOG_TAG, "name: " + name + " imageUrl: " + imageUrl + " artistId: " + artistId);
            return new ArtistView(name, imageUrl, artistId);
        }
    }
}
