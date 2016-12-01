package com.kewldevs.sathish.faceless;

import java.io.Serializable;

/**
 * Created by sathish on 10/23/16.
 */

public class GeoLoc implements Serializable{
    double latitude , longitude;

    public GeoLoc(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoLoc() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
