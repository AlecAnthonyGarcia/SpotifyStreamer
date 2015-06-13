package com.calialec.spotifystreamer;

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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

        // Fetch the artist's Top Tracks
        fetchTopTracksResults(id);
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

    public void fetchTopTracksResults(String artistId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
        spotify.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Track> tracksList = tracks.tracks;
                        // If the results are empty, show the NO_TOP_TRACKS placeholder, else update the adapter with the data
                        if (tracksList.isEmpty()) {
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_NO_TOP_TRACKS);
                        } else {
                            topTracksResultsAdapter.addAll(tracksList);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtil.showNetworkError(getApplicationContext());
                    }
                });
            }
        });
    }

}
