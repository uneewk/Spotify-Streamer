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
        TrackViewHolder holder;

        if (convertView != null) {
            holder = (TrackViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.top_ten_track, parent, false);

            holder = new TrackViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.top_ten_artist_thumbnail);
            holder.topText = (TextView) convertView.findViewById(R.id.artist_detail_topLine);
            holder.bottomText = (TextView) convertView.findViewById(R.id.artist_detail_bottomLine);

            convertView.setTag(holder);
        }

        Picasso.with(getContext()).load(trackViewInfo.getSmallImageUrl()).into(holder.imageView);
        holder.topText.setText(trackViewInfo.getName());
        holder.bottomText.setText(trackViewInfo.getAlbum());

        return convertView;
    }

    private View getArtistView(ArtistView artistViewInfo, View convertView, ViewGroup parent) {
        ArtistViewHolder holder;

        if (convertView != null) {
            holder = (ArtistViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.results_view_item, parent, false);

            holder = new ArtistViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.artist_thumbnail);
            holder.topText = (TextView) convertView.findViewById(R.id.artist_detail_text);

            convertView.setTag(holder);
        }

        holder.topText.setText(artistViewInfo.getName());
        Picasso.with(getContext()).load(artistViewInfo.getSmallImageUrl()).into(holder.imageView);

        return convertView;
    }

    // ViewHolder pattern
    static class ArtistViewHolder {
        ImageView imageView;
        TextView topText;
    }

    static class TrackViewHolder extends ArtistViewHolder {
        TextView bottomText;
    }

}
