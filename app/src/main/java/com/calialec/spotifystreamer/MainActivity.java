package com.calialec.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.calialec.spotifystreamer.model.ArtistParcelable;

public class MainActivity extends ActionBarActivity implements SearchFragment.SearchFragmentCallback {

    private static final String TOPTRACKSFRAGMENT_TAG = "TTFTAG";
    private boolean mTwoPane;

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
}
