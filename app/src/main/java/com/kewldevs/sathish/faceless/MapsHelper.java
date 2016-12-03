package com.kewldevs.sathish.faceless;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static com.kewldevs.sathish.faceless.FirebaseHelper.mActiveReference;
import static com.kewldevs.sathish.faceless.FirebaseHelper.mUserReference;

/**
 * Created by sathish on 10/15/16.
 */

public class MapsHelper {

    public static GeoFire mGeoActiveReference = new GeoFire(mActiveReference);
    public static GeoQuery mQuery;
    public static Map<String, Marker> markers = new HashMap<String, Marker>();
    static String TAG = "CARD";

    public static Location getMyLoc(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        Location location;
        // Getting the name of the best provider
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) return location;
        else {
            location = locationManager.getLastKnownLocation(locationProvider);
            locationProvider = LocationManager.GPS_PROVIDER;
            if (location != null) return location;
            else {
                location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) return location;
                else return null;
            }
        }


    }


    public static void setMarkersOnMap(final GoogleMap googleMap, final Context context, GeoLocation centerLocation) {

        mQuery = mGeoActiveReference.queryAtLocation(centerLocation, 0.5);
        mQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
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
                            Marker marker = googleMap.addMarker(markerOption);
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
                Log.d(TAG, String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "All initial data has been loaded and events have been fired!");

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG, "There was an error with this query: " + error);
            }
        });

    }

    public static void DrawCircleOnMap(GoogleMap map, LatLng latLngCenter) {
        final Circle searchCircle;
        final int INITIAL_ZOOM_LEVEL = 14;
        searchCircle = map.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
        searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
        searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng center = cameraPosition.target;
                double radius = zoomLevelToRadius(cameraPosition.zoom);
                Log.e(TAG, "onCameraChange: " + center.toString() + ",radius:" + radius);
                searchCircle.setCenter(center);
                searchCircle.setRadius(radius);
                mQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                // radius in km
                mQuery.setRadius(radius / 1000);
            }
        });
    }

    private static double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(2, zoomLevel);
    }


    public static void stopListners() {
        mQuery.removeAllListeners();

        Log.d(TAG, "stopListners: Fired");
    }
}
