package com.kewldevs.sathish.faceless;

/**
 * Created by sathish on 10/22/16.
 */

public class UserProfile {

    String name,address,phno,lat,lng,img,email;

    public UserProfile() {
    }

    public UserProfile(String name, String address, String phno, String lat, String lng, String img , String email) {
        this.name = name;
        this.address = address;
        this.phno = phno;
        this.lat = lat;
        this.lng = lng;
        this.img = img;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
