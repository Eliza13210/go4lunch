package com.oc.liza.go4lunch.util;

import android.content.Context;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapManager {

    private Context context;
    private RestaurantManager restaurantManager;

    private boolean userGoing;
    private List<Marker> listMarkers = new ArrayList<>();
    private List<RestaurantDetails> listOfRestaurants;

    public MapManager(Context context) {
        this.context = context;
        restaurantManager = new RestaurantManager(context);
    }

    public void showUser(GoogleMap map) {
        //Get latitude and longitude
        SharedPreferences prefs = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        Double mLatitude = Double.valueOf(Objects.requireNonNull(prefs.getString("CurrentLatitude", null)));
        Double mLongitude = Double.valueOf(Objects.requireNonNull(prefs.getString("CurrentLongitude", null)));
        //Add user marker on map
        map.addMarker(new MarkerOptions()
                .position(new LatLng(mLatitude,
                        mLongitude))
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
                    restaurantManager.saveInfoToRestaurantActivity(marker.getTitle());
                    restaurantManager.startRestaurantActivity();
                }
                return false;
            }
        });
    }

    /**
     * This function will check if user is going to the restaurant and add a marker to the map
     * The marker will be green if a user is going
     * or orange if not
     */

    public void checkIfUser(final GoogleMap map, List<RestaurantDetails> list) {
        map.clear();
        showUser(map);
        listOfRestaurants = restaurantManager.getListOfRestaurants();
        this.listOfRestaurants = list;
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            final int finalI = i;
            //Fetch information from Firestore; user going to the restaurant
            UserHelper.getUsersCollection()
                    .whereEqualTo("restaurant", listOfRestaurants.get(i).getName())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //If there is at least one person going, the marker will be green
                                if (Objects.requireNonNull(task.getResult()).size() > 0) {
                                    userGoing = true;
                                    //create marker and add it to the map
                                    displayOnMap(listOfRestaurants.get(finalI), finalI, map);
                                } else {
                                    userGoing = false;
                                    //create marker and add it to the map
                                    displayOnMap(listOfRestaurants.get(finalI), finalI, map);
                                }
                            } else {
                                Log.d("manager", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    //Show restaurant object as a marker on map
    private void displayOnMap(RestaurantDetails result, int tag, GoogleMap map) {

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
        listMarkers.add(marker);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                lng), 15));
    }
}
