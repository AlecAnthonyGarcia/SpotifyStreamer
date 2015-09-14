package com.calialec.spotifystreamer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.calialec.spotifystreamer.model.TrackParcelable;
import com.squareup.picasso.Picasso;

public class TrackPlayerDialogFragment extends DialogFragment {

    public static final int TRACK_PREVIEW_DURATION = 30000;

    public static ImageView trackPlayerControlPlay;
    private ImageView trackPlayerControlNext;
    private ImageView trackPlayerControlPrevious;
    public static SeekBar seekBar;
    public static TextView trackLapsedTv;

    private TextView artistNameTv;
    private TextView trackNameTv;
    private TextView albumNameTv;
    private ImageView albumImageIv;

    private TrackParcelable playingTrack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_track_player, container, false);

        artistNameTv = (TextView) rootView.findViewById(R.id.track_player_artist_name_textview);
        trackNameTv = (TextView) rootView.findViewById(R.id.track_player_track_name_textview);
        albumNameTv = (TextView) rootView.findViewById(R.id.track_player_album_name_textview);
        albumImageIv = (ImageView) rootView.findViewById(R.id.track_player_album_image_imageview);

        trackPlayerControlPlay = (ImageView) rootView.findViewById(R.id.track_player_control_play);
        trackPlayerControlNext = (ImageView) rootView.findViewById(R.id.track_player_control_next);
        trackPlayerControlPrevious = (ImageView) rootView.findViewById(R.id.track_player_control_previous);
        seekBar = (SeekBar) rootView.findViewById(R.id.track_player_control_seekbar);
        trackLapsedTv = (TextView) rootView.findViewById(R.id.track_player_track_lapsed_textview);

        seekBar.setMax(TRACK_PREVIEW_DURATION);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    ((TrackPlayerDialogFragmentCallback) getActivity()).onProgressChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        trackPlayerControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TrackPlayerDialogFragmentCallback) getActivity()).onPlaybackChanged();
            }
        });

        trackPlayerControlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackParcelable track = ((TrackPlayerDialogFragmentCallback) getActivity()).onNextSong();
                if (track != null) {
                    updatePlayerUi(track);
                }
            }
        });

        trackPlayerControlPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackParcelable track = ((TrackPlayerDialogFragmentCallback) getActivity()).onPreviousSong();
                if (track != null) {
                    updatePlayerUi(track);
                }
            }
        });

        if (savedInstanceState != null) {
            playingTrack = savedInstanceState.getParcelable(getString(R.string.saved_state_playing_track));
            updatePlayerUi(playingTrack);
        } else {
            updatePlayerUi(null);
        }

        return rootView;
    }

    public void updatePlayerUi(TrackParcelable track) {
        String artistName = "";
        String albumName = "";
        String trackName = "";
        String imageUrl = "";
        // Retrieve the extras passed from the previous activity
        Bundle extras = getArguments();
        if (track != null) {
            if (extras != null) {
                artistName = extras.getString(getString(R.string.extra_artist_name));
            }
            albumName = track.albumName;
            trackName = track.trackName;
            imageUrl = track.imageUrl;
            playingTrack = track;
        } else {
            if (extras != null) {
                artistName = extras.getString(getString(R.string.extra_artist_name));
                albumName = extras.getString(getString(R.string.extra_album_name));
                trackName = extras.getString(getString(R.string.extra_track_name));
                imageUrl = extras.getString(getString(R.string.extra_image_url));
            }

        }
        artistNameTv.setText(artistName);
        albumNameTv.setText(albumName);
        trackNameTv.setText(trackName);
        if (!imageUrl.equals("")) {
            Picasso.with(getActivity()).load(imageUrl).into(albumImageIv);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.saved_state_playing_track), playingTrack);
        super.onSaveInstanceState(outState);
    }

    public interface TrackPlayerDialogFragmentCallback {
        void onProgressChanged(int progress);

        void onPlaybackChanged();

        TrackParcelable onNextSong();

        TrackParcelable onPreviousSong();

    }

}
