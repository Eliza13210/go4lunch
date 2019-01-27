package com.oc.liza.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photos {

    @SerializedName("photo_reference")
    @Expose
    private String photoRef;

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }
}
