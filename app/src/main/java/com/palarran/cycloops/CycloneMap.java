package com.palarran.cycloops;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class CycloneMap extends SupportMapFragment {

    //Defining Google Map objects variables
    private GoogleMap googleMapView;
    boolean mapReady = false;

    static final CameraPosition START_POINT = CameraPosition.builder()
            .target(new LatLng(38.1254, -101.1703))
            .zoom(3)
            .bearing(359)
            .tilt(5)
            .build();

    private void initGoogleMap() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //Setting mapReady to true
                mapReady = true;

                //Loading local instance map from Callback
                googleMapView = googleMap;

                //Set map type to Satellite view
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                //Set camera at starting point, high over the middle of the U.S of A.
                initialCameraPosition(START_POINT);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cyclone_list, container, false);
    }

    //@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initGoogleMap();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initialCameraPosition(CameraPosition target) {
        //Setting position to the target created above
        googleMapView.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}