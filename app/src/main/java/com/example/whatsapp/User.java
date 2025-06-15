package com.example.whatsapp;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String phone;
    private String profileUrl;

    public User(String uid, String name, String profileUrl) {
        this.uid = uid;
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public User() {
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

}
