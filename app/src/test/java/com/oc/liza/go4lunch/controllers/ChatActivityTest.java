package com.oc.liza.go4lunch.controllers;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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
public class ChatActivityTest {

    Context context = Mockito.mock(Context.class);
        private ChatActivity activity;

        @Before
        public void setUp() {
            FirebaseApp.initializeApp(RuntimeEnvironment.application);
            activity = Robolectric.buildActivity(ChatActivity.class)
                    .create()
                    .resume()
                    .get();
        }

        @Test
        public void testActivityExists() {
            assertNotNull(shadowOf(RuntimeEnvironment.application));
            assertNotNull(Robolectric.setupActivity(ChatActivity.class));
        }

    @Test
    public void onClickAddFile() {

        activity.findViewById(R.id.activity_chat_add_file_button).performClick();

        Intent expectedIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }
}