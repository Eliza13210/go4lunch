package com.oc.liza.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.RestaurantManager;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantStream;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.content.Context.MODE_PRIVATE;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;

    //For google maps
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(-33.8670522, 151.1957362);
    private Double mLatitude;
    private Double mLongitude;

    private boolean mLocationPermissionGranted;

    //For API request
    private Disposable mDisposable;
    private List<Result> results;

    private SharedPreferences prefs;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GeoDataClient.
        GeoDataClient geoDataClient = Places.getGeoDataClient(Objects.requireNonNull(getActivity()));

        // Construct a PlaceDetectionClient.
        PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(getActivity());

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        prefs = getActivity().getSharedPreferences("Go4Lunch", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return rootView;
    }

    /**
     * private void getCurrentLocation() {
     * try {
     * if (mLocationPermissionGranted) {
     * // Get the current location of the device and set the position of the map.
     * getDeviceLocation();
     * } else {
     * getLocationPermission();
     * }
     * } catch (SecurityException e) {
     * Log.e("Exception: %s", e.getMessage());
     * }
     * }
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;
        displayRestaurantsOnMap();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
    }

    private void updateLocationUI() {
        mLocationPermissionGranted = prefs.getBoolean("LocationGranted", false);
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnMyLocationClickListener(this);
                Log.e("Update", "permission ok");

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                Log.e("Update", "permission not yet granted");
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

/**
 private void getLocationPermission() {

 if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
 android.Manifest.permission.ACCESS_FINE_LOCATION)
 == PackageManager.PERMISSION_GRANTED) {
 mLocationPermissionGranted = true;
 // updateLocationUI();
 getDeviceLocation();
 Log.e("Permission", "granted ok");
 } else {
 ActivityCompat.requestPermissions(getActivity(),
 new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
 PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
 Log.e("Permission", "request permission");
 }
 }

 @Override public void onRequestPermissionsResult(int requestCode,
 @NonNull String permissions[],
 @NonNull int[] grantResults) {
 mLocationPermissionGranted = false;
 switch (requestCode) {
 case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
 // If request is cancelled, the result arrays are empty.
 if (grantResults.length > 0
 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
 mLocationPermissionGranted = true;
 }
 }
 }
 updateLocationUI();
 Log.e("Perm granted", "updating ok");
 }

 private void getDeviceLocation() {
 /*
  * Get the best and most recent location of the device, which may be null in rare
  * cases when a location is not available.
 */
    /**
     * try {
     * if (!mLocationPermissionGranted) {
     * Toast.makeText(getActivity(), "You need to grant permission to access your location", Toast.LENGTH_LONG).show();
     * } else {
     * Task locationResult = mFusedLocationProviderClient.getLastLocation();
     * locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
     * Location mLastKnownLocation;
     *
     * @Override public void onComplete(@NonNull Task task) {
     * if (task.isSuccessful()) {
     * // Set the map's camera position to the current location of the device.
     * mLastKnownLocation = (Location) task.getResult();
     * assert mLastKnownLocation != null;
     * <p>
     * //Get the latitude and longitude
     * mLatitude = mLastKnownLocation.getLatitude();//-33.8670522;
     * mLongitude = mLastKnownLocation.getLongitude();//151.1957362;
     * <p>
     * //Save latitude and longitude to calculate distance in list view
     * prefsEditor.putString("CurrentLatitude", Double.toString(mLatitude)).apply();
     * prefsEditor.putString("CurrentLongitude", Double.toString(mLongitude)).apply();
     * <p>
     * String location = Double.toString(mLatitude) + "," + Double.toString(mLongitude);
     * getRestaurants(location);
     * <p>
     * Log.e("location map", "success" + location);
     * <p>
     * } else {
     * Log.d("map", "Current location is null. Using defaults.");
     * Log.e("map", "Exception: %s", task.getException());
     * //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15));
     * // mMap.getUiSettings().setMyLocationButtonEnabled(false);
     * }
     * }
     * });
     * }
     * <p>
     * } catch (
     * SecurityException e)
     * <p>
     * {
     * Log.e("Exception: %s", e.getMessage());
     * }
     * }


    private void getRestaurants() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Go4Lunch", MODE_PRIVATE);
        String location = prefs.getString("CurrentLocation", null);
        this.mDisposable = RestaurantStream.fetchNearbyRestaurantsStream((location))
                .subscribeWith(new DisposableObserver<NearbySearchObject>() {
                    @Override
                    public void onNext(NearbySearchObject nearbySearchObject) {
                        addToList(nearbySearchObject);
                        Log.e("onNext", nearbySearchObject.getStatus());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void addToList(NearbySearchObject nearbySearchObject) {
       /** results = new ArrayList<>();

        //Add restaurants results from fetched nearby search object to the list
        if (nearbySearchObject.getStatus().equals("OK")) {
            results.addAll(nearbySearchObject.getResults());

            //Save the list of restaurants
            Gson gson = new Gson();
            String json = gson.toJson(results);
            prefs.edit().putString("ListOfRestaurants", json);
            prefs.edit().apply();

          //create map
            final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

        } else {
            Toast.makeText(getActivity(), "No results", Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }


    private void displayRestaurantsOnMap() {
        mLatitude = Double.valueOf(prefs.getString("CurrentLatitude", null));
        mLongitude=Double.valueOf(prefs.getString("CurrentLongitude", null));
        //Add user marker on map
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLatitude,
                mLongitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_phone))
                .title("User"))
                .setTag(100);

        //Move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLatitude,
                        mLongitude), 15));

        //Add restaurant markers on map
        SharedPreferences pref = getActivity().getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfRestaurants", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        results = gson.fromJson(json, type);

        RestaurantManager manager = new RestaurantManager(getActivity(), results);
        manager.displayOnMap(this.mMap);
    }

}
