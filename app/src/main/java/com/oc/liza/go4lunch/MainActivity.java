package com.oc.liza.go4lunch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.ProfileActivity;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    LinearLayout linearLayout;

    //For Firebase login
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //For user location
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //For saving
    private SharedPreferences pref;
    private Disposable mDisposable;
    private String location = "";
    private List<Result> results = new ArrayList<>();


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
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        pref = getSharedPreferences("Go4Lunch", MODE_PRIVATE);
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
                getCurrentLocation();

                Log.e("MainActivity", "success");
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

    private void getCurrentLocation() {
        try {
            if (mLocationPermissionGranted) {
                // Get the current location of the device if permission granted
                getDeviceLocation();
            } else {
                // Permission not granted
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            pref.edit().putBoolean("LocationGranted", mLocationPermissionGranted).apply();
            getDeviceLocation();
            Log.e("Permission", "granted ok");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.e("Permission", "request permission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    pref.edit().putBoolean("LocationGranted", mLocationPermissionGranted).apply();
                    Log.e("Perm granted", "updating ok");
                    getCurrentLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!mLocationPermissionGranted) {
                Toast.makeText(this, "You need to grant permission to access your location", Toast.LENGTH_LONG).show();
            } else {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    Location mLastKnownLocation;

                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            Double mLatitude;
                            Double mLongitude;

                            if (mLastKnownLocation != null) {
                                //Get the latitude and longitude
                                mLatitude = mLastKnownLocation.getLatitude();
                                mLongitude = mLastKnownLocation.getLongitude();
                            } else {
                                //Set default location
                                mLatitude = 43.7845096;
                                mLongitude = 4.846242;
                            }

                            // Set location to use when fetching nearby restaurants
                            location = Double.toString(mLatitude) + "," + Double.toString(mLongitude);
                            //Save latitude and longitude to calculate distance in list view
                            pref.edit().putString("CurrentLatitude", Double.toString(mLatitude)).apply();
                            pref.edit().putString("CurrentLongitude", Double.toString(mLongitude)).apply();

                            getRestaurants();

                            Log.e("location map", "success" + location);
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

    // Create user in firestore database
    private void createUserInFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserHelper.getUser(currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult() == null) {

                        //Get current user info
                        String urlPicture = (currentUser.getPhotoUrl() != null)
                                ? currentUser.getPhotoUrl().toString() : null;
                        String username = currentUser.getDisplayName();
                        String uid = currentUser.getUid();

                        // Access the Cloud Firestore instance from the Activity
                        UserHelper.createUser(uid, username, urlPicture, "not selected");
                        Log.e("created", "success creating new user");
                    }
                }
            });

        }
    }

    //Search for nearby restaurants and start Profile activity when complete
    private void getRestaurants() {
        this.mDisposable = RestaurantStream.fetchNearbyRestaurantsStream((location))
                .subscribeWith(new DisposableObserver<NearbySearchObject>() {
                    @Override
                    public void onNext(NearbySearchObject nearbySearchObject) {
                        addToList(nearbySearchObject);
                        Log.e("onNext", nearbySearchObject.getStatus());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Main", "Error fetching restaurants " + e);
                    }

                    @Override
                    public void onComplete() {
                        startProfileActivity();
                    }
                });
    }

    private void addToList(NearbySearchObject nearbySearchObject) {

        //Add restaurants results from fetched nearby search object to the list
        results.addAll(nearbySearchObject.getResults());
        //Save the list of restaurants
        Gson gson = new Gson();
        String json = gson.toJson(results);
        pref.edit().putString("ListOfRestaurants", json).apply();
    }

    private void startProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
}
