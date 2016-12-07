package com.kewldevs.sathish.faceless;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sathish on 12/4/16.
 */

public class ViewedByFrags extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    String TAG = "CARD";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<String, ViewedCardsViewHolder> mAdapter;
    ActionBar actionBar;
    FragmentManager fragmentManager;

    public ViewedByFrags(FragmentManager fragmentManager) {
        BucketActivity.isViewFragment = true;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viewed_by, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_viewed);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Viewed by");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshViewedList);
        recyclerView = (RecyclerView) view.findViewById(R.id.your_views_recycler_view);
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
        FirebaseHelper.checkForEmptyViews(swipeRefreshLayout, getActivity());
        mAdapter = new FirebaseRecyclerAdapter<String, ViewedCardsViewHolder>(String.class, R.layout.viewedby_card_view, ViewedCardsViewHolder.class, FirebaseHelper.mMyBucketListViewedReference) {
            @Override
            protected void populateViewHolder(final ViewedCardsViewHolder viewHolder, String model, int position) {
                FirebaseHelper.mUserReference.child(model).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String name = (String) dataSnapshot.getValue();
                            Log.d(TAG, "onDataChange: Name " + name);
                            viewHolder.USR_NAME.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FirebaseHelper.mUserReference.child(model).child("img").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String url = (String) dataSnapshot.getValue();
                            Log.d(TAG, "onDataChange: URL " + url);
                            ImageSetTask image = new ImageSetTask(viewHolder.USR_IMG, url);
                            image.execute();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            fragmentManager.beginTransaction().replace(R.id.bucket_container, new BucketFrags(fragmentManager)).commit();
        }
        return true;
    }
}
