package com.kewldevs.sathish.faceless;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 567;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 890;
    EditText etName, etPhone, etAddr;
    TextView tvEmail;
    ImageView imUserImg;
    FloatingActionButton fab;
    Boolean toggleEdit;
    RelativeLayout relativeName;
    ActionBar actionBar;
    CollapsingToolbarLayout collapsingToolBar;
    View view;
    ImageButton btPrfEdit;
    UserProfile me;
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
        relativeName = (RelativeLayout) findViewById(R.id.relativeName);
        imUserImg = (ImageView)findViewById(R.id.userImageProfile);
        btPrfEdit = (ImageButton) findViewById(R.id.prfImageEditor);
        if (FirebaseHelper.mMyProfileReference != null) {
            FirebaseHelper.mMyProfileReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    me = dataSnapshot.getValue(UserProfile.class);
                    updateInfo();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        fab = (FloatingActionButton) findViewById(R.id.fab_prof_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode();
            }
        });


        btPrfEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    pickImageFromGallery();
                else requestToReadExternalData();
            }
        });

    }


    private void updateInfo() {
        if(me!=null){
            collapsingToolBar.setTitle(me.getName());
            etName.setText(me.getName());
            etPhone.setText(me.getPhno());
            etAddr.setText(me.getAddress());
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
            if(!name.contentEquals("") && !tele.contentEquals("") && !addr.contentEquals("")){
                UserProfile up = new UserProfile(name, addr, tele, me.getImg(), me.getEmail());
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


    private void requestToReadExternalData() {
        ActivityCompat.requestPermissions(ProfileActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Profile picture"), PICK_IMAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
                // permission was granted, yay! Do the
                // storage-related task you need to do.

            } else {
                Toast.makeText(ProfileActivity.this, "Permission should be granted to access storage!!", Toast.LENGTH_LONG).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            UploadToStorage upToStorage = new UploadToStorage(FirebaseHelper.mMyProfileStorageReference, this, "profile", FirebaseHelper.mMyProfileReference.child("img"), uri, imUserImg);
            upToStorage.execute();
        }
    }
}
