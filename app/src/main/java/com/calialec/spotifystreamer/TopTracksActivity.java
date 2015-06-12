package com.calialec.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.calialec.spotifystreamer.adapter.TopTracksResultAdapter;
import com.calialec.spotifystreamer.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTracksActivity extends ActionBarActivity {

    private FrameLayout resultsPlaceholder;
    private ArrayAdapter topTracksResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        String id = "";
        String name = "";
        // Retrieve the extras passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString(MainActivity.EXTRA_ARTIST_ID);
            name = extras.getString(MainActivity.EXTRA_ARTIST_NAME);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.activity_top_tracks_title));
            actionBar.setSubtitle(name);
        }

        resultsPlaceholder = (FrameLayout) findViewById(R.id.results_placeholder);

        topTracksResultsAdapter = new TopTracksResultAdapter(this, new ArrayList<Track>());
        ListView artistResults = (ListView) findViewById(R.id.listview_top_tracks_results);
        artistResults.setAdapter(topTracksResultsAdapter);

        new FetchTopTracksResultsTask().execute(id);
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

    private class FetchTopTracksResultsTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            // Fetch the artist's Top Tracks
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map<String, Object> options = new HashMap<>();
            options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
            Tracks results = spotify.getArtistTopTrack(params[0], options);
            return results.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            // If the results are empty, show the NO_TOP_TRACKS placeholder, else update the adapter with the data
            if (tracks.isEmpty()) {
                ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_NO_TOP_TRACKS);
            } else {
                topTracksResultsAdapter.addAll(tracks);
            }
        }
    }

}
