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
    private List<Result> results;

    @SerializedName("status")
    @Expose
    private String status;

    public RestaurantDetails getDetails() {
        return details;
    }

    public void setResult(RestaurantDetails result) {
        this.details = result;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
