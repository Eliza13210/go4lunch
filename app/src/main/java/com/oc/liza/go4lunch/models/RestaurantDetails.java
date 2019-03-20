package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetails {

    @SerializedName("international_phone_number")
    @Expose
    private String phone;

    @SerializedName("formatted_address")
    @Expose
    private String address;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("periods")
    @Expose
    List<OpeningHours> periods;

    @SerializedName("opening_hours")
    @Expose
    private RestaurantDetails opening_hours;

    @SerializedName("open_now")
    @Expose
    private boolean open_now;


    @SerializedName("types")
    @Expose
    List<String> types;

    @SerializedName("name")
    @Expose
    private String name;


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

    public boolean getOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }


    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
