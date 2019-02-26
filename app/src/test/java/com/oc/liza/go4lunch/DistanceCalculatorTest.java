package com.oc.liza.go4lunch;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceCalculatorTest {

    DistanceCalculator calculator=new DistanceCalculator();

    @Test
    public void greatCircleInMeters() {
        LatLng latLng1=new LatLng(43.7839551,4.8505099); //Chemin de bigau
        LatLng latLng2=new LatLng(43.788811, 4.832314);
        int result=(int) (calculator.greatCircleInMeters(latLng1, latLng2));

        assertEquals(1557,result);
    }

    @Test
    public void greatCircleInKilometers() {
        LatLng latLng1=new LatLng(43.7839551,4.8505099); //Chemin de bigau
        LatLng latLng2=new LatLng(43.788811, 4.832314);
        double value=calculator.greatCircleInKilometers(latLng1.latitude, latLng1.longitude, latLng2.latitude,
                latLng2.longitude);
        double rounded=calculator.roundOneDecimale(value);
        String result=String.valueOf(rounded);
        assertEquals("1.6",result);
    }
}