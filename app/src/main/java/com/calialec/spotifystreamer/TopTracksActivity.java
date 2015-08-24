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

import com.calialec.spotifystreamer.model.TrackParcelable;

import java.util.ArrayList;

public class TopTracksActivity extends ActionBarActivity implements TopTracksFragment.TopTracksFragmentCallback, TrackPlayerDialogFragment.TrackPlayerDialogFragmentCallback {

    private TrackPlayerService trackPlayerService;
    private Intent trackPlayerServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.extra_artist_id), getIntent().getStringExtra(getString(R.string.extra_artist_id)));
            arguments.putString(getString(R.string.extra_artist_name), getIntent().getStringExtra(getString(R.string.extra_artist_name)));

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, fragment)
                    .commit();
        }
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
}
