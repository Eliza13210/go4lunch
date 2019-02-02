package com.oc.liza.go4lunch;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.ProfileActivity;
import com.oc.liza.go4lunch.models.firebase.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    LinearLayout linearLayout;

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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
                        .setTheme(R.style.AppTheme_NoTitle)
                        .build(),
                RC_SIGN_IN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Show Snack Bar with a message
    private void showSnackBar(LinearLayout linearLayout, String message) {
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = mAuth.getCurrentUser();
                Log.e("MainActivity", "success");
                checkIfUserExists();
                startProfileActivity();

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

    private void checkIfUserExists(){
        UserHelper.getUser(currentUser.getUid()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                createUserInFirestore();
            }
        });

    }

    private void startProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    // 1 - Http request that create user in firestore
    private void createUserInFirestore() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
            //Get current user info
            String urlPicture = (currentUser.getPhotoUrl() != null)
                    ? currentUser.getPhotoUrl().toString() : null;
            String username = currentUser.getDisplayName();
            String uid = currentUser.getUid();

            Log.e("createMain", uid);
            // Access a Cloud Firestore instance from your Activity
            UserHelper.createUser(uid, username, urlPicture, "not selected");
            Log.e("created", "success creating new user");

        }
    }


    private void createFakeUserDatabase() {

        Log.e("main", "create fake list");
        UserHelper.createUser("1",
                "Groot",
                "https://image.dhgate.com/0x0/f2/albu/g4/M00/8F/A9/rBVaEFmb-oqAdds-AAIn5o9UqUc875.jpg",
                "not selected");
        UserHelper.createUser("2",
                "Star-Lord",
                "https://img.huffingtonpost.com/asset/5b1045a92000006505b9311c.jpeg?cache=nbn7sg2chh&ops=crop_7_43_1667_1086,scalefit_720_noupscale",
                "not selected");
        UserHelper.createUser("3",
                "Gamora",
                "https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/6/61/Gamora_AIW_Profile.jpg/revision/latest/scale-to-width-down/2000?cb=20180518212221",
                "not selected");
        UserHelper.createUser("4",
                "Rocket Raccoon",
                "https://img1.looper.com/img/gallery/untold-truth-of-rocket-raccoon/intro.jpg",
                "not selected");
    }
}
