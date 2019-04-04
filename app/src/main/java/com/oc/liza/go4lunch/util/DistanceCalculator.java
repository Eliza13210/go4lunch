package com.oc.liza.go4lunch.util;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class DistanceCalculator {

    public DistanceCalculator() {
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in meters.
     */
    public double greatCircleInMeters(LatLng latLng1, LatLng latLng2) {
        double value;
        value = greatCircleInKilometers(latLng1.latitude, latLng1.longitude, latLng2.latitude,
                latLng2.longitude) * 1000;
        return value;

    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in kilometers.
     * https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps
     */
   public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;


        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }

    public double roundOneDecimal(double value) {
        int scale = (int) Math.pow(10, 1);

        //Round result to one decimal
        return (double) Math.round(value * scale) / scale;

    }

    public String calculateDistance(RestaurantDetails result, Context context) {
        Double lat = result.getGeometry().getLocation().getLat();
        Double lng = result.getGeometry().getLocation().getLng();
        LatLng latLng1 = new LatLng(lat, lng);

        //Get current location
        LocationManager locationManager = new LocationManager(context);
        LatLng latLng2 = locationManager.getCurrentLatLng();

        //Calculate distance in meter
        double distanceDouble = greatCircleInMeters(latLng1, latLng2);
        String distance;
        //If distance is more than 900 m, convert to km
        if (distanceDouble > 900) {
            distanceDouble = distanceDouble / 1000;
            distance = String.valueOf(roundOneDecimal(distanceDouble) + "km");
        } else {
            distance = String.valueOf(Math.round(distanceDouble)) + "m";
        }
        return distance;

    }

}
