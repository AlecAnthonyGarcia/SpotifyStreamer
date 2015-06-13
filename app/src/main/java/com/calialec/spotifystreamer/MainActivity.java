package com.calialec.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.calialec.spotifystreamer.adapter.ArtistResultAdapter;
import com.calialec.spotifystreamer.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    public static final String EXTRA_ARTIST_ID = "com.calialec.spotifystreamer.EXTRA_ARTIST_ID";
    public static final String EXTRA_ARTIST_NAME = "com.calialec.spotifystreamer.EXTRA_ARTIST_NAME";

    private FrameLayout resultsPlaceholder;
    private EditText artistSearchField;
    private ImageView clearArtistResults;
    private ArrayAdapter artistResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsPlaceholder = (FrameLayout) findViewById(R.id.results_placeholder);

        artistResultsAdapter = new ArtistResultAdapter(this, new ArrayList<Artist>());
        final ListView artistResults = (ListView) findViewById(R.id.listview_artist_results);
        artistResults.setAdapter(artistResultsAdapter);
        artistResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected artist and start the activity to show the Top 10 Tracks
                Artist artist = (Artist) artistResultsAdapter.getItem(position);
                Intent topTracksIntent = new Intent(getApplicationContext(), TopTracksActivity.class);
                topTracksIntent.putExtra(EXTRA_ARTIST_ID, artist.id);
                topTracksIntent.putExtra(EXTRA_ARTIST_NAME, artist.name);
                startActivity(topTracksIntent);
            }
        });

        artistSearchField = (EditText) findViewById(R.id.edittext_artist_search);
        artistSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Query the Spotify API when the user presses the search key on the keyboard
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = artistSearchField.getText().toString();
                    if (searchQuery.length() > 0) {
                        // Fetch the related artists from the user input
                        fetchArtistsResults(searchQuery);
                    }
                }
                return false;
            }
        });
        artistSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Show/hide the button to clear the results based on input length
                if (s.length() > 0) {
                    clearArtistResults.setVisibility(View.VISIBLE);
                } else {
                    clearArtistResults.setVisibility(View.INVISIBLE);
                }
            }
        });

        clearArtistResults = (ImageView) findViewById(R.id.imageview_clear_artist_search);
        clearArtistResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the search results
                artistSearchField.setText("");
                artistResultsAdapter.clear();
                clearArtistResults.setVisibility(View.INVISIBLE);
                ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_SEARCH);
            }
        });
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

    public void fetchArtistsResults(String searchQuery) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.searchArtists(searchQuery, new Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        artistResultsAdapter.clear();
                        // If the results are empty, show the NO_RESULTS placeholder, else update the adapter with the data
                        List<Artist> artists = artistsPager.artists.items;
                        if (artists.isEmpty()) {
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_NO_RESULTS);
                        } else {
                            artistResultsAdapter.addAll(artists);
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, false, -1);
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
