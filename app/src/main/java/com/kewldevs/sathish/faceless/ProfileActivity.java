package com.kewldevs.sathish.faceless;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    EditText etName,etPhone,etAddr,etLatLng;
    TextView tvEmail;
    ImageView imUserImg;
    FloatingActionButton fab;
    Boolean toggleEdit;
    RelativeLayout relativeName;
    ActionBar actionBar;
    CollapsingToolbarLayout collapsingToolBar;
    Location mLoc;
    View view;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }


    private void init() {
        toggleEdit = false;
        collapsingToolBar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.prfToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        tvEmail = (TextView) findViewById(R.id.prfEmail);
        etName = (EditText) findViewById(R.id.prfName);
        etPhone = (EditText) findViewById(R.id.prfPh);
        etAddr = (EditText) findViewById(R.id.prfAddr);
        etLatLng = (EditText) findViewById(R.id.prfLoc);
        relativeName = (RelativeLayout) findViewById(R.id.relativeName);
        imUserImg = (ImageView)findViewById(R.id.userImageProfile);
        mLoc = MapsHelper.getMyLoc(this);
        updateInfo();
        fab = (FloatingActionButton) findViewById(R.id.fab_prof_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode();
            }
        });

    }

    private void updateInfo() {
        UserProfile me = FirebaseHelper.myProfile;
        if(me!=null){
            collapsingToolBar.setTitle(me.getName());
            etName.setText(me.getName());
            etPhone.setText(me.getPhno());
            etAddr.setText(me.getAddress());
            etLatLng.setText(me.getLat()+","+me.getLng());
            tvEmail.setText(me.getEmail());
            ImageSetTask imageSetTask = new ImageSetTask(imUserImg, me.getImg());
            imageSetTask.execute();
        }
    }

    private void toggleMode() {
        toggleEdit = !toggleEdit;
        if(toggleEdit){
            //Edit Mode
            toggleState(toggleEdit);
            fab.setImageResource(android.R.drawable.ic_menu_save);
            relativeName.setVisibility(View.VISIBLE);
        }else {
            //Save Mode
            toggleState(toggleEdit);
            fab.setImageResource(android.R.drawable.ic_menu_edit);
            relativeName.setVisibility(View.GONE);
            String name = etName.getText().toString();
            String tele = etPhone.getText().toString();
            String addr = etAddr.getText().toString();
            String lat = "";
            String lon = "";
            if(mLoc!=null){
                lat =""+mLoc.getLatitude();
                lon = ""+mLoc.getLongitude();
            }
            if(!name.contentEquals("") && !tele.contentEquals("") && !addr.contentEquals("")){
                UserProfile up = new UserProfile(name,addr,tele,lat,lon,FirebaseHelper.getmUser().getPhotoUrl().toString(),FirebaseHelper.getmUser().getEmail());
                FirebaseHelper.updateProfileInfo(up,this);
                collapsingToolBar.setTitle(name);
            }else Toast.makeText(this,"Please fill the details",Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleState(Boolean toggle) {
        etName.setEnabled(toggle);
        etAddr.setEnabled(toggle);
        etPhone.setEnabled(toggle);
    }

}
