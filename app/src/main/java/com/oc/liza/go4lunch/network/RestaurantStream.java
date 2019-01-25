package com.oc.liza.go4lunch.network;

import com.oc.liza.go4lunch.models.Restaurants;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RestaurantStream {

    public static io.reactivex.Observable<Restaurants> fetchNearbyRestaurantsStream(String location) {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        return restaurantService.getRestaurant(location, restaurantService.radius,restaurantService.type,restaurantService.apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);


    }
}
