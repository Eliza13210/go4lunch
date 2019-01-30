package com.oc.liza.go4lunch.network;

import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.models.Restaurants;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RestaurantService {
     String API_KEY=BuildConfig.API_KEY;
     String RADIUS="1500";
     String TYPE="Restaurant";
     String NEARBY="nearbysearch";
     String PLACE_ID="placeid";

    @GET("{search}/json")
    Observable<Restaurants> getRestaurant(
            @Path("search") String search,
            @Query("location") String location,
            @Query("radius")String radius,
            @Query("type") String type,
            @Query("key") String key
            );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}