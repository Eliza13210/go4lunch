package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import java.util.List;
import java.util.Objects;

public class MapManager {

    private Context context;
    private RestaurantManager restaurantManager;
    private GoogleMap map;

    private boolean userGoing;
    private List<RestaurantDetails> listOfRestaurants;

    public MapManager(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        restaurantManager = new RestaurantManager(context);
    }

    //Set my location button listener and show user location
    public void updateLocationUI(MapFragment fragment) {
        if (map == null) {
            return;
        }
        try {   //Check permission to show user location
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(context),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //If ok, initialize map to show user location and buttons to zoom user
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.setOnMyLocationButtonClickListener(fragment);
            } else {
                //If no permission, do not show user location, only map
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                Log.e("Update Map", "permission not yet granted");
            }
            //Set map type and zoom
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setZoomControlsEnabled(true);

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public LatLng getUserLatLng() {
        SharedPreferences prefs = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        double mLatitude = Double.parseDouble(Objects.requireNonNull(prefs.getString("CurrentLatitude", null)));
        double mLongitude = Double.parseDouble(Objects.requireNonNull(prefs.getString("CurrentLongitude", null)));
        return new LatLng(mLatitude,
                mLongitude);
    }

    public void showUser() {
        //Add user marker on map
        map.addMarker(new MarkerOptions()
                .position(getUserLatLng())
                .title("User"))
                .setTag(100);

        //User click on marker will start function that fetch details about restaurant and start new activity
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final int i = (int) (marker.getTag());
                Log.e("click", "get tag " + i);
                if (i == 100) {
                    Log.e("Manager", "Clicked on user");
                } else {
                    restaurantManager.saveInfoToRestaurantActivity(listOfRestaurants.get(i).getPlace_id());
                    restaurantManager.startRestaurantActivity();
                }
                return false;
            }
        });
        //Move camera to show user location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getUserLatLng(), 15));
    }

    /**
     * This function will check if user is going to the restaurant and add a marker to the map
     * The marker will be green if a user is going
     * or orange if not
     */

    public void showRestaurantsOnMap(List<RestaurantDetails> list) {
        map.clear();
        showUser();
        listOfRestaurants = restaurantManager.getListOfRestaurants();
        this.listOfRestaurants = list;
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            final int finalI = i;
            //Fetch information from Firestore; user going to the restaurant
            UserHelper.getUsersCollection()
                    .whereEqualTo("place_id", listOfRestaurants.get(i).getPlace_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //If there is at least one person going, the marker will be green
                                if (Objects.requireNonNull(task.getResult()).size() > 0) {
                                    userGoing = true;
                                    //create marker and add it to the map
                                    displayOnMap(listOfRestaurants.get(finalI), finalI);
                                } else {
                                    userGoing = false;
                                    //create marker and add it to the map
                                    displayOnMap(listOfRestaurants.get(finalI), finalI);
                                }
                            } else {
                                Log.d("manager", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    //Show restaurant object as a marker on map
    private void displayOnMap(RestaurantDetails result, int tag) {

        String name = result.getName();

        Double lat = result.getGeometry().getLocation().getLat();
        Double lng = result.getGeometry().getLocation().getLng();

        //Set color on marker depending if user has chosen this restaurant
        BitmapDescriptor colored_marker;
        if (userGoing) {
            Log.e("manager", "user going true " + name);
            colored_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_green);
        } else {
            colored_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_orange);
        }

        //Add marker to map
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat,
                lng))
                .title(name)
                .icon(colored_marker));
        marker.setTag(tag);
    }
}
