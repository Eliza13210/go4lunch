package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class LocationManagerTest {
    final SharedPreferences sharedPrefs = Mockito.mock(SharedPreferences.class);
    final Context context = Mockito.mock(Context.class);

    @Before
    public void setUp() {
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
    }

    @Test
    public void getCurrentLatLng() {
        Mockito.when(sharedPrefs.getString(anyString(), anyString())).thenReturn("211");
        LocationManager manager = new LocationManager(context);
        LatLng latlng= manager.getCurrentLatLng();
        LatLng latLngExpected=new LatLng(211,211);
        assertEquals(latlng,latLngExpected);
    }
}