package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {


    @SerializedName("close")
    @Expose
    private OpeningHours close;

    @SerializedName("open")
    @Expose
    private OpeningHours open;

    @SerializedName("day")
    @Expose
    private int day;

    @SerializedName("time")
    @Expose
    private String time;

    public OpeningHours getClose() {
        return close;
    }

    public void setClose(OpeningHours close) {
        this.close = close;
    }

    public OpeningHours getOpen() {
        return open;
    }

    public void setOpen(OpeningHours open) {
        this.open = open;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
