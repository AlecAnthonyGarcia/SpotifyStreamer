package com.calialec.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.calialec.spotifystreamer.adapter.TopTracksResultAdapter;
import com.calialec.spotifystreamer.model.TrackParcelable;
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

public class TopTracksFragment extends Fragment {

    private FrameLayout resultsPlaceholder;
    private ArrayAdapter topTracksResultsAdapter;

    private ArrayList<TrackParcelable> topTracksList;
    private String artistName = "";
    private boolean mTwoPane;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        String id = "";
        // Retrieve the extras passed from the previous activity
        Bundle extras = getArguments();
        if (extras != null) {
            id = extras.getString(getString(R.string.extra_artist_id));
            artistName = extras.getString(getString(R.string.extra_artist_name));
            if (extras.getBoolean(getString(R.string.extra_two_pane))) {
                mTwoPane = true;
            } else {
                mTwoPane = false;
            }
        }

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.activity_top_tracks_title));
            actionBar.setSubtitle(artistName);
        }

        if (savedInstanceState != null) {
            topTracksList = savedInstanceState.getParcelableArrayList(getString(R.string.saved_state_top_tracks_list));
        } else {
            topTracksList = new ArrayList<>();
        }

        resultsPlaceholder = (FrameLayout) rootView.findViewById(R.id.results_placeholder);

        topTracksResultsAdapter = new TopTracksResultAdapter(getActivity(), topTracksList);
        ListView artistResults = (ListView) rootView.findViewById(R.id.listview_top_tracks_results);
        artistResults.setAdapter(topTracksResultsAdapter);
        artistResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrackParcelable track = (TrackParcelable) topTracksResultsAdapter.getItem(position);
                showTrackPlayerDialog(track);
                ((TopTracksFragmentCallback) getActivity()).onTopTrackSelected(position);
            }
        });

        if (savedInstanceState == null) {
            // Fetch the artist's Top Tracks
            fetchTopTracksResults(id);
        }

        return rootView;
    }

    public void showTrackPlayerDialog(TrackParcelable track) {
        FragmentManager fragmentManager = ((ActionBarActivity) getActivity()).getSupportFragmentManager();
        TrackPlayerDialogFragment newFragment = new TrackPlayerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.extra_artist_name), artistName);
        bundle.putString(getString(R.string.extra_track_name), track.trackName);
        bundle.putString(getString(R.string.extra_album_name), track.albumName);
        bundle.putString(getString(R.string.extra_image_url), track.imageUrl);
        newFragment.setArguments(bundle);

        if (mTwoPane) {
            newFragment.show(fragmentManager, "dialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }
    }

    public void fetchTopTracksResults(String artistId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
        spotify.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Track> tracksList = tracks.tracks;
                        // If the results are empty, show the NO_TOP_TRACKS placeholder, else update the adapter with the data
                        if (tracksList.isEmpty()) {
                            ViewUtil.initResultsPlaceholder(resultsPlaceholder, true, ViewUtil.PLACEHOLDER_TYPE_NO_TOP_TRACKS);
                        } else {
                            for (Track track : tracksList) {
                                topTracksList.add(new TrackParcelable(track));
                            }
                            topTracksResultsAdapter.addAll(topTracksList);
                            ((TopTracksFragmentCallback) getActivity()).onTopTracksReceived(topTracksList);
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
        outState.putParcelableArrayList(getString(R.string.saved_state_top_tracks_list), topTracksList);
        super.onSaveInstanceState(outState);
    }

    public interface TopTracksFragmentCallback {
        void onTopTracksReceived(ArrayList<TrackParcelable> topTracksList);

        void onTopTrackSelected(int position);
    }

}
