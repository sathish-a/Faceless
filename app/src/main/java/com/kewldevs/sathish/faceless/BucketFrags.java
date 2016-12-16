package com.kewldevs.sathish.faceless;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.kewldevs.sathish.faceless.FirebaseHelper.checkForEmptyBucket;

/**
 * Created by sathish on 12/4/16.
 */

public class BucketFrags extends Fragment implements SearchView.OnQueryTextListener {

    String TAG = "CARD";

    View view;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    ActionBar actionBar;

    FirebaseRecyclerAdapter<Food, FoodCardsViewHolder> mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    FragmentManager fragmentManager;


    public BucketFrags() {

    }


    public BucketFrags(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bucket_lists, container, false);

        BucketActivity.isViewFragment = false;

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_foods);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshBucketList);
        fab = (FloatingActionButton) view.findViewById(R.id.fabAddFood);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FirebaseHelper.isInfoPresent()) {
                    Toast.makeText(getActivity(), "First fill the details inorder to add items", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                } else
                    startActivity(new Intent(getActivity(), FoodViewActivity.class));
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.your_foods_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList();
            }
        });
        createList();
        return view;
    }


    private void createList() {
        swipeRefreshLayout.setRefreshing(true);
        checkForEmptyBucket(swipeRefreshLayout, getActivity());
        mAdapter = new FirebaseRecyclerAdapter<Food, FoodCardsViewHolder>(Food.class, R.layout.foods_card_view, FoodCardsViewHolder.class, FirebaseHelper.mMyBucketListItemsReference) {

            @Override
            protected void populateViewHolder(FoodCardsViewHolder holder, Food cards, int position) {
                swipeRefreshLayout.setRefreshing(false);
                createRecyclerView(holder, cards, position);
            }


        };

        recyclerView.setAdapter(mAdapter);
    }


    void createRecyclerView(final FoodCardsViewHolder holder, final Food cards, final int position) {
        holder.NAME.setText(cards.getFood_name());
        holder.DESC.setText(cards.getFood_desc());
        if (cards.getFood_img() != null) {
            Picasso.with(getActivity()).load(cards.getFood_img()).into(holder.IMG);

        }
        holder.AVAIL.setText(cards.getFood_avail_for() + " people(s)");
        holder.EXP.setText("Expire by: " + getResources().getStringArray(R.array.expire_time_array)[Integer.parseInt(cards.getFood_expiry())]);
        holder.POSTON.setText("" + FirebaseHelper.convertTime(cards.getFood_post_onLong()));

        holder.VIEWBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.HIDDENLAYOUT.setVisibility(View.GONE);
                //open the detailed form of item
                startActivity(new Intent(getActivity(), FoodViewActivity.class).putExtra("FOOD_BUNDLE", cards));

            }
        });

        holder.DELETEBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Key = cards.getFood_key();
                //Delete image from firebase storage
                FirebaseHelper.mMyBucketStorageReference.child(Key).child(Key + ".png").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: File Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: File Not Deleted");
                    }
                });

                //Delete node from DB
                FirebaseHelper.mMyBucketListItemsReference.child(Key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(view.findViewById(R.id.activity_your_bucket), cards.getFood_name() + " deleted!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            checkForEmptyBucket(null, getActivity());
                        }
                    }
                });

                holder.HIDDENLAYOUT.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bucket_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem viewedByItem = menu.findItem(R.id.action_viewed_by);

        viewedByItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //update viewed by fragments
                fragmentManager.beginTransaction().replace(R.id.bucket_container, new ViewedByFrags(fragmentManager)).commit();
                return false;
            }
        });

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        String lowrCase = newText.toLowerCase();
        Query q = FirebaseHelper.mMyBucketListItemsReference.orderByChild("food_name").startAt(lowrCase);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot:" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkForEmptyBucket(null, getActivity());
    }
}
