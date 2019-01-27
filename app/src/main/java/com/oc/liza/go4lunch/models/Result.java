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

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("photos")
    @Expose
    private List<Photos> photos;


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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

}
