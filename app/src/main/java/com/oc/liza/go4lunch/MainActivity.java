package com.oc.liza.go4lunch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.util.LocationManager;
import com.oc.liza.go4lunch.util.RestaurantRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    LinearLayout linearLayout;

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
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
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
                createUserInFirestore();
                //Get user location
                locationManager = new LocationManager(this);
                locationManager.checkLocationPermission();
                //Get nearby restaurants and launch Profile Activity
                RestaurantRequest restaurantRequest = new RestaurantRequest(this);
                restaurantRequest.getRestaurants();
                // getCurrentLocation();

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
                        UserHelper.createUser(uid, username, urlPicture, "not selected");
                        Log.e("MainActivity", "Success creating new user in Firestore");
                    }
                }
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
}
