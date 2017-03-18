package com.palarran.cycloops;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class CycloneMap extends AppCompatActivity implements OnMapReadyCallback {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Calling up the map fragment from activity_main.xml
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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