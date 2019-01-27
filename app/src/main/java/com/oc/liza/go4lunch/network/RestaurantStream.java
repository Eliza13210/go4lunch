package com.oc.liza.go4lunch.network;

import com.oc.liza.go4lunch.models.Restaurants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RestaurantStream {

    public static Observable<Restaurants> fetchNearbyRestaurantsStream(String location) {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        return restaurantService.getRestaurant(RestaurantService.NEARBY, location, RestaurantService.RADIUS,RestaurantService.TYPE,
                RestaurantService.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);


    }

    public static Observable<Restaurants> fetchDetailsStream(String place_id) {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        return restaurantService.getRestaurant(RestaurantService.PLACE_ID, place_id, null,null,restaurantService.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
