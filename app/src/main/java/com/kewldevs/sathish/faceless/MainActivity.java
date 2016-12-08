package com.kewldevs.sathish.faceless;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kewldevs.sathish.faceless.FirebaseHelper.myProfile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int REQUEST_CODE_FOR_ACCESS_FINE_LOCATION = 888;
    private static final int RC_SIGN_IN = 123;
    static CircleImageView drawerUserImage;
    static TextView drawerUserName;
    static TextView drawerUserEmail;
    static Context context;
    View view;
    String TAG = "CARD";
    FragmentManager fragmentManager;
    FirebaseAuth auth;

    //updateDrawerInformation
    public static void updateDrawerInformation() {

        if (myProfile != null) {
            if (drawerUserName != null) drawerUserName.setText(myProfile.getName());
            if (drawerUserEmail != null) drawerUserEmail.setText(myProfile.getEmail());
            if (myProfile.getImg() != null) {
                if (drawerUserImage != null) {
                    Picasso.with(context).load(myProfile.getImg()).into(drawerUserImage);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        context = MainActivity.this;
        if (auth.getCurrentUser() != null) {
            // already signed in
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            Log.d(TAG, "onCreate: User Exists!!");
            initializeViews();

        } else {
            // not signed in
            //calling Firebase UI auth intent
            Log.d(TAG, "onCreate: No user exists!!");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setProviders(Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                    )).setIsSmartLockEnabled(false).build(), RC_SIGN_IN);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Autentication success!! :)", Toast.LENGTH_SHORT).show();
                initializeViews();
            } else {
                Toast.makeText(this, "Autentication failed!! Try again :(", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Initialize views for the activity
    void initializeViews() {
        FirebaseHelper.setmUser(auth.getCurrentUser());
        setContentView(R.layout.activity_main);
        //Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        drawerUserImage = (CircleImageView) v.findViewById(R.id.drawerUserImage);
        drawerUserName = (TextView) v.findViewById(R.id.drawerUserName);
        drawerUserEmail = (TextView) v.findViewById(R.id.drawerUserEmail);
        drawerUserEmail.setOnClickListener(this);
        drawerUserImage.setOnClickListener(this);
        drawerUserName.setOnClickListener(this);
        updateDrawerInformation();
        fragmentManager = this.getFragmentManager();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            makeRequestPermissionForAccessFineLocation();
        } else {
            callMapsFrag();
        }

    }

    //Update map fragment inside the container
    void callMapsFrag() {
        fragmentManager.beginTransaction().add(R.id.main_container, new MapsFrags()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_feeds:
                startActivity(new Intent(MainActivity.this, BucketActivity.class));
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_container);

        drawer.setPressed(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void doLogout() {

        AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Signed out Successfully!!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });

    }


    private void makeRequestPermissionForAccessFineLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_FOR_ACCESS_FINE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_FOR_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMapsFrag();
            } else
                Toast.makeText(this, "Some functions might not work!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawerUserImage:
                gotoProfileActivity();
                break;
            case R.id.drawerUserEmail:
                gotoProfileActivity();
                break;
            case R.id.drawerUserName:
                gotoProfileActivity();
                break;
        }
    }

    void gotoProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }
}
