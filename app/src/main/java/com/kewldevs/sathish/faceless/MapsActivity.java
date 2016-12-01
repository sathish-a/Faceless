package com.kewldevs.sathish.faceless;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_CODE_FOR_ACCESS_FINE_LOCATION = 888;
    View view;

    CircleImageView drawerUserImage;
    TextView drawerUserName, drawerUserEmail;
    String TAG = "CARD";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        view = (View) findViewById(R.id.coordinatorMap);
        //Floating bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_prof_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, BucketActivity.class));
            }
        });


        //Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        //Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        drawerUserImage = (CircleImageView) v.findViewById(R.id.drawerUserImage);
        drawerUserName = (TextView) v.findViewById(R.id.drawerUserName);
        drawerUserEmail = (TextView) v.findViewById(R.id.drawerUserEmail);
        updateDrawerInformation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void updateDrawerInformation() {

        if (FirebaseHelper.myProfile != null) {
            drawerUserName.setText(FirebaseHelper.myProfile.getName());
            drawerUserEmail.setText(FirebaseHelper.getmUser().getEmail());
            if (FirebaseHelper.myProfile.getImg() != null) {
                ImageSetTask im = new ImageSetTask(drawerUserImage, FirebaseHelper.myProfile.getImg());
                im.execute();
            } else {
                drawerUserImage.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            FirebaseUser user = FirebaseHelper.getmUser();
            if (user.getDisplayName() != null)
                drawerUserName.setText(user.getDisplayName());
            else drawerUserName.setText("Not provided!");
            if (user.getEmail() != null)
                drawerUserEmail.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                ImageSetTask im = new ImageSetTask(drawerUserImage, user.getPhotoUrl().toString());
                im.execute();
            } else {
                drawerUserImage.setImageResource(R.mipmap.ic_launcher);
            }
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_feeds:
                startActivity(new Intent(MapsActivity.this, BucketActivity.class));
                break;
            case R.id.nav_logout:
                doLogout();
                break;
            case R.id.nav_community:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_TC:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doLogout() {

        AuthUI.getInstance().signOut(MapsActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MapsActivity.this, "Signed out Successfully!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

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
        //@@@@@//
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            makeRequestPermissionForAccessFineLocation();
            return;
        }

        //@@@@@//


       /* mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);*/
        startWork();

    }

    void startWork() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        Location myLoc;
        if ((myLoc = MapsHelper.getMyLoc(this)) != null) {
            LatLng me = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 16));
            MapsHelper.setMarkersOnMap(mMap, this,new GeoLocation(myLoc.getLatitude(),myLoc.getLongitude()));
        }

    }


    private void makeRequestPermissionForAccessFineLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_FOR_ACCESS_FINE_LOCATION);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_FOR_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWork();
            } else
                Toast.makeText(this, "Some functions might not work!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Feeds feeds = (Feeds) marker.getTag();
        String name = marker.getTitle();
        Log.d(TAG, feeds.toString() + "Title:" + name);
        if (!feeds.getUserId().contentEquals(FirebaseHelper.getUID()))
            startActivity(new Intent(MapsActivity.this, UserFoodViewActivity.class).putExtra("USER_VIEW", feeds).putExtra("TITLE", name));
        else {
            startActivity(new Intent(MapsActivity.this, BucketActivity.class));
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateDrawerInformation();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"Pressed");
        finish();
    }*/

    /*  @Override
    public void onMapLongClick(LatLng latLng) {
        if(newFeedLatLn == null) {
            marker =  mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Now set name!"));
            Snackbar.make(view, "Location added!!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            marker.remove();
            marker =  mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Now set name!"));
            Snackbar.make(view, "Location updated!!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        newFeedLatLn = latLng;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        newFeedLatLn = marker.getPosition();
        Snackbar.make(view, "Got it!!!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }*/
}
