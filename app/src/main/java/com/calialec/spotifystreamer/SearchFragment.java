package com.calialec.spotifystreamer;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.calialec.spotifystreamer.adapter.ArtistResultAdapter;
import com.calialec.spotifystreamer.model.ArtistParcelable;
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

public class SearchFragment extends Fragment {

    private FrameLayout resultsPlaceholder;
    private EditText artistSearchField;
    private ImageView clearArtistResults;
    private ArrayAdapter artistResultsAdapter;
    private ArrayList<ArtistParcelable> artistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        resultsPlaceholder = (FrameLayout) rootView.findViewById(R.id.results_placeholder);

        if (savedInstanceState != null) {
            artistList = savedInstanceState.getParcelableArrayList(getString(R.string.saved_state_artist_list));
            ViewUtil.initResultsPlaceholder(resultsPlaceholder, false, -1);
        } else {
            artistList = new ArrayList<>();
        }

        artistResultsAdapter = new ArtistResultAdapter(getActivity(), artistList);
        final ListView artistResults = (ListView) rootView.findViewById(R.id.listview_artist_results);
        artistResults.setAdapter(artistResultsAdapter);
        artistResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected artist and start the activity to show the Top 10 Tracks
                ArtistParcelable artist = (ArtistParcelable) artistResultsAdapter.getItem(position);
                ((SearchFragmentCallback) getActivity()).onArtistSelected(artist);
            }
        });

        artistSearchField = (EditText) rootView.findViewById(R.id.edittext_artist_search);
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

        clearArtistResults = (ImageView) rootView.findViewById(R.id.imageview_clear_artist_search);
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

        return rootView;
    }


    public void fetchArtistsResults(String searchQuery) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.searchArtists(searchQuery, new Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        artistResultsAdapter.clear();
                        // If the results are empty, show the NO_RESULTS placeholder, else update the adapter with the data
                        List<Artist> artists = artistsPager.artists.items;
                        if (artists.isEmpty()) {
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_NO_RESULTS);
                        } else {
                            for (Artist artist : artists) {
                                artistList.add(new ArtistParcelable(artist));
                            }
                            artistResultsAdapter.notifyDataSetChanged();
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, false, -1);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtil.showNetworkError(getActivity());
                    }
                });
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.saved_state_artist_list), artistList);
        super.onSaveInstanceState(outState);
    }

    public interface SearchFragmentCallback {
        void onArtistSelected(ArtistParcelable artist);
    }
}
