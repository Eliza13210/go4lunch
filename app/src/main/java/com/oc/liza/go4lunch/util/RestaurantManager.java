package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import java.lang.reflect.Type;
import java.util.List;

public class RestaurantManager {

    //info from fragment or activity
    private Context context;
    private List<Result> listOfRestaurants;
    private List<RestaurantDetails> listOfDetails;
    private SharedPreferences pref;


    public RestaurantManager(Context context) {
        this.context = context;
        listOfRestaurants = getListOfRestaurants();
        listOfDetails = getListOfDetails();
    }


    public List<RestaurantDetails> getListOfDetails() {

        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfDetails", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<RestaurantDetails>>() {
        }.getType();

        listOfDetails = gson.fromJson(json, type);
        return listOfDetails;
    }


    public List<Result> getListOfRestaurants() {

        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfRestaurants", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        listOfRestaurants = gson.fromJson(json, type);
        return listOfRestaurants;
    }

    public void saveInfoToRestaurantActivity(String query) {
        //Fetch details about Restaurant
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            if (listOfRestaurants.get(i).getName().equals(query)) {
                //Fetch info about restaurant
                String name = listOfRestaurants.get(i).getName();
                Log.e("saving ", "save name " + name);
                String phone = listOfDetails.get(i).getPhone();
                String address = listOfDetails.get(i).getAddress();
                String website = listOfDetails.get(i).getWebsite();
                String imgUrl = context.getString(R.string.photo_url)
                        + listOfRestaurants.get(i).getPhotos().get(0).getPhotoRef()
                        + "&key="
                        + BuildConfig.API_KEY;

                //Save detailed info so it can be accessed from activity
                SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                pref.edit().putString("Name", name).putString("Phone", phone).putString("Website", website).putString("Img", imgUrl)
                        .putString("Address", address).apply();

                //Start restaurant Activity
                startRestaurantActivity();
            }
        }
    }

    private void startRestaurantActivity() {
        Intent restaurantActivity = new Intent(context, RestaurantActivity.class);
        context.startActivity(restaurantActivity);
    }

}