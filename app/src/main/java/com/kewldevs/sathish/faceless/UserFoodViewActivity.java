package com.kewldevs.sathish.faceless;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class UserFoodViewActivity extends AppCompatActivity {

    Feeds feeds;
    String TAG = "CARD";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food,UserFoodCardsViewHolder> mAdapter;
    DatabaseReference userRefernce;
    ActionBar actionBar;
    SwipeRefreshLayout swipeRefresh;
    String userId,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_food_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar_foods);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        feeds = (Feeds) getIntent().getSerializableExtra("USER_VIEW");
        title = getIntent().getStringExtra("TITLE");
        if(title!=null){
            actionBar.setTitle(title+"'s Bucket List");
        }else actionBar.setTitle("Bucket List");
        if(feeds!=null){
            userRefernce = FirebaseHelper.mBucketListRefernce.child(feeds.getUserId());
            userId = feeds.getUserId();
            Log.d(TAG,"refernce:"+feeds.getUserId());
        }
        recyclerView = (RecyclerView) findViewById(R.id.userRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshUserBucketList);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList();
                swipeRefresh.setRefreshing(false);
            }
        });
        createList();

    }

    private void createList() {
        mAdapter = new FirebaseRecyclerAdapter<Food, UserFoodCardsViewHolder>(Food.class,R.layout.users_foods_card_view,UserFoodCardsViewHolder.class,userRefernce) {
            @Override
            protected void populateViewHolder(UserFoodCardsViewHolder holder, Food model, int position) {
                holder.NAME.setText(model.getFood_name());
                holder.DESC.setText("Description:"+model.getFood_desc());
                holder.IMG.setImageResource(R.mipmap.ic_launcher);
                holder.AVAIL.setText(model.getFood_avail_for()+" people(s)");
                holder.EXP.setText("Expire by: "+UserFoodViewActivity.this.getResources().getStringArray(R.array.expire_time_array)[Integer.parseInt(model.getFood_expiry())]);
                holder.TIME_OF_COOK.setText("Cooked at: "+UserFoodViewActivity.this.getResources().getStringArray(R.array.cooked_time_array)[Integer.parseInt(model.getFood_time_of_cook())]);
                holder.TYPE.setText(UserFoodViewActivity.this.getResources().getStringArray(R.array.food_type_array)[Integer.parseInt(model.getFood_type())]);
                holder.POSTON.setText(""+FirebaseHelper.convertTime(model.getFood_post_onLong()));
            }
        };
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_bucket_list, menu);

         MenuItem profileItem = menu.findItem(R.id.action_user_profile);
        profileItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Log.d(TAG,"Clicked");
                startActivity(new Intent(UserFoodViewActivity.this,UserProfileViewActivity.class).putExtra("USERID",userId).putExtra("TITLE",title));
                return false;
            }
        });


        return true;

    }


}
