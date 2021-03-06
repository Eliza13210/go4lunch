package com.oc.liza.go4lunch.models.firebase;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class User {

    private String uid;
    private String restaurant;
    private String username;
    private String place_id;
    private ArrayList<String> like;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String restaurant) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurant = restaurant;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getLike() {
        return like;
    }


    public String getUrlPicture() {
        return urlPicture;
    }

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setLike(ArrayList<String> like) {
        this.like = like;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
