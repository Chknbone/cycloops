package com.palarran.cycloops;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class CycloneMap extends Fragment implements OnMapReadyCallback {

    //Defining Google Map objects variables
    GoogleMap mMap;
    boolean mapReady=false;

    static final CameraPosition START_POINT = CameraPosition.builder()
            .target(new LatLng(38.1254, -101.1703))
            .zoom(3)
            .bearing(359)
            .tilt(5)
            .build();

    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_layout);
//
//        //Calling up the map fragment from activity_main.xml
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
//        mapFragment.getMapAsync(this);
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    //Google map object that will change the map fragment in activity_main.xml
    @Override
    //onMapReady CallBack method
    public void onMapReady(GoogleMap map) {

        //Setting mapReady to true
        mapReady=true;

        //Loading local instance map from Callback
        mMap = map;

        //Set camera at starting point, high over the middle of the U.S of A.
        initialCameraPosition(START_POINT);
    }

    private void initialCameraPosition(CameraPosition target) {
        //Setting position to the target created above
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}