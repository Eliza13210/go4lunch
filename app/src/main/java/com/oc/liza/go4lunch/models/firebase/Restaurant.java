package com.oc.liza.go4lunch.models.firebase;

public class Restaurant {

    private String name;
    private int rating;
    private int personsGoing;

    public Restaurant(){}

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

    public int getPersonsGoing() {
        return personsGoing;
    }

    public void setPersonsGoing(int personsGoing) {
        this.personsGoing = personsGoing;
    }
}
