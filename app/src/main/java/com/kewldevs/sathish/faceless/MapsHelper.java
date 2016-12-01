package com.kewldevs.sathish.faceless;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.kewldevs.sathish.faceless.FirebaseHelper.mActiveReference;

/**
 * Created by sathish on 10/15/16.
 */

public class MapsHelper {

    static String TAG = "CARD";
    public static GeoFire mGeoActiveReference = new GeoFire(mActiveReference);


    public static Location getMyLoc(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        Location location;
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) return location;
        else return null;
    }


    public static void setMarkersOnMap(final GoogleMap googleMap, final Context context, GeoLocation location) {

        GeoQuery geo = mGeoActiveReference.queryAtLocation(location,1.0);
        geo.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(TAG,String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(TAG,String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG,String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG,"All initial data has been loaded and events have been fired!");

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG,"There was an error with this query: " + error);
            }
        });



        mActiveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "snapshot has value");
                    GenericTypeIndicator<HashMap<String, Feeds>> gt = new GenericTypeIndicator<HashMap<String, Feeds>>() {
                    };
                    Log.d(TAG, "value:"+dataSnapshot.getValue());
                    HashMap<String, Feeds> a = (HashMap<String, Feeds>) dataSnapshot.getValue(gt);
                    List<Feeds> FeedList = new ArrayList<Feeds>(a.values());
                    if (FeedList != null) {
                        googleMap.clear();
                        Log.d(TAG, "list size:" + FeedList.size());
                        for (Feeds feeds : FeedList) {
                            addMarkersToMap(feeds);
                        }
                    }

                }
                else {
                    Toast.makeText(context, "No Feeds found!!", Toast.LENGTH_SHORT).show();
                }
            }

            private void addMarkersToMap(final Feeds feeds) {
                final String[] title = {null};
                final MarkerOptions mp = new MarkerOptions();
                mp.position(new LatLng(feeds.getLatitude(), feeds.getLongitude()));
                mp.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_heart_marker));
                if (feeds.getUserId().equals(FirebaseHelper.getUID())) {
                    mp.title("me");
                    googleMap.addMarker(mp).setTag(feeds);
                } else {
                    FirebaseHelper.mUserReference.child(feeds.getUserId()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            title[0] = (String) dataSnapshot.getValue();
                            if (title[0] != null)
                                mp.title(title[0]);
                            else mp.title("Not provided");

                            googleMap.addMarker(mp).setTag(feeds);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
}
