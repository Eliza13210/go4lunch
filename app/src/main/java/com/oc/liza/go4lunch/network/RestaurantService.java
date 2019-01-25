package com.oc.liza.go4lunch.network;

import com.oc.liza.go4lunch.models.Restaurants;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantService {
     String apiKey="AIzaSyAv1YigRBCspJCcYQwzY7rHtbrX1dpx868";
     String radius="1500";
     String type="restaurant";

    @GET("json")
    Observable<Restaurants> getRestaurant(
            @Query("location") String location,
            @Query("radius")String radius,
            @Query("type") String type,
            @Query("key") String key
            );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
