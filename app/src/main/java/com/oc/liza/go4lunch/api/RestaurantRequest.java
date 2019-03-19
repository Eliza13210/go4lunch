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
    private Result restaurant;

    public RestaurantRequest(Context context) {
        this.context = context;
        results = new ArrayList<>();
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

                    }
                });
    }

    private void addToListOfRestaurants(NearbySearchObject nearbySearchObject) {

        //Add restaurants results from fetched nearby search object to the list
        results.addAll(nearbySearchObject.getResults());
        for (Result r : results) {
            fetchRestaurantDetails(r, r.getPlace_id());
        }

        startProfileActivity();
    }

    private void fetchRestaurantDetails(final Result result, String place_id) {

            this.disposable = RestaurantStream.fetchDetailsStream((place_id))
                    .subscribeWith(new DisposableObserver<NearbySearchObject>() {

                        @Override
                        public void onNext(NearbySearchObject nearbySearchObject) {
                            result.setDetails(nearbySearchObject.getDetails());
                            Log.e("ListofRest", "detail " + result.getDetails().getAddress());
                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e("Main", "Error fetching details " + e);
                        }

                        @Override
                        public void onComplete() {
                            //Save the list of restaurants
                            Gson gson = new Gson();
                            String json = gson.toJson(results);
                            pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                            pref.edit().putString("ListOfRestaurants", json).apply();

                            Log.e("Restaurant Request", "Number of restaurants " + results.size());
                        }
                    });


    }

    private void startProfileActivity() {
        disposeWhenDestroy();
        context.startActivity(new Intent(context, ProfileActivity.class));
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}
