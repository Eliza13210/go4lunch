package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Result {

    @SerializedName("place_id")
    @Expose
    private String place_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("photos")
    @Expose
    private List<Photos> photos;

    @SerializedName("opening_hours")
    @Expose
    private Result opening_hours;

    @SerializedName("open_now")
    @Expose
    private String open_now;

    @SerializedName("rating")
    @Expose
    private int rating;

    //To get location
    @SerializedName("geometry")
    @Expose
    private Result geometry;

    @SerializedName("location")
    @Expose
    private Result location;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Result getGeometry() {
        return geometry;
    }

    public void setGeometry(Result geometry) {
        this.geometry = geometry;
    }

    public Result getLocation() {
        return location;
    }

    public void setLocation(Result location) {
        this.location = location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public Result getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Result opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getOpen_now() {
        return open_now;
    }

    public void setOpen_now(String open_now) {
        this.open_now = open_now;
    }
}
