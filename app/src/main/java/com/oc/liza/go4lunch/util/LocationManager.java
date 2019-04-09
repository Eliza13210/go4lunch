package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.oc.liza.go4lunch.MainActivity;

import java.util.Objects;

public class LocationManager {

    private Context context;
    private SharedPreferences pref;

    //For user location
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted = false;

    public LocationManager(Context context) {
        this.context = context;
    }

    public void checkLocationPermission() {

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this).context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
            Log.e("Permission", "Granted");
        } else {
            ActivityCompat.requestPermissions((MainActivity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.e("Permission", "Request permission");
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!locationPermissionGranted) {
                Toast.makeText(context, "You need to grant permission to access your location", Toast.LENGTH_LONG).show();
            } else {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((MainActivity) context, new OnCompleteListener() {
                    Location mLastKnownLocation;

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            double mLatitude;
                            double mLongitude;

                            if (mLastKnownLocation != null) {
                                //Get the latitude and longitude
                                mLatitude = mLastKnownLocation.getLatitude();
                                mLongitude = mLastKnownLocation.getLongitude();
                            } else {
                                //Set default location
                                mLatitude = 43.7839551;
                                mLongitude = 4.8505099;
                                Log.e("location map", "Using default location");

                                Toast.makeText(context, "Error defining location, using default location", Toast.LENGTH_LONG).show();
                            }

                            //Save latitude and longitude
                            pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                            pref.edit().putString("CurrentLatitude", Double.toString(mLatitude)).apply();
                            pref.edit().putString("CurrentLongitude", Double.toString(mLongitude)).apply();
                        } else {
                            Log.e("map", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (
                SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public LatLng getCurrentLatLng() {
        //Get current location
        SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        double currentLat = Double.parseDouble(pref.getString("CurrentLatitude", "10"));
        double currentLng = Double.parseDouble(pref.getString("CurrentLongitude", "10"));

        return new LatLng(currentLat, currentLng);
    }
}
