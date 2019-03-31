package com.oc.liza.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.util.MapManager;
import com.oc.liza.go4lunch.util.RestaurantManager;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener{

    //For google maps
    private GoogleMap mMap;
    private MapManager manager;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //Show the google map
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        manager = new MapManager(getActivity(), mMap);
        //Show restaurants as markers on map
        displayRestaurantsOnMap();

        // Turn on the My Location layer and the related control on the map.
        manager.updateLocationUI(this);
    }

    private void displayRestaurantsOnMap() {
        RestaurantManager restaurantManager = new RestaurantManager(getContext());
        List<RestaurantDetails> listOfRestaurants = restaurantManager.getListOfRestaurants();
        //Use Restaurant Manager to display markers on map
        manager.showUser();
        manager.checkIfUser(listOfRestaurants);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // (the camera animates to the user's current position).
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manager.getUserLatLng(), 15));
          return false;
    }
}
