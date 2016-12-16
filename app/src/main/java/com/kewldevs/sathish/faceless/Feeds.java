package com.kewldevs.sathish.faceless;

import java.io.Serializable;

/**
 * Created by sathish on 10/11/16.
 */
public class Feeds implements Serializable {

    String key; //User ID

    String UserName; //User Name

    Feeds() {
    }

    public Feeds(String key, String userName) {
        this.key = key;
        UserName = userName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Override
    public String toString() {
        return "Feeds{" +
                "key='" + key + '\'' +
                ", UserName='" + UserName + '\'' +
                '}';
    }
}
