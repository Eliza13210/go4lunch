package com.oc.liza.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.RestaurantDetails;
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

    public RestaurantManager(Context context, List<Result> list) {
        this.context = context;
        this.list = list;

        Log.e("manager", "create");
    }

    public void displayOnMap(GoogleMap map) {
        for (int i = 0; i < list.size(); i++) {
            name = list.get(i).getName();
            Double lat = list.get(i).getGeometry().getLocation().getLat();
            Double lng = list.get(i).getGeometry().getLocation().getLng();
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat,
                    lng))
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            marker.setTag(i);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                    lng), 10));


            //CHECK WITH LIST OF USERS AND CHANGE COLOR OF MARKER
            Log.e("Manager", "display");
        }

        //User click on marker
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                final int position = (int) (marker.getTag());
                fetchRestaurantDetails(position);
                return false;
            }
        });
    }

    private void fetchRestaurantDetails(final int position) {
        String place_id = list.get(position).getPlace_id();

        this.disposable = RestaurantStream.fetchDetailsStream(place_id).subscribeWith(new DisposableObserver<NearbySearchObject>() {
            @Override
            public void onNext(NearbySearchObject nearbySearchObject) {
                restaurantDetails = nearbySearchObject.getDetails();
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
        //Fetch details about Restaurant
        phone = restaurantDetails.getPhone();
        address = restaurantDetails.getAddress();
        website = restaurantDetails.getWebsite();
        imgUrl = context.getString(R.string.photo_url)
                + list.get(i).getPhotos().get(0).getPhotoRef()
                +"&key="
                + BuildConfig.API_KEY;

        //Save detailed info so it can be accessed from activity
        SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        pref.edit().putString("Name", name).putString("Phone", phone).putString("Website", website).putString("Img", imgUrl)
                .putString("Address", address).apply();

        startRestaurantActivity();
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
