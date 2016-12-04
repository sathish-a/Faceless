package com.kewldevs.sathish.faceless;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BucketActivity extends AppCompatActivity {

    public static boolean isViewFragment = false;
    String TAG = "CARD";
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_lists);

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.bucket_container, new BucketFrags(fragmentManager)).commit();

    }



    @Override
    public void onBackPressed() {
        if (isViewFragment) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.bucket_container, new BucketFrags(fragmentManager)).commit();
            isViewFragment = false;
        } else super.onBackPressed();


    }
}
