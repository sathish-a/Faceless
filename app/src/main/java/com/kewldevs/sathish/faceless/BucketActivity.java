package com.kewldevs.sathish.faceless;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.kewldevs.sathish.faceless.FirebaseHelper.checkForEmptyBucket;

public class BucketActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String TAG = "CARD";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    ActionBar actionBar;
    FirebaseRecyclerAdapter<Food, FoodCardsViewHolder> mAdapter;
    ProgressDialog progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_foods);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshBucketList);
        fab = (FloatingActionButton) findViewById(R.id.fabAddFood);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FirebaseHelper.getUserPresentStatus()) {
                    startActivity(new Intent(BucketActivity.this, ProfileActivity.class));
                } else
                    startActivity(new Intent(BucketActivity.this, FoodViewActivity.class));
            }
        });

        
        recyclerView = (RecyclerView) findViewById(R.id.your_foods_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList();
            }
        });
        createList();

    }

    private void createList() {
        swipeRefreshLayout.setRefreshing(true);
        checkForEmptyBucket(swipeRefreshLayout, getApplicationContext());
        mAdapter = new FirebaseRecyclerAdapter<Food, FoodCardsViewHolder>(Food.class, R.layout.foods_card_view, FoodCardsViewHolder.class, FirebaseHelper.mMyBucketListReference) {

            @Override
            protected void populateViewHolder(FoodCardsViewHolder holder, Food cards, int position) {
                // if(progressBar!=null) progressBar.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                createRecyclerView(holder, cards, position);
            }


        };
        recyclerView.setAdapter(mAdapter);
    }


    void createRecyclerView(final FoodCardsViewHolder holder, final Food cards, final int position) {
        holder.NAME.setText(cards.getFood_name());
        holder.DESC.setText(cards.getFood_desc());
        holder.IMG.setImageResource(R.mipmap.ic_launcher);
        holder.AVAIL.setText(cards.getFood_avail_for() + " people(s)");
        holder.EXP.setText("Expire by: " + BucketActivity.this.getResources().getStringArray(R.array.expire_time_array)[Integer.parseInt(cards.getFood_expiry())]);
        holder.POSTON.setText("" + FirebaseHelper.convertTime(cards.getFood_post_onLong()));

        holder.VIEWBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.HIDDENLAYOUT.setVisibility(View.GONE);
                startActivity(new Intent(BucketActivity.this, FoodViewActivity.class).putExtra("FOOD_BUNDLE", cards));

            }
        });

        holder.DELETEBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.mMyBucketListReference.child(cards.getFood_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.activity_your_bucket), cards.getFood_name() + " deleted!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            checkForEmptyBucket(null, getApplicationContext());
                        }
                    }
                });


                holder.HIDDENLAYOUT.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bucket_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @Override
    public boolean onQueryTextChange(String queryString) {
        String lowrCase = queryString.toLowerCase();
        Query q = FirebaseHelper.mMyBucketListReference.orderByChild("food_name").startAt(lowrCase);
       /* mAdapter = new FirebaseRecyclerAdapter<Food, FoodCardsViewHolder>(Food.class,R.layout.foods_card_view, FoodCardsViewHolder.class,q) {

            @Override
            protected void populateViewHolder(FoodCardsViewHolder holder,Food cards,int position) {
                createRecyclerView(holder,cards,position);
            }

        };
        recyclerView.setAdapter(mAdapter);
*/
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot:" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }


    @Override
    protected void onRestart() {
        Log.d(TAG, "Restarted");
        super.onRestart();
        checkForEmptyBucket(null, getApplicationContext());
    }


}
