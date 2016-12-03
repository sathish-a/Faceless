package com.kewldevs.sathish.faceless;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sathish on 10/14/16.
 */

public class FirebaseHelper {


    public static String TAG = "CARD";
    public static String UID;
    public static FirebaseUser mUser;
    public static DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference mUserReference = mRoot.child("Users");
    public static DatabaseReference mActiveReference = mRoot.child("ActiveList");
    public static DatabaseReference mBucketListRefernce = mRoot.child("BucketLists");
    public static DatabaseReference mMyActiveReference;
    public static DatabaseReference mMyProfileReference;
    public static DatabaseReference mMyBucketListReference;
    public static boolean isUserInfoPresent = false;
    public static UserProfile myProfile;

    public static GeoFire mGeoActiveReference = new GeoFire(mActiveReference);


    public static void checkForEmptyBucket(final SwipeRefreshLayout swipeRefreshLayout, final Context context) {

        FirebaseHelper.mMyBucketListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                if (dataSnapshot.getValue() == null) {
                    goOffline();
                    Log.d(TAG, "goOffline");
                } else {
                    goOnline(context);
                    Log.d(TAG, "goOnline");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void goOnline(final Context context) {
        mMyActiveReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //If my bucket is not empty then my id is appear in Activelist
                    Location location = MapsHelper.getMyLoc(context);
                    if (location != null) {
                        Log.d(TAG, "lat:" + location.getLatitude() + ",lon" + location.getLongitude());

                        mGeoActiveReference.setLocation(UID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error == null) {
                                    Log.d(TAG, "Value Added");
                                } else {
                                    Log.d(TAG, "Error Online");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void goOffline() {
        mMyActiveReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //If my bucket is  empty then my id is removed from Activelist
                    mGeoActiveReference.removeLocation(UID, new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Log.d(TAG, "Value Removed");
                            } else Log.d(TAG, "Error Offline");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static String convertTime(Long unixtime) {
        Date dateObject = new Date(unixtime);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy hh:mmaa");
        return dateFormatter.format(dateObject);
    }


    public static String getUID() {
        return UID;
    }

    public static void setUID(String mUID) {
        UID = mUID;
        Log.d(TAG, "User id is:" + UID);
        mMyActiveReference = mActiveReference.child(UID);
        mMyProfileReference = mUserReference.child(UID);
        mMyBucketListReference = mBucketListRefernce.child(UID);
    }

    public static void updateProfileInfo(UserProfile profile, final Context context) {
        mMyProfileReference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    isProfileInformationPresent();
                    Log.d(TAG, "Info updated!!");
                    if (context != null)
                        Toast.makeText(context, "Profile updated!!", Toast.LENGTH_SHORT).show();
                } else Log.d(TAG, String.valueOf(task.getResult()));
            }
        });

    }

    public static void isProfileInformationPresent() {

        mMyProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    isUserInfoPresent = true;
                    myProfile = dataSnapshot.getValue(UserProfile.class);
                } else isUserInfoPresent = false;
                Log.d(TAG, "isInfoPresent:" + isUserInfoPresent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    public static boolean getUserPresentStatus() {
        return isUserInfoPresent;
    }

    public static FirebaseUser getmUser() {
        return mUser;
    }

    public static void setmUser(FirebaseUser User) {
        FirebaseHelper.mUser = User;
        setUID(mUser.getUid());
    }


}
