package com.oc.liza.go4lunch.controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity {

    @BindView(R.id.search_result)
    TextView search_result;

    private SharedPreferences pref;
    private List<Result> listRestaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getListOfRestaurants();
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            getListOfRestaurants();
        }
    }

    private void getListOfRestaurants() {
        pref = this.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);

       String query= pref.getString("Search", null);

        String json = pref.getString("ListOfRestaurants", null);
        Log.e("listF", json);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        listRestaurants = gson.fromJson(json, type);
        searchInList(query);

    }

    private void searchInList(String query) {
        for (int i = 0; i < listRestaurants.size(); i++) {
            if (listRestaurants.get(i).getName().equals(query)) {
                getRestaurantInfo(i);
            } else {
                search_result.setText(R.string.no_result);
            }
        }
    }

    private void startRestaurantActivity() {
        //Start restaurant activity
        Intent restaurant = new Intent(SearchResultsActivity.this, RestaurantActivity.class);
        startActivity(restaurant);
    }

    private void getRestaurantInfo(int number_in_list) {
        SharedPreferences.Editor editor = pref.edit();
        int i = number_in_list;
        //Fetch restaurant information and save in shared pref
        String jsonDetails = pref.getString("ListOfDetails", null);

        Gson gsonDetails = new Gson();
        Type typeDetails = new TypeToken<List<RestaurantDetails>>() {
        }.getType();
        List<RestaurantDetails> listDetails;

        listDetails = gsonDetails.fromJson(jsonDetails, typeDetails);

        String imgUrl = this.getString(R.string.photo_url)
                + listRestaurants.get(i).getPhotos().get(0).getPhotoRef()
                + "&key="
                + BuildConfig.API_KEY;
        editor.putString("Name", listRestaurants.get(i).getName())
                .putString("Img", imgUrl)
                .putString("Address", listDetails.get(i).getAddress())
                .putString("Phone", listDetails.get(i).getPhone())
                .putString("Website", listDetails.get(i).getWebsite())
                .putString("Rating", String.valueOf(listRestaurants.get(i).getRating()));
        editor.apply();

        startRestaurantActivity();
    }


}


