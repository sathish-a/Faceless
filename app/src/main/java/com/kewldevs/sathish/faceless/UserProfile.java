package com.kewldevs.sathish.faceless;

/**
 * Created by sathish on 10/22/16.
 */

public class UserProfile {

    String name, address, phno, img, email;

    public UserProfile() {
    }

    public UserProfile(String name, String address, String phno, String img, String email) {
        this.name = name;
        this.address = address;
        this.phno = phno;
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
