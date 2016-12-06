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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sathish on 10/14/16.
 */

public class FirebaseHelper {



    public static String TAG = "CARD";
    public static String UID;
    public static String DEFAULT_FILL_MSG = "Not Updated";
    public static String DEFAULT_USR_PRF = "https://firebasestorage.googleapis.com/v0/b/bucketlist-f440c.appspot.com/o/default_system%2Fdefault_img.png?alt=media&token=154fdd52-1131-46fb-976e-c154dc3ec09a";
    public static FirebaseUser mUser;
    public static DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference mUserReference = mRoot.child("Users");
    public static DatabaseReference mActiveReference = mRoot.child("ActiveList");
    public static DatabaseReference mBucketListRefernce = mRoot.child("BucketLists");
    public static DatabaseReference mMyActiveReference;
    public static DatabaseReference mMyProfileReference;
    public static DatabaseReference mMyBucketListReference;
    public static DatabaseReference mMyBucketListItemsReference;
    public static DatabaseReference mMyBucketListViewedReference;

    public static UserProfile myProfile;
    public static GeoFire mGeoActiveReference = new GeoFire(mActiveReference);


    public static StorageReference mStorageRoot = FirebaseStorage.getInstance().getReference();
    public static StorageReference mMyStorageReference;
    public static StorageReference mMyBucketStorageReference;
    public static StorageReference mMyProfileStorageReference;

    public static Boolean isInfoPresent = false;
    private static boolean infoPresent;


    public static void setmUser(FirebaseUser User) {
        FirebaseHelper.mUser = User;
        setUID(User.getUid());
    }

    public static void downloadUserProfileDatas() {
        if (mMyProfileReference != null) {
            mMyProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(TAG, "DownloadUserData:onDataChange: InfoPresent!");
                        myProfile = dataSnapshot.getValue(UserProfile.class);
                    } else {
                        Log.d(TAG, "DownloadUserData:onDataChange: InfoNotPresent!");
                        myProfile = new UserProfile();
                        if (mUser.getPhotoUrl() != null)
                            myProfile.setImg(mUser.getPhotoUrl().toString());
                        else myProfile.setImg(DEFAULT_USR_PRF);
                        myProfile.setName(mUser.getDisplayName());
                        myProfile.setEmail(mUser.getEmail());
                        myProfile.setPhno(DEFAULT_FILL_MSG);
                        myProfile.setAddress(DEFAULT_FILL_MSG);
                        updateProfileInfo(myProfile, null);
                    }

                    if (!myProfile.getAddress().contentEquals(DEFAULT_FILL_MSG) && !myProfile.getPhno().contentEquals(DEFAULT_FILL_MSG))
                        isInfoPresent = true;
                    else isInfoPresent = false;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    public static String getUID() {
        return UID;
    }

    public static void setUID(String mUID) {
        UID = mUID;
        Log.d(TAG, "User id is:" + UID);
        mMyProfileReference = mUserReference.child(UID);
        mMyActiveReference = mActiveReference.child(UID);
        mMyBucketListReference = mBucketListRefernce.child(UID);
        mMyBucketListItemsReference = mMyBucketListReference.child("items");
        mMyBucketListViewedReference = mMyBucketListReference.child("viewed");
        downloadUserProfileDatas();
        mMyStorageReference = mStorageRoot.child(UID);
        mMyBucketStorageReference = mMyStorageReference.child("Bucket");
        mMyProfileStorageReference = mMyStorageReference.child("Profile");

    }

    public static void updateProfileInfo(final UserProfile profile, final Context context) {
        mMyProfileReference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    myProfile = profile;
                    Log.d(TAG, "Info updated!!");
                    if (context != null)
                        Toast.makeText(context, "Profile updated!!", Toast.LENGTH_SHORT).show();
                } else Log.d(TAG, String.valueOf(task.getResult()));
            }
        });

    }



    public static void checkForEmptyBucket(final SwipeRefreshLayout swipeRefreshLayout, final Context context) {


        FirebaseHelper.mMyBucketListItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            if (error == null) {
                                mMyBucketListReference.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Log.d(TAG, "onComplete: Node Deleted from both ActiveLists and BucketLists");
                                        }
                                    }
                                });
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


    public static void addLogtoViewedReference(String key) {
        mBucketListRefernce.child(key).child("viewed").child(UID).setValue(UID);
    }

    public static void checkForEmptyViews(final SwipeRefreshLayout swipeRefreshLayout, final Context context) {
        mMyBucketListViewedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(context, "None viewed", Toast.LENGTH_SHORT).show();
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

    public static boolean isInfoPresent() {
        return infoPresent;
    }

    /*
            Firebase Storage
    */


}
