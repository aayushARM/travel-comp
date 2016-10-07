package com.android.aayush.travelcompaniondemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class FullscreenMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fullscreen_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(MainDrawerActivity.currLocMarkerOptions);
        if(MainDrawerActivity.SEARCH_BY_NAME_CLICKED==0){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainDrawerActivity.currLocMarkerOptions.getPosition(),15));
        }
        else if(MainDrawerActivity.SEARCH_BY_NAME_CLICKED==1)
        {
            mMap.addMarker(MainDrawerActivity.placeSrchMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainDrawerActivity.placeSrchMarkerOptions.getPosition(),15));
        }
        else
        {
            for(int p = 0; p< MainDrawerActivity.nearbyMarkerOptions.length; p++)
            {
                mMap.addMarker(MainDrawerActivity.nearbyMarkerOptions[p]);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainDrawerActivity.currLocMarkerOptions.getPosition(),15));
        }
    }
}
