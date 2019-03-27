package com.oc.liza.go4lunch.models.firebase;

import java.util.Date;

public class Message {
    private User userSender;
    private String message;
    private Date dateCreated;
    private String urlImage;

    public Message() {
    }

    public Message(String message, String urlImage, User userSender, Date dateCreated) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;
        this.dateCreated = dateCreated;
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }


}
