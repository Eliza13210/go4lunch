package com.oc.liza.go4lunch.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;

import com.oc.liza.go4lunch.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.robolectric.Shadows.shadowOf;

    @RunWith(RobolectricTestRunner.class)
    public class RestaurantActivityTest {

        private RestaurantActivity activity;

        final SharedPreferences sharedPrefs = Mockito.mock(SharedPreferences.class);
        final Context context = Mockito.mock(Context.class);

        @Before
        public void setUp() {

            Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);

            activity = Robolectric.buildActivity(RestaurantActivity.class)
                    .create()
                    .resume()
                    .get();
        }

        @Test
        public void testActivityExists() {
            assertNotNull(shadowOf(RuntimeEnvironment.application));
            assertNotNull(Robolectric.setupActivity(RestaurantActivity.class));


            BottomNavigationView lvMenu = (BottomNavigationView) activity.findViewById(R.id.navigation);
            Mockito.when(sharedPrefs.getString(anyString(), anyString())).thenReturn("url");

            lvMenu.findViewById(R.id.navigation_website).performClick();//click first item
            Intent expectedIntent = new Intent(activity, WebviewActivity.class);
            ShadowActivity shadowActivity = Shadows.shadowOf(activity);
            Intent actualIntent = shadowActivity.getNextStartedActivity();

            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
            assertTrue(actualIntent.filterEquals(expectedIntent));
        }

}