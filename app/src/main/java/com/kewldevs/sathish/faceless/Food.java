package com.kewldevs.sathish.faceless;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sathish on 9/30/16.
 */

public class Food implements Serializable {
    String food_name;
    String food_avail_for;
    String food_desc;
    String food_img;
    String food_expiry;
    String food_type;
    String food_time_of_cook;
    String food_key;


    HashMap<String,Object> food_post_on ;



    public Food(String food_name, String food_desc, String food_avail_for, String food_time_of_cook, String food_expiry, String food_type, String food_img, String food_key,String food_post_on) {
        this.food_name = food_name;
        this.food_avail_for = food_avail_for;
        this.food_desc = food_desc;
        this.food_img = food_img;
        this.food_expiry = food_expiry;
        this.food_type = food_type;
        this.food_time_of_cook = food_time_of_cook;
        this.food_key = food_key;
        HashMap<String,Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.food_post_on = timestampNow;
    }
    public Food() {
    }


    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_avail_for() {
        return food_avail_for;
    }

    public void setFood_avail_for(String food_avail_for) {
        this.food_avail_for = food_avail_for;
    }

    public String getFood_desc() {
        return food_desc;
    }

    public void setFood_desc(String food_desc) {
        this.food_desc = food_desc;
    }

    public String getFood_img() {
        return food_img;
    }

    public void setFood_img(String food_img) {
        this.food_img = food_img;
    }

    public String getFood_expiry() {
        return food_expiry;
    }

    public void setFood_expiry(String food_expiry) {
        this.food_expiry = food_expiry;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getFood_time_of_cook() {
        return food_time_of_cook;
    }

    public void setFood_time_of_cook(String food_time_of_cook) {
        this.food_time_of_cook = food_time_of_cook;
    }

    public HashMap<String, Object> getFood_post_on() {
        return food_post_on;
    }

    public void setFood_post_on(HashMap<String, Object> food_post_on) {
        this.food_post_on = food_post_on;
    }


    public String getFood_key() {
        return food_key;
    }

    public void setFood_key(String food_key) {
        this.food_key = food_key;
    }

    @Exclude
    public long getFood_post_onLong(){
        return (long) food_post_on.get("timestamp");
    }


    @Override
    public String toString() {
        return food_name + " " + food_desc;
    }
}

