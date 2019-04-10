package com.oc.liza.go4lunch.controllers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.util.DrawerManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ProfileActivityTest {

    private ProfileActivity activity;

    @Before
    public void setUp() {
        FirebaseAuth authMock = mock(FirebaseAuth.class);
        FirebaseUser userMock = mock(FirebaseUser.class);
        FirebaseApp.initializeApp(RuntimeEnvironment.application);

        activity = Robolectric.buildActivity(ProfileActivity.class)
                .create()
                .resume()
                .get();


        when(FirebaseAuth.getInstance()).thenReturn(authMock);
        when(authMock.getCurrentUser()).thenReturn(userMock);
        when(userMock.getPhotoUrl().toString()).thenReturn("url");
        when(userMock.getDisplayName()).thenReturn("Spiderman");
        when(userMock.getEmail()).thenReturn("@");
    }


    @Test
    public void testActivityExists() {
        //assertNotNull(shadowOf(RuntimeEnvironment.application));
        //assertNotNull(Robolectric.setupActivity(ProfileActivity.class));
    }

    @Test
    public void when_MenuItemSearchClicked_then_StartSearchActivity() {
        //User click on search in menu and starts new activity
        // ListView lvMenu = (ListView)activity.findViewById(R.id.activity_profile_nav_view);
        //Shadows.shadowOf(lvMenu).performItemClick(0); //click first item
        //Shadows.shadowOf(lvMenu).performItemClick(1); //click second item

    }

}