package com.kewldevs.sathish.faceless;

import java.io.Serializable;

/**
 * Created by sathish on 10/11/16.
 */
public class Feeds implements Serializable {
    public String userId;
    public GeoLoc location;

    public Feeds() {
    }

    public Feeds(String userId, GeoLoc geoLocation) {
        this.userId = userId;
        this.location = new GeoLoc(geoLocation.latitude,geoLocation.longitude);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return location.latitude;
    }

    public Double getLongitude() {
        return location.longitude;
    }


    public GeoLoc getLocation() {
        return location;
    }

    public void setLocation(GeoLoc location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Feeds{" +
                "userId='" + userId + '\'' +
                ", latitude=" + location.latitude +
                ", longitude=" + location.longitude +
                '}';
    }


}
