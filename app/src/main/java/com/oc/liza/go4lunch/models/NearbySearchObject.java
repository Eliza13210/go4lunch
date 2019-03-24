package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NearbySearchObject {

    @SerializedName("result")
    @Expose
    private RestaurantDetails details;

    @SerializedName("results")
    @Expose
    private List<NearbySearchObject> results;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("place_id")
    @Expose
    private String place_id;

    public RestaurantDetails getDetails() {
        return details;
    }

    public void setResult(RestaurantDetails result) {
        this.details = result;
    }

    public List<NearbySearchObject> getResults() {
        return results;
    }

    public void setResults(ArrayList<NearbySearchObject> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

}
