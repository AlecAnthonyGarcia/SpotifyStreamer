package com.calialec.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.calialec.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Track;

public class TopTracksResultAdapter extends ArrayAdapter<Track> {

    final private Context context;

    private static class ViewHolder {
        ImageView topTrackImage;
        TextView topTrackName;
        TextView topTrackAlbum;
    }

    public TopTracksResultAdapter(Context context, List<Track> tracks) {
        super(context, R.layout.list_item_top_track_result, tracks);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = getItem(position);
        AlbumSimple album = track.album;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_top_track_result, parent, false);
            viewHolder.topTrackImage = (ImageView) convertView.findViewById(R.id.list_item_top_track_image_imageview);
            viewHolder.topTrackName = (TextView) convertView.findViewById(R.id.list_item_top_track_name_textview);
            viewHolder.topTrackAlbum = (TextView) convertView.findViewById(R.id.list_item_top_track_album_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Load first available image from images array if it exists
        if (album.images.size() > 0) {
            Picasso.with(context).load(album.images.get(0).url).into(viewHolder.topTrackImage);
        }
        viewHolder.topTrackName.setText(track.name);
        viewHolder.topTrackAlbum.setText(album.name);
        return convertView;
    }
}
