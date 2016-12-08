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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;
import static com.kewldevs.sathish.faceless.FirebaseHelper.mActiveReference;

/**
 * Created by sathish on 10/15/16.
 */

public class MapsHelper {

    public static GeoFire mGeoActiveReference = new GeoFire(mActiveReference);
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



    public static void DrawCircleOnMap(final GoogleMap map, LatLng latLngCenter, final GeoQuery mQuery) {
        final Circle searchCircle;
        final int INITIAL_ZOOM_LEVEL = 14;
        final Marker[] centerMarker = {map.addMarker(new MarkerOptions().position(latLngCenter))};
        searchCircle = map.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
        searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
        searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng center = cameraPosition.target;
                centerMarker[0].remove();
                centerMarker[0] = map.addMarker(new MarkerOptions().position(center));
                double radius = zoomLevelToRadius(cameraPosition.zoom);
                Log.d(TAG, "onCameraChange: " + center.toString() + ",radius:" + radius);
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



}
