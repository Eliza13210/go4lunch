package com.oc.liza.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Restaurants;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantService;
import com.oc.liza.go4lunch.network.RestaurantStream;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantManager {

    private Context context;
    private List<Result> list;
    private GoogleMap map;
    private RestaurantDetails restaurantDetails;
    private Disposable disposable;

    //This will be saved and showed in activity
    private String name = "";
    private String website = "";
    private String phone = "";
    private String address = "";
    private String imgUrl = "";

    public RestaurantManager(Context context, List<Result> list, GoogleMap map) {
        this.context = context;
        this.list = list;
        this.map = map;
        Log.e("manager", "create");
    }

    public void displayOnMap() {
        for (int i = 0; i < list.size(); i++) {
            name = list.get(i).getName();
            Double lat = list.get(i).getGeometry().getLat();
            Double lng = list.get(i).getGeometry().getLng();

            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat,
                    lng))
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            marker.setTag(i);

            Log.e("Manager", "display");
        }


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            private Disposable mDisposable;

            @Override
            public boolean onMarkerClick(Marker marker) {
                final int position = (int) (marker.getTag());
                fetchRestaurantDetails(position);
                startRestaurantActivity();
                return false;
            }
        });
    }

    private void fetchRestaurantDetails(final int position) {
        String place_id = list.get(position).getPlace_id();

        this.disposable = RestaurantStream.fetchDetailsStream(place_id).subscribeWith(new DisposableObserver<Restaurants>() {
            @Override
            public void onNext(Restaurants restaurants) {
                restaurantDetails = restaurants.getDetails();
                saveInfo(position);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void saveInfo(int i) {
        //Fetch details about restaurant
        phone = restaurantDetails.getPhone();
        address = restaurantDetails.getAddress();
        website = restaurantDetails.getWebsite();
        imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + list.get(i).getPhotos().get(0).getPhotoRef()
                + RestaurantService.API_KEY;

        //Save detailed info so it can be accessed from activity
        SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        pref.edit().putString("Name", name).putString("Phone", phone).putString("Website", website).putString("Img", imgUrl)
                .putString("Address", address).apply();
    }

    private void startRestaurantActivity() {
        disposeWhenDestroy();
        //Using position get Value from arraylist
        Intent restaurantActivity = new Intent(context, RestaurantActivity.class);
        context.startActivity(restaurantActivity);

    }
    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}
