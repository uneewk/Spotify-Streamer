package com.eekout.spotify_streamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom list adapter for the spotify streamer.
 */
public class SpotifyStreamerListAdapter extends ArrayAdapter<ViewInfo> {
    private static final String LOG_TAG = SpotifyStreamerListAdapter.class.getSimpleName();

    public SpotifyStreamerListAdapter(Activity context, List<ViewInfo> viewInfos) {
        super(context, 0, viewInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewInfo viewInfo = getItem(position);

        if (viewInfo instanceof ArtistView) {
            ArtistView artistViewInfo = (ArtistView) viewInfo;
            return getArtistView(artistViewInfo, convertView, parent);
        } else if (viewInfo instanceof TrackView) {
            TrackView trackViewInfo = (TrackView) viewInfo;
            return getTrackView(trackViewInfo, convertView, parent);
        }

        return null;
    }

    private View getTrackView(TrackView trackViewInfo, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.top_ten_track, parent, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.top_ten_artist_thumbnail);
        Picasso.with(getContext()).load(trackViewInfo.getSmallImageUrl()).into(imageView);

        TextView topText = (TextView) rootView.findViewById(R.id.artist_detail_topLine);
        topText.setText(trackViewInfo.getName());

        TextView bottomText = (TextView) rootView.findViewById(R.id.artist_detail_bottomLine);
        bottomText.setText(trackViewInfo.getAlbum());

        return rootView;
    }

    private View getArtistView(ArtistView artistViewInfo, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.results_view_item, parent, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.artist_thumbnail);
        Picasso.with(getContext()).load(artistViewInfo.getSmallImageUrl()).into(imageView);

        TextView topText = (TextView) rootView.findViewById(R.id.artist_detail_text);
        topText.setText(artistViewInfo.getName());

        return rootView;
    }

}
