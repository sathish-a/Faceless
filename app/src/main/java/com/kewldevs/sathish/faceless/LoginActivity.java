package com.kewldevs.sathish.faceless;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity  {

    String TAG = "CARD";
    FirebaseAuth auth;
    int RC_SIGN_IN = 0;
    FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setProviders(AuthUI.GOOGLE_PROVIDER,AuthUI.FACEBOOK_PROVIDER).build(),RC_SIGN_IN);
        }

        authListener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG,"USER EXISTS");
                    FirebaseHelper.setmUser(user);
                    gotoMapActivity();
                }else {
                    Log.d(TAG,"USER SIGNED OUT");
                }
            }
        };

    }

    private void gotoMapActivity() {
        FirebaseHelper.isProfileInformationPresent();
        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"Autentication success!! :)",Toast.LENGTH_SHORT).show();
                gotoMapActivity();
            }else {Toast.makeText(this,"Autentication failed!! Try again :(",Toast.LENGTH_SHORT).show();
            finish();}
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener!=null)auth.removeAuthStateListener(authListener);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"Pressed");
        finish();
    }


}