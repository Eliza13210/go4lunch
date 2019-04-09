package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.RestaurantRequest;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestaurantManager {

    //info from fragment or activity
    private Context context;
    private List<RestaurantDetails> listOfRestaurants;
    private SharedPreferences pref;
    private RestaurantRequest request;


    public RestaurantManager(Context context) {
        this.context = context;
        listOfRestaurants = getListOfRestaurants();
    }

    public List<RestaurantDetails> getListOfRestaurants() {
        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfRestaurants", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<RestaurantDetails>>() {
        }.getType();

        listOfRestaurants = gson.fromJson(json, type);
        return listOfRestaurants;
    }

    public String getRestaurantAddress(String place_id) {
        String address = "";
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            if (listOfRestaurants.get(i).getPlace_id().equals(place_id)) {
                //Fetch info about restaurant
                address = listOfRestaurants.get(i).getAddress();
            }
        }
        return address;
    }

    public void saveInfoToRestaurantActivity(String query) {
        boolean restaurantIsInList = false;
        //Fetch details about Restaurant
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            if (listOfRestaurants.get(i).getPlace_id().equals(query)) {
                restaurantIsInList = true;
                //Fetch info about restaurant
                String place_id = listOfRestaurants.get(i).getPlace_id();
                String name = listOfRestaurants.get(i).getName();
                String rating = String.valueOf(listOfRestaurants.get(i).getRating());
                String phone = listOfRestaurants.get(i).getPhone();
                String address = listOfRestaurants.get(i).getAddress();
                String website = listOfRestaurants.get(i).getWebsite();
                String imgUrl = context.getString(R.string.photo_url)
                        + listOfRestaurants.get(i).getPhotos().get(0).getPhotoRef()
                        + "&key="
                        + BuildConfig.API_KEY;

                //Save detailed info so it can be accessed from activity
                SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                pref.edit().putString("Place_id", place_id).putString("Name", name).putString("Rating", rating).putString("Phone", phone).putString("Website", website).putString("Img", imgUrl)
                        .putString("Address", address).apply();
            }
        }
        if (!restaurantIsInList) {
            request = new RestaurantRequest(context);
            request.fetchDetailsForRestaurantActivity(query);
        }
    }

    public void startRestaurantActivity() throws InterruptedException {
        //Check if restaurant request is used and in that case if API request finished before starting activity
        if (request != null) {
            TimeUnit.SECONDS.sleep(2);
            Log.e("Restaurant Manager", "Request, sleep 2 s");
        }
        Intent restaurantActivity = new Intent(context, RestaurantActivity.class);
        context.startActivity(restaurantActivity);
    }

    void updateListAfterSearch(List<RestaurantDetails> listSearch) {

        //Save the updated list of restaurants
        Gson gson = new Gson();
        String json = gson.toJson(listSearch);
        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        pref.edit().putString("ListOfRestaurants", json).apply();
    }

    void resetFullListOfRestaurants() {

        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfRestaurantsBackUp", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<RestaurantDetails>>() {
        }.getType();
        listOfRestaurants = gson.fromJson(json, type);
        //Save the updated list of restaurants
        gson = new Gson();
        json = gson.toJson(listOfRestaurants);
        pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        pref.edit().putString("ListOfRestaurants", json).apply();
        Log.e("Restaurant Search", "Number of restaurants " + listOfRestaurants.size());
    }

}
