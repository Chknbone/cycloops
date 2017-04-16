package com.palarran.cycloops;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of cyclones by using an AsyncTask to perform the network request
 * to the given URL.
 */
public class CycloneLoader extends AsyncTaskLoader<List<Cyclone>> {

    /** Tag for log messages */
    private static final String LOG_TAG = CycloneLoader.class.getName();

    /** Query URL */
    private String url;

    /**
     * Constructs a new {@link CycloneLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */

    public CycloneLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

     //This is on a background thread
    @Override
    public List<Cyclone> loadInBackground() {
        if (url == null) {
            return null;
    }

        // Perform the network request, parse the response, and extract a list of Cyclones.
        List<Cyclone> cyclones = JsonData.fetchCycloneData(url);
        return cyclones;
    }
}
