package com.calialec.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistParcelable implements Parcelable {

    public String id;
    public String name;
    public String imageUrl;

    public ArtistParcelable(Artist artist) {
        this.id = artist.id;
        this.name = artist.name;
        // Get first available image from images array if it exists
        if (artist.images.size() > 0) {
            this.imageUrl = artist.images.get(0).url;
        }
    }

    private ArtistParcelable(Parcel source) {
        id = source.readString();
        name = source.readString();
        imageUrl = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR = new Parcelable.Creator<ArtistParcelable>() {

        public ArtistParcelable createFromParcel(Parcel source) {
            return new ArtistParcelable(source);
        }

        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };

}
