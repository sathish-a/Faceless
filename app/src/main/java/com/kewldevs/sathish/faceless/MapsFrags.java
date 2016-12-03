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
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.kewldevs.sathish.faceless.FirebaseHelper.mUserReference;
import static com.kewldevs.sathish.faceless.R.id.map;

/**
 * Created by sathish on 12/1/16.
 */

public class MapsFrags extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GeoQueryEventListener {

    public static Map<String, Marker> markers = new HashMap<String, Marker>();
    View view;
    MapView mapView;
    Context context;
    String TAG = "CARD";
    GoogleMap mMap;
    GeoLocation INITIAL_CENTER;
    GeoQueryEventListener mGeoQueryListeners;
    GeoQuery mQuery;

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

            mQuery = MapsHelper.mGeoActiveReference.queryAtLocation(INITIAL_CENTER, 0.5);
            mQuery.addGeoQueryEventListener(this);
            MapsHelper.DrawCircleOnMap(mMap, new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude), mQuery);

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
        try {
            Feeds feeds = (Feeds) marker.getTag();
            String name = marker.getTitle();
            Log.d(TAG, feeds.toString() + "Title:" + name);
            if (!feeds.getKey().contentEquals(FirebaseHelper.getUID()))
                startActivity(new Intent(context, UserFoodViewActivity.class).putExtra("USER_VIEW", feeds).putExtra("TITLE", name));
            else {
                startActivity(new Intent(context, BucketActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }


    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {
        Log.d(TAG, String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

        mUserReference.child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String name = (String) dataSnapshot.getValue();
                    MarkerOptions markerOption = new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_heart_marker)).title(name);
                    Marker marker = mMap.addMarker(markerOption);
                    marker.setTag(new Feeds(key, name));
                    markers.put(key, marker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onKeyExited(String key) {
        Marker marker = markers.get(key);
        if (marker != null) marker.remove();
        markers.remove(key);
        Log.d(TAG, "onKeyExited: " + key + " has been removed!!");
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mQuery != null) {
            mQuery.addGeoQueryEventListener(this);
            Log.d(TAG, "onResume: Listener Attached!!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mQuery != null) {
            mQuery.removeAllListeners();
            Log.d(TAG, "onPause: Listener Detached");
            removeMarkers();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQuery != null) {
            mQuery.removeAllListeners();
            Log.d(TAG, "onStop: Listener Detached");
            removeMarkers();
        }


    }

    void removeMarkers() {

        for (Marker marker : markers.values()) {
            marker.remove();
        }
        markers.clear();
    }



}
