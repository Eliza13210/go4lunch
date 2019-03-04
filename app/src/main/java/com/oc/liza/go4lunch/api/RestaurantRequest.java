package com.oc.liza.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.oc.liza.go4lunch.controllers.ProfileActivity;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantStream;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantRequest {

    private Context context;
    //For saving
    private SharedPreferences pref;
    private Disposable disposable;
    private StringBuilder builder;
    private List<Result> results;
    private List<RestaurantDetails> listOfDetails;


    public RestaurantRequest(Context context) {
        this.context = context;
        results = new ArrayList<>();
        listOfDetails = new ArrayList<>();
        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
    }

    private void setLocationString() {
        //Build location string to fetch nearby restaurants
        builder = new StringBuilder();
        builder.append(pref.getString("CurrentLatitude", null));
        builder.append(",");
        builder.append(pref.getString("CurrentLongitude", null));
    }

    //Search for nearby restaurants
    public void getRestaurants() {
        setLocationString();
        disposable = RestaurantStream.fetchNearbyRestaurantsStream((builder.toString()))
                .subscribeWith(new DisposableObserver<NearbySearchObject>() {
                    @Override
                    public void onNext(NearbySearchObject nearbySearchObject) {
                        addToListOfRestaurants(nearbySearchObject);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Main", "Error fetching restaurants " + e);
                    }

                    @Override
                    public void onComplete() {
                        fetchRestaurantDetails();
                    }
                });
    }

    private void addToListOfRestaurants(NearbySearchObject nearbySearchObject) {

        //Add restaurants results from fetched nearby search object to the list
        results.addAll(nearbySearchObject.getResults());
        //Save the list of restaurants
        Gson gson = new Gson();
        String json = gson.toJson(results);
        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        pref.edit().putString("ListOfRestaurants", json).apply();

        Log.e("Restaurant Request", "Number of restaurants " + results.size());
    }

    private void fetchRestaurantDetails() {
        for (Result r : results) {
            String place_id = r.getPlace_id();

            this.disposable = RestaurantStream.fetchDetailsStream((place_id))
                    .subscribeWith(new DisposableObserver<NearbySearchObject>() {

                        @Override
                        public void onNext(NearbySearchObject nearbySearchObject) {
                            listOfDetails.add(nearbySearchObject.getDetails());
                            if (listOfDetails.size() == results.size()) {
                                saveListDetails();
                                startProfileActivity();
                            }
                            Log.e("onnext", "size " + listOfDetails.size());
                            Log.e("ListofRest", "size" + results.size());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }


    private void saveListDetails() {
        //Save the list of restaurants
        Gson gson = new Gson();
        String json = gson.toJson(listOfDetails);
        pref.edit().putString("ListOfDetails", json).apply();
    }

    private void startProfileActivity() {
        disposeWhenDestroy();
        context.startActivity(new Intent(context, ProfileActivity.class));
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}
