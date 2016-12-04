package com.kewldevs.sathish.faceless;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_FOR_ACCESS_FINE_LOCATION = 888;
    View view;

    CircleImageView drawerUserImage;
    TextView drawerUserName, drawerUserEmail;
    String TAG = "CARD";
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        drawerUserImage = (CircleImageView) v.findViewById(R.id.drawerUserImage);
        drawerUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        drawerUserName = (TextView) v.findViewById(R.id.drawerUserName);
        drawerUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

            }
        });
        drawerUserEmail = (TextView) v.findViewById(R.id.drawerUserEmail);
        drawerUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        updateDrawerInformation();
        fragmentManager = this.getFragmentManager();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            makeRequestPermissionForAccessFineLocation();
        } else {
            callMapsFrag();
        }

    }

    void callMapsFrag() {
        fragmentManager.beginTransaction().add(R.id.main_container, new MapsFrags(this)).commit();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_container);
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

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
        updateDrawerInformation();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}
