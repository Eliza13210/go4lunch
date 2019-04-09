package com.oc.liza.go4lunch.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ProfileActivityTest {

    private ProfileActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(ProfileActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testActivityExists() {
        assertNotNull(shadowOf(RuntimeEnvironment.application));
        assertNotNull(Robolectric.setupActivity(ProfileActivity.class));
    }

    @Test
    public void when_MenuItemSearchClicked_then_StartSearchActivity() {
        //User click on search in menu and starts new activity
       // ListView lvMenu = (ListView)activity.findViewById(R.id.activity_profile_nav_view);
        //Shadows.shadowOf(lvMenu).performItemClick(0); //click first item
        //Shadows.shadowOf(lvMenu).performItemClick(1); //click second item

    }

}