package com.calialec.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.calialec.spotifystreamer.model.ArtistParcelable;
import com.calialec.spotifystreamer.model.TrackParcelable;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements SearchFragment.SearchFragmentCallback, TopTracksFragment.TopTracksFragmentCallback, TrackPlayerDialogFragment.TrackPlayerDialogFragmentCallback {

    private static final String TOPTRACKSFRAGMENT_TAG = "TTFTAG";
    private boolean mTwoPane;
    private TrackPlayerService trackPlayerService;
    private Intent trackPlayerServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, new TopTracksFragment(), TOPTRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistSelected(ArtistParcelable artist) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(getString(R.string.extra_artist_id), artist.id);
            args.putString(getString(R.string.extra_artist_name), artist.name);
            args.putBoolean(getString(R.string.extra_two_pane), true);
            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, fragment, TOPTRACKSFRAGMENT_TAG)
                    .commit();
        } else {
            Intent topTracksIntent = new Intent(this, TopTracksActivity.class);
            topTracksIntent.putExtra(getString(R.string.extra_artist_id), artist.id);
            topTracksIntent.putExtra(getString(R.string.extra_artist_name), artist.name);
            startActivity(topTracksIntent);
        }
    }

    private ServiceConnection trackPlayerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackPlayerService.TrackPlayerBinder binder = (TrackPlayerService.TrackPlayerBinder) service;
            trackPlayerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onTopTracksReceived(ArrayList<TrackParcelable> topTracksList) {
        if (trackPlayerService != null) {
            trackPlayerService.setTopTracksList(topTracksList);
        }
    }

    @Override
    public void onTopTrackSelected(int position) {
        trackPlayerService.playTopTrack(position);
    }

    @Override
    public void onProgressChanged(int progress) {
        trackPlayerService.seekTo(progress);
    }

    @Override
    public void onPlaybackChanged() {
        trackPlayerService.handlePlayback();
    }

    @Override
    public TrackParcelable onNextSong() {
        return trackPlayerService.nextSong();
    }

    @Override
    public TrackParcelable onPreviousSong() {
        return trackPlayerService.previousSong();
    }

    @Override
    public void onTrackPlayerControlPlayInitialized(ImageView trackPlayerControlPlay) {
        if (trackPlayerService != null) {
            trackPlayerService.setTrackPlayerControlPlay(trackPlayerControlPlay);
        }
    }

    @Override
    public void onTrackPlayerControlSeekBarInitialized(SeekBar trackPlayerControlSeekBar) {
        if (trackPlayerService != null) {
            trackPlayerService.setTrackPlayerControlSeekBar(trackPlayerControlSeekBar);
        }
    }

    @Override
    public void onTrackLapsedTvInitialized(TextView trackLapsedTv) {
        if (trackPlayerService != null) {
            trackPlayerService.setTrackLapsedTv(trackLapsedTv);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (trackPlayerServiceIntent == null) {
            trackPlayerServiceIntent = new Intent(this, TrackPlayerService.class);
            bindService(trackPlayerServiceIntent, trackPlayerServiceConnection, Context.BIND_AUTO_CREATE);
            startService(trackPlayerServiceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(trackPlayerServiceIntent);
        trackPlayerService = null;
        super.onDestroy();
    }

}
