package com.kewldevs.sathish.faceless;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ServerValue;

public class FoodViewActivity extends AppCompatActivity {

    String TAG = "FOOD VIEW";
    ImageView food_img;
    EditText  food_name,food_desc,food_avail;
    Spinner food_time,food_expiry,food_type;
    RelativeLayout relative_food_name;
    FloatingActionButton fab;
    Boolean toggleEdit;
    CollapsingToolbarLayout collapsingToolBar;

    String mKey;
    Food f;


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
        //food_img.setImageResource(Integer.parseInt(f.getFood_img()));
        collapsingToolBar.setTitle(f.getFood_name());
        food_name.setText(f.getFood_name());
        food_desc.setText(f.getFood_desc());
        food_avail.setText(f.getFood_avail_for());
        food_time.setSelection(Integer.parseInt(f.getFood_time_of_cook()));
        food_expiry.setSelection(Integer.parseInt(f.getFood_expiry()));
        food_type.setSelection(Integer.parseInt(f.getFood_type()));
        mKey = f.getFood_key();
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
                    mKey = FirebaseHelper.mMyBucketListReference.push().getKey();
                }


                f = new Food(name,desc,""+avail,""+timePos,""+expPos,""+typePos,"",mKey,""+ ServerValue.TIMESTAMP);
                FirebaseHelper.mMyBucketListReference.child(mKey).setValue(f).addOnCompleteListener(new OnCompleteListener<Void>() {
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



}
