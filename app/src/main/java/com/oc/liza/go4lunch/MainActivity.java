package com.oc.liza.go4lunch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.ProfileActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main)
    LinearLayout linearLayout;

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private List<AuthUI.IdpConfig> providers;
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
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void startSignInActivity() {
        // Choose authentication providers
        providers = Arrays.asList(
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                createUserInFirestore();
                startProfileActivity();

            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.linearLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.linearLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.linearLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void startProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    // 1 - Http request that create user in firestore
    private void createUserInFirestore() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String urlPicture = (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null)
                    ? FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() : null;
            String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture, "not selected").addOnFailureListener(this.onFailureListener());
        }
    }

    //BaseActivity?
    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };

    }
}
