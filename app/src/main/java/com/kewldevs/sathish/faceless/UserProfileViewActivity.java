package com.kewldevs.sathish.faceless;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class UserProfileViewActivity extends AppCompatActivity {

    EditText tvUsrProfName, tvUsrAddr, tvUsrPhone, tvUsrEmail;
    ImageView ivUsrImg;
    SwipeRefreshLayout swipeRefreshLayout;
    String userId = null;
    UserProfile user;
    ActionBar actionBar;
    String title = "Profile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        tvUsrAddr = (EditText) findViewById(R.id.UsrprfAddr);
        tvUsrProfName = (EditText) findViewById(R.id.UsrprfName);
        tvUsrPhone = (EditText) findViewById(R.id.UsrprfPh);
        tvUsrEmail = (EditText) findViewById(R.id.UsrprfEmail);
        ivUsrImg = (ImageView) findViewById(R.id.UsrImg);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLay);
        if(getIntent()!=null){
        userId = getIntent().getStringExtra("USERID");
        title = getIntent().getStringExtra("TITLE");
        }
        actionBar.setTitle(title+"'s profile");
        updateInfo(userId);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateInfo(userId);
            }
        });
    }

    private void updateInfo(final String userId) {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseHelper.mUserReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                user = dataSnapshot.getValue(UserProfile.class);
                putIntoViews(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void putIntoViews(UserProfile user) {
        ImageSetTask im = new ImageSetTask(ivUsrImg,user.getImg());
        im.execute();
        tvUsrProfName.setText(user.getName());
        tvUsrPhone.setText(user.getPhno());
        tvUsrAddr.setText(user.getAddress());
        tvUsrEmail.setText(user.getEmail());
    }

}
