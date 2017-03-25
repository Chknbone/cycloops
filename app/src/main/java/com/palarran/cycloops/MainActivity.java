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

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Cyclone>>,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String LOG_TAG = MainActivity.class.getName();

    //Adapter for the list of Cyclones
    private CycloneAdapter adapter;

    //URL for JSON Cyclone data from the USGS website data set
    private static final String WUNDERGROUND_CURRENT_HURRICANE_URI
            = "http://api.wunderground.com/api/95e20de6002dc6f0/currenthurricane/view.json";

    //Constant value for the Cyclone loader ID. Can be any integer.
    //This really only comes into play if using multiple loaders. Setting to 1
    private static final int CYCLONE_LOADER_ID = 1;

    //TextView that is displayed when the Cyclone list is empty
    private TextView emptyStateTextView;

    //TextView ths is displayed when there is no available internet/network connection
    private TextView noNetworkTextView;

    //Defining Google Map objects variables
    GoogleMap googleMap;
    boolean mapReady = false;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    Marker currLocationMarker;
    LocationRequest locationRequest;

    //Object for the current position of the current cyclone
    Cyclone currentCyclonePosition;
    Cyclone currentCycloneName;

    //Fixme: Debug is showing NUllPointerException here. Most likely JSON data is not getting pulled correctly
    //Defining map markers for Cyclone location (Lat & Lon)
    double cycloneLatitude = currentCyclonePosition.getLatitude();
    double cycloneLongitude = currentCyclonePosition.getLongitude();
    String cycloneName = currentCycloneName.getName();

    MarkerOptions cyclonePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cyclone_list);

        // Find a reference to the {@link ListView} in the basic_list layout
        ListView cycloneListView = (ListView) findViewById(R.id.cyclone_list);

        // Create a new adapter that takes an empty list of Cyclones as input
        adapter = new CycloneAdapter(this, new ArrayList<Cyclone>());

        // Set the adapter on the {@link ListView} so the list can be populated in the UI
        cycloneListView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected Cyclone.
        cycloneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Cyclone that was clicked on
                Cyclone currentCyclone = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri cycloneUri = Uri.parse(currentCyclone.getUrl());

                // Create a new intent to view the Cyclone URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, cycloneUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Setting up the correct view in the event there is no network connection
        noNetworkTextView = (TextView) findViewById(R.id.no_network);
        cycloneListView.setEmptyView(noNetworkTextView);

        //Setting up the correct view in the event there is no Cyclones to list
        emptyStateTextView = (TextView) findViewById(R.id.empty_cyclone_list);
        cycloneListView.setEmptyView(emptyStateTextView);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(CYCLONE_LOADER_ID, null, this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready
        //Calling up the map fragment from cyclone_list.xml
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        //Setting up map markers for Cyclone Position with custom icons
        cyclonePosition = new MarkerOptions().position(new LatLng(cycloneLatitude, cycloneLongitude))
                .title(cycloneName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cyclone_shape));
    }

    @Override
    public Loader<List<Cyclone>> onCreateLoader(int i, Bundle bundle) {

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
    public void onLoadFinished(Loader<List<Cyclone>> loader, List<Cyclone> cyclones) {

        //No connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            noNetworkTextView.setText(R.string.no_network);
        } else {
            // Set empty state text to display "No cyclones found."
            emptyStateTextView.setText(R.string.no_cyclones_found);
        }

        //Stop displaying Progressbar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous cyclone data
        adapter.clear();

        // If there is a valid list of {@link Cyclone}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (cyclones != null && !cyclones.isEmpty()) {
            adapter.addAll(cyclones);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Cyclone>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap mapLocalInstance) {
        //Setting mapReady to true
        mapReady = true;

        //Loading local instance map from Callback
        googleMap = mapLocalInstance;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Placing cyclone map marker (cycloneName) on Map
        mapLocalInstance.addMarker(cyclonePosition);

        //checks for permission using the Support library before enabling the My Location layer
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Method GoogleApiClient.Builder is used to configure client.
     * .addConnectionCallbacks provides callbacks that are called when client is connected or disconnected.
     * .addOnConnectionFailedListener handles failed connection attempts to service.
     * .addApi adds the LocationServices API endpoint from Google Play Services.
     * googleApiClient.connect(): A client must be connected before executing any operation.
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /**
     * In a Google Maps App, it is always required to update current location of user at regular
     * intervals. Also we may want current velocity, altitude etc. These all are covered inside
     * the location object which can be retrieved using fused location provider.
     * Fused Location Provider analyses GPS, Cellular and Wi-Fi network location data in order to
     * provide the highest accuracy data. It uses different device sensors to define if a user is
     * walking, riding a bicycle, driving a car or just standing in order to adjust the frequency
     * of location updates. It also helps in android location tracking.
     * This will be used to get the last updated location.
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Using this Google predefined function onLocationChanged() that will be called
     * as soon as user location has changed.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        //Place current location marker
        LatLng userLatLon = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(userLatLon);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLon));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    /**
     * App will not be granted any permission at installation. Instead, app will ask user for
     * permission one-by-one at runtime
     */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    //Handles the result of the permission request by implementing the
    //ActivityCompat.OnRequestPermissionsResultCallback from the Support library
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}