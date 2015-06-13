package com.calialec.spotifystreamer.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.calialec.spotifystreamer.R;

public class ViewUtil {

    public static final int PLACEHOLDER_TYPE_SEARCH = 0;
    public static final int PLACEHOLDER_TYPE_NO_RESULTS = 1;
    public static final int PLACEHOLDER_TYPE_NO_TOP_TRACKS = 2;

    /**
     * Formats the placeholder icon, title, and subtitle based on the type
     *
     * @param resultsPlaceholder The placeholder view to modify
     * @param visible            True if the placeholder should be visible, false to hide the placeholder
     * @param placeHolderType    A type that determines the placeholder icon, title, and subtitle
     */
    public static void initResultsPlaceholder(View resultsPlaceholder, boolean visible, int placeHolderType) {
        Context context = resultsPlaceholder.getContext();
        switch (placeHolderType) {
            case PLACEHOLDER_TYPE_SEARCH:
                ((ImageView) resultsPlaceholder.findViewById(R.id.result_placeholder_icon)).setImageResource(R.drawable.cat_placeholder_search);
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_title)).setText(context.getString(R.string.result_placeholder_search_title));
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_subtitle)).setText(context.getString(R.string.result_placeholder_search_subtitle));
                break;
            case PLACEHOLDER_TYPE_NO_RESULTS:
                String searchQuery = ((EditText) ((Activity) context).findViewById(R.id.edittext_artist_search)).getText().toString();
                ((ImageView) resultsPlaceholder.findViewById(R.id.result_placeholder_icon)).setImageResource(R.drawable.cat_placeholder_flag);
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_title)).setText(context.getString(R.string.result_placeholder_no_results_title) + " \"" + searchQuery + "\"");
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_subtitle)).setText(context.getString(R.string.result_placeholder_no_results_subtitle));
                break;
            case PLACEHOLDER_TYPE_NO_TOP_TRACKS:
                ((ImageView) resultsPlaceholder.findViewById(R.id.result_placeholder_icon)).setImageResource(R.drawable.cat_placeholder_flag);
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_title)).setText(context.getString(R.string.result_placeholder_no_top_tracks_title));
                ((TextView) resultsPlaceholder.findViewById(R.id.result_placeholder_subtitle)).setText("");
                break;
        }
        resultsPlaceholder.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    public static void showNetworkError(Context context) {
        Toast.makeText(context, context.getString(R.string.error_network_failure), Toast.LENGTH_LONG).show();
    }

}
