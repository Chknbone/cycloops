/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palarran.cycloops;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<CycloneData>>{

    public static final String LOG_TAG = MainActivity.class.getName();

    /** Adapter for the list of Cyclones */
    private CycloneAdapter mAdapter;

    /** URL for Cyclone data from the USGS website data set */
    private static final String WUNDERGROUND_CURRENT_HURRICANE_URI = "http://api.wunderground.com/api/95e20de6002dc6f0/currenthurricane/view.format";

    /**
     * Constant value for the Cyclone loader ID. Can be any integer.
     *
     * This really only comes into play if using multiple loaders.
     */
    private static final int CYCLONE_LOADER_ID = 1;

    /** TextView that is displayed when the Cyclone list is empty */
    private TextView mEmptyStateTextView;

    /** TextView ths is displayed when there is no available internet/network connection */
    private TextView mNoNetworkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_list);

        // Find a reference to the {@link ListView} in the basic_list layout
        ListView cycloneListView = (ListView) findViewById(R.id.basic_list);

        // Create a new adapter that takes an empty list of Cyclones as input
        mAdapter = new CycloneAdapter(this, new ArrayList<CycloneData>());

        // Set the adapter on the {@link ListView} so the list can be populated in the UI
        cycloneListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected Cyclone.
        cycloneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Cyclone that was clicked on
                CycloneData currentCyclone = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri cycloneUri = Uri.parse(currentCyclone.getmUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, cycloneUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Setting up the correct view in the event there is no network connection
        mNoNetworkTextView = (TextView) findViewById(R.id.no_network);
        cycloneListView.setEmptyView(mNoNetworkTextView);

        //Setting up the correct view in the event there is no Cyclones to list
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_cyclone_list);
        cycloneListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(CYCLONE_LOADER_ID, null, this);

    }
    @Override
    public Loader<List<CycloneData>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URI
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minCategory = sharedPrefs.getString(
                getString(R.string.settings_min_category_key),
                getString(R.string.settings_min_category_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(WUNDERGROUND_CURRENT_HURRICANE_URI);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minCat", minCategory);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new CycloneLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<CycloneData>> loader, List<CycloneData> cyclones) {

        //No connectivity
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected == false) {
            mNoNetworkTextView.setText(R.string.no_network);
        } else {
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.no_cyclones_found);
        }

        //Stop displaying Progressbar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link CycloneData}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (cyclones != null && !cyclones.isEmpty()) {
            mAdapter.addAll(cyclones);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CycloneData>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //Menu for user preference settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, MenuSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}