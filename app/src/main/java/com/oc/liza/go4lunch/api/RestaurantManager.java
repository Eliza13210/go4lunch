package com.oc.liza.go4lunch.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    //For api request
    private RestaurantDetails restaurantDetails;
    private Disposable disposable;

    //This will be saved and showed in activity
    private String name = "";
    private String website = "";
    private String phone = "";
    private String address = "";
    private String imgUrl = "";

    private BitmapDescriptor colored_marker;
    private boolean userGoing;

    public RestaurantManager(Context context, List<Result> list) {
        this.context = context;
        this.list = list;
        Log.e("manager", "list length=" + list.size());
    }

    public void displayOnMap(GoogleMap map) {

        //display user
        SharedPreferences prefs = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        Double mLatitude = Double.valueOf(prefs.getString("CurrentLatitude", null));
        Double mLongitude = Double.valueOf(prefs.getString("CurrentLongitude", null));
        //Add user marker on map
        map.addMarker(new MarkerOptions()
                .position(new LatLng(mLatitude,
                        mLongitude))
                .title("User"))
                .setTag(100);

        for (int i = 0; i < list.size(); i++) {
            name = list.get(i).getName();
            checkIfUser(name);
            Double lat = list.get(i).getGeometry().getLocation().getLat();
            Double lng = list.get(i).getGeometry().getLocation().getLng();
            //Set color on marker depending if user has chosen this restaurant
            if (userGoing) {
                Log.e("manager", "user going true "+name);
                colored_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_green);
            } else {
                colored_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_orange);
            }
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat,
                    lng))
                    .title(name)
                    .icon(colored_marker));
            marker.setTag(i);
            Log.e("manager", "marker" + i);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                    lng), 15));
        }

        //User click on marker
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final int position = (int) (marker.getTag());
                if (position == 100) {
                    Log.e("manager", "user");
                } else {
                    fetchRestaurantDetails(position);
                }
                return false;
            }
        });
    }

    public void checkIfUser(final String name) {
        UserHelper.getUsersCollection()
                .whereEqualTo("restaurant", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.e("checkifuser", "rest" +name +task.getResult().size());
                            if (task.getResult().size()>0) {
                                userGoing = true;
                            } else {
                                userGoing = false;
                            }
                        } else {
                            Log.d("manager", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void fetchRestaurantDetails(final int position) {
        String place_id = list.get(position).getPlace_id();
        Log.e("manager", list.get(position).getName() + position);
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
        name = list.get(i).getName();
        phone = restaurantDetails.getPhone();
        address = restaurantDetails.getAddress();
        website = restaurantDetails.getWebsite();
        imgUrl = context.getString(R.string.photo_url)
                + list.get(i).getPhotos().get(0).getPhotoRef()
                + "&key="
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
