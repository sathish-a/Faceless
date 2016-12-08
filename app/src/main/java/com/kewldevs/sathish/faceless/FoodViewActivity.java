package com.kewldevs.sathish.faceless;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FoodViewActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 567;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 890;
    private static final String DEFAULT_IMG = "none";
    String TAG = "FOOD VIEW";
    ImageView food_img;
    EditText  food_name,food_desc,food_avail;
    Spinner food_time,food_expiry,food_type;
    RelativeLayout relative_food_name;
    FloatingActionButton fab;
    Boolean toggleEdit;
    CollapsingToolbarLayout collapsingToolBar;
    ImageButton btImageEdit;
    String mKey;
    Food f;
    Map<String, Object> food = new HashMap<>();

    String imgURL = DEFAULT_IMG;
    StorageReference mImgStorageRef = null;
    DatabaseReference mDatabaseRefernce = null;
    ArrayAdapter<CharSequence> cookedTimeAdapter,expireTimeAdapter,foodTypeAdapter;


    int timePos,expPos,typePos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewFoodToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        try {
            f = (Food) getIntent().getSerializableExtra("FOOD_BUNDLE");
            if(f!=null){
                fillContents();
            }else {
                gotoEditMode(true); collapsingToolBar.setTitle("Add Item");
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Where is the food??",Toast.LENGTH_SHORT).show();
        }
    }

    private void fillContents() {
        collapsingToolBar.setTitle(f.getFood_name());
        food_name.setText(f.getFood_name());
        food_desc.setText(f.getFood_desc());
        food_avail.setText(f.getFood_avail_for());
        food_time.setSelection(Integer.parseInt(f.getFood_time_of_cook()));
        food_expiry.setSelection(Integer.parseInt(f.getFood_expiry()));
        food_type.setSelection(Integer.parseInt(f.getFood_type()));
        mKey = f.getFood_key();
        mImgStorageRef = FirebaseHelper.mMyBucketStorageReference.child(f.getFood_key());
        mDatabaseRefernce = FirebaseHelper.mMyBucketListItemsReference.child(f.getFood_key());
        if (f.getFood_img() != null) {
            if (!f.getFood_img().contentEquals(DEFAULT_IMG)) {
                imgURL = f.getFood_img();

                Picasso.with(FoodViewActivity.this).load(imgURL).into(food_img);

            }
        }
    }

    private void init() {

        toggleEdit = false;
        food_img = (ImageView) findViewById(R.id.image_view_food);
        food_name = (EditText) findViewById(R.id.foodName);
        food_desc = (EditText) findViewById(R.id.foodDesc);
        food_avail = (EditText) findViewById(R.id.foodAvail);
        food_time = (Spinner) findViewById(R.id.foodTime_spinner);
        food_expiry = (Spinner) findViewById(R.id.foodExpireTime_spinner);
        food_type = (Spinner) findViewById(R.id.foodType_spinner);
        btImageEdit = (ImageButton) findViewById(R.id.foodImageEditor);
        food_time.setEnabled(false);
        food_expiry.setEnabled(false);
        food_type.setEnabled(false);
        relative_food_name = (RelativeLayout) findViewById(R.id.relativeFoodName);
        fab = (FloatingActionButton) findViewById(R.id.fab_food_edit);
        collapsingToolBar = (CollapsingToolbarLayout) findViewById(R.id.collapsetoolbar_layout_view_food);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEdit();
            }
        });

        cookedTimeAdapter = ArrayAdapter.createFromResource(this,R.array.cooked_time_array,android.R.layout.simple_spinner_item);
        cookedTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        food_time.setAdapter(cookedTimeAdapter);

        expireTimeAdapter = ArrayAdapter.createFromResource(this,R.array.expire_time_array,android.R.layout.simple_spinner_item);
        cookedTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        food_expiry.setAdapter(expireTimeAdapter);

        foodTypeAdapter = ArrayAdapter.createFromResource(this,R.array.food_type_array,android.R.layout.simple_spinner_item);
        cookedTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        food_type.setAdapter(foodTypeAdapter);

        food_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timePos=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        food_expiry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    expPos=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        food_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typePos=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                if (ActivityCompat.checkSelfPermission(FoodViewActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (mKey != null) pickImageFromGallery();
                    else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FoodViewActivity.this);
                        alertDialogBuilder.setTitle("Alert").setMessage("First fill and update the below fields!").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else requestToReadExternalData();

            }
        });
    }


    void gotoEditMode(boolean toggleEdit){
        toggleState(toggleEdit);
        fab.setImageResource(android.R.drawable.ic_menu_save);
        relative_food_name.setVisibility(View.VISIBLE);
    }

    void gotoSaveMode(boolean toggleEdit)
    {
        String name = food_name.getText().toString();
        String desc = food_desc.getText().toString();

        if(timePos!=0 && expPos!=0 && typePos!=0 && !desc.contentEquals("") && !name.contentEquals("") && !food_avail.getText().toString().contentEquals("")){
            collapsingToolBar.setTitle(name);
            int avail = Integer.parseInt(food_avail.getText().toString());
            if(avail!=0) {
                if(mKey==null) {
                    mKey = FirebaseHelper.mMyBucketListItemsReference.push().getKey();
                    mImgStorageRef = FirebaseHelper.mMyBucketStorageReference.child(mKey);
                    mDatabaseRefernce = FirebaseHelper.mMyBucketListItemsReference.child(mKey);
                }

                // to update the database without disturbing img field
                food.put("food_name", "" + name);
                food.put("food_avail_for", "" + avail);
                food.put("food_desc", "" + desc);
                food.put("food_expiry", "" + expPos);
                food.put("food_type", "" + typePos);
                food.put("food_time_of_cook", "" + timePos);
                food.put("food_key", "" + mKey);
                //food.put("food_post_on/timestamp",new HashMap<String ,Object>().put("timestamp", ServerValue.TIMESTAMP));

                food.put("food_post_on/timestamp", ServerValue.TIMESTAMP);
                mDatabaseRefernce.updateChildren(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(findViewById(R.id.food_view_activity), "Successfully updated!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            FirebaseHelper.checkForEmptyBucket(null,FoodViewActivity.this);
                        }else {
                            Snackbar.make(findViewById(R.id.food_view_activity), "Something went wrong!! Try again later!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });

            }

        }else Toast.makeText(this,"Fill all the properties",Toast.LENGTH_SHORT).show();

        toggleState(toggleEdit);
        fab.setImageResource(android.R.drawable.ic_menu_edit);
        relative_food_name.setVisibility(View.GONE);

    }

    private void toggleEdit() {

        toggleEdit = !toggleEdit;
        if(toggleEdit){
            //Edit Mode
            gotoEditMode(toggleEdit);
        }else {
            //Save Mode
            gotoSaveMode(toggleEdit);

        }
    }

    private void toggleState(Boolean mode) {
        food_name.setEnabled(mode);
        food_desc.setEnabled(mode);
        food_avail.setEnabled(mode);
        food_type.setEnabled(mode);
        food_time.setEnabled(mode);
        food_expiry.setEnabled(mode);

    }


    private void requestToReadExternalData() {
        ActivityCompat.requestPermissions(FoodViewActivity.this,
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
                Toast.makeText(FoodViewActivity.this, "Permission should be granted to access storage!!", Toast.LENGTH_LONG).show();
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
            //Do upload task;
            Uri uri = data.getData();
            if (mImgStorageRef != null && mDatabaseRefernce != null) {
                UploadToStorage uploadToStorage = new UploadToStorage(mImgStorageRef, FoodViewActivity.this, mKey, mDatabaseRefernce.child("food_img"), uri, food_img);
                uploadToStorage.execute();
            } else Log.d(TAG, "Upload:onActivityResult:Broken directory ");
        }
    }

}
