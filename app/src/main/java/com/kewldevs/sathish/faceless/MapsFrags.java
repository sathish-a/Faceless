package com.kewldevs.sathish.faceless;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static com.kewldevs.sathish.faceless.R.id.map;

/**
 * Created by sathish on 12/1/16.
 */

public class MapsFrags extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    View view;
    MapView mapView;
    Context context;
    String TAG = "CARD";
    GoogleMap mMap;
    GeoLocation INITIAL_CENTER;
    GeoQueryEventListener mGeoQueryListeners;

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
        mapView = (MapView) view.findViewById(map);
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
        mMap.setMyLocationEnabled(true);
        startWork();
    }

    void startWork() {
        Log.d(TAG, "startWork: Started");
        mMap.setOnMarkerClickListener(this);
        Location myLoc;
        myLoc = MapsHelper.getMyLoc(context);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (myLoc == null)
            myLoc = mMap.getMyLocation();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (myLoc != null) {
            LatLng me = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 20));
            INITIAL_CENTER = new GeoLocation(myLoc.getLatitude(), myLoc.getLongitude());
            MapsHelper.setMarkersOnMap(mMap, context, INITIAL_CENTER);
            MapsHelper.DrawCircleOnMap(mMap, new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude));
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Sorry!!").setMessage("Unable to fetch your current location. Turn On Location service(GPS) from settings!!!").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startWork();
                }
            }).show();
        }


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Feeds feeds = (Feeds) marker.getTag();
        String name = marker.getTitle();
        Log.d(TAG, feeds.toString() + "Title:" + name);
        if (!feeds.getKey().contentEquals(FirebaseHelper.getUID()))
            startActivity(new Intent(context, UserFoodViewActivity.class).putExtra("USER_VIEW", feeds).putExtra("TITLE", name));
        else {
            startActivity(new Intent(context, BucketActivity.class));
        }
        return false;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Fired");


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Fired");


    }



}
