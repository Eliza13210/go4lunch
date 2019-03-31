package com.oc.liza.go4lunch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.liza.go4lunch.api.RestaurantRequest;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.util.LocationManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    LinearLayout linearLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private static final String CHANNEL_ID = "NOTIFICATION CHANNEL";

    //For Firebase login
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //For user location
    private LocationManager locationManager;

    @Override
    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null)
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        createNotificationChannel();
        startSignInActivity();
    }

    private void startSignInActivity() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MainTheme)
                        .build(),
                RC_SIGN_IN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Show Snack Bar with a message
    private void showSnackBar(LinearLayout linearLayout, String message) {
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG).show();
    }

    //Result when started sign in with Google or Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = mAuth.getCurrentUser();
                getUserInfo();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.linearLayout, getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.linearLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.linearLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void getUserInfo() {
        //Show progressbar
        progressBar.setVisibility(View.VISIBLE);

        //Create user in firestore if not already registered
        createUserInFirestore();

        //Get user location
        locationManager = new LocationManager(this);
        locationManager.checkLocationPermission();

        //Get nearby restaurants and launch Profile Activity
        RestaurantRequest restaurantRequest = new RestaurantRequest(this);
        restaurantRequest.getRestaurants();
    }

    // Create user in Firestore database
    private void createUserInFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserHelper.getUser(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //Check if user exists in database
                    if (task.getResult() == null) {
                        //If not - get current user info
                        String urlPicture = (currentUser.getPhotoUrl() != null)
                                ? currentUser.getPhotoUrl().toString() : null;
                        String username = currentUser.getDisplayName();
                        String uid = currentUser.getUid();

                        // Access the Cloud Firestore instance from the Activity
                        UserHelper.createUser(uid, username, urlPicture);
                        Log.e("MainActivity", "Success creating new user in Firestore");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("main", "failure firebase "+ e); }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocationManager.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("MainActivity", "Permission Granted");
                } else {
                    Toast.makeText(this, "You need to grant permission to access your location", Toast.LENGTH_LONG).show();
                }
                locationManager.checkLocationPermission();
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        //Save the Channel Id in shared preferences
        SharedPreferences preferences = getSharedPreferences("MYNEWS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("CHANNEL_KEY", CHANNEL_ID).apply();

    }
}
