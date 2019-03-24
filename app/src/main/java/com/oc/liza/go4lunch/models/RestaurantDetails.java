package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetails {


    @SerializedName("types")
    @Expose
    List<String> types;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("international_phone_number")
    @Expose
    private String phone;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("photos")
    @Expose
    private List<Photos> photos;

    @SerializedName("rating")
    @Expose
    private double rating;

    //Opening days and hours

    @SerializedName("periods")
    @Expose
    List<OpeningHours> periods;

    @SerializedName("opening_hours")
    @Expose
    private RestaurantDetails opening_hours;

    @SerializedName("open_now")
    @Expose
    private boolean open_now;

    //To get location
    @SerializedName("geometry")
    @Expose
    private RestaurantDetails geometry;

    @SerializedName("location")
    @Expose
    private RestaurantDetails location;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<OpeningHours> getPeriods() {
        return periods;
    }

    public void setPeriods(List<OpeningHours> periods) {
        this.periods = periods;
    }

    public RestaurantDetails getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(RestaurantDetails opening_hours) {
        this.opening_hours = opening_hours;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public RestaurantDetails getGeometry() {
        return geometry;
    }

    public void setGeometry(RestaurantDetails geometry) {
        this.geometry = geometry;
    }

    public RestaurantDetails getLocation() {
        return location;
    }

    public void setLocation(RestaurantDetails location) {
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
}
