package com.calialec.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

public class TrackParcelable implements Parcelable {

    public String trackName;
    public String albumName;
    public String imageUrl;
    public String previewUrl;

    public TrackParcelable(Track track) {
        this.trackName = track.name;
        this.albumName = track.album.name;
        this.previewUrl = track.preview_url;
        // Get first available image from images array if it exists
        if (track.album.images.size() > 0) {
            this.imageUrl = track.album.images.get(0).url;
        }
    }

    private TrackParcelable(Parcel source) {
        trackName = source.readString();
        albumName = source.readString();
        imageUrl = source.readString();
        previewUrl = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(imageUrl);
        dest.writeString(previewUrl);
    }

    public static final Parcelable.Creator<TrackParcelable> CREATOR = new Parcelable.Creator<TrackParcelable>() {

        public TrackParcelable createFromParcel(Parcel source) {
            return new TrackParcelable(source);
        }

        public TrackParcelable[] newArray(int size) {
            return new TrackParcelable[size];
        }
    };

}
