package com.oc.liza.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ProgressBar;

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
    private List<NearbySearchObject> results;
    private List<RestaurantDetails> listOfRestaurants;
    private int progressStatus = 0;
    private ProgressBar progressBar;

    public RestaurantRequest(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
        results = new ArrayList<>();
        listOfRestaurants = new ArrayList<>();
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
        // - Update UI
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
        for (NearbySearchObject r : results) {
            fetchRestaurantDetails(r, r.getPlace_id());
        }
    }

    private void fetchRestaurantDetails(final NearbySearchObject result, final String place_id) {
        this.disposable = RestaurantStream.fetchDetailsStream((place_id))
                .subscribeWith(new DisposableObserver<NearbySearchObject>() {

                    @Override
                    public void onNext(NearbySearchObject nearbySearchObject) {
                        listOfRestaurants.add(nearbySearchObject.getDetails());
                        Log.e("ListofRest", "detail " + nearbySearchObject.getDetails().getAddress());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Main", "Error fetching details " + e);
                    }

                    @Override
                    public void onComplete() {
                        if (place_id.equals(results.get(results.size() - 1).getPlace_id())) {
                            //Save the list of restaurants
                            Gson gson = new Gson();
                            String json = gson.toJson(listOfRestaurants);
                            pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                            pref.edit().putString("ListOfRestaurants", json).apply();

                            //Save a back up of the list of restaurants
                            gson = new Gson();
                            json = gson.toJson(listOfRestaurants);
                            pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                            pref.edit().putString("ListOfRestaurantsBackUp", json).apply();

                            startProfileActivity();
                            Log.e("Restaurant Request", "Number of restaurants " + results.size());
                        }
                    }
                });


    }

    private void updateUIWhenStopingHTTPRequest() {
        progressStatus = 100;
    }

    private void startProfileActivity() {
        disposeWhenDestroy();
        context.startActivity(new Intent(context, ProfileActivity.class));
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}
