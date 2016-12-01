package com.kewldevs.sathish.faceless;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by sathish on 12/1/16.
 */

public class MapsFrags extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    View view;
    MapView mapView;
    Context context;
    String TAG = "CARD";
    GoogleMap mMap;

    public MapsFrags(Context context) {
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mid_maps, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_prof_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, BucketActivity.class));
            }
        });
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startWork();
    }

    void startWork() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        Location myLoc;
        if ((myLoc = MapsHelper.getMyLoc(context)) != null) {
            LatLng me = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 16));
            MapsHelper.setMarkersOnMap(mMap, context, new GeoLocation(myLoc.getLatitude(), myLoc.getLongitude()));
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Feeds feeds = (Feeds) marker.getTag();
        String name = marker.getTitle();
        Log.d(TAG, feeds.toString() + "Title:" + name);
        if (!feeds.getUserId().contentEquals(FirebaseHelper.getUID()))
            startActivity(new Intent(context, UserFoodViewActivity.class).putExtra("USER_VIEW", feeds).putExtra("TITLE", name));
        else {
            startActivity(new Intent(context, BucketActivity.class));
        }
        return false;
    }
}
