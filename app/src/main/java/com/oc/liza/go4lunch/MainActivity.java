package com.oc.liza.go4lunch;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oc.liza.go4lunch.controllers.ProfileActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;

    @BindView(R.id.activity_main) LinearLayout linearLayout;

    @BindView(R.id.button_google)
    Button googleButton;
    @BindView(R.id.button_facebook)
    Button facebookButton;
    private CallbackManager mCallbackManager;
    List<AuthUI.IdpConfig> providers;
    FirebaseUser currentUser;

    // ...
    @Override
    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
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

    private void startProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));

    }

    protected Boolean isCurrentUserLogged(){ return (currentUser != null); }

    @Override
    public void onClick(View v) {
    }
   // Show Snack Bar with a message
    private void showSnackBar(LinearLayout linearLayout, String message){
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
                startProfileActivity();
                // ...
            } else { // ERRORS
                    if (response == null) {
                        showSnackBar(this.linearLayout, getString(R.string.error_authentication_canceled));
                    } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(this.linearLayout, getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(this.linearLayout, getString(R.string.error_unknown_error));
                    }
                }
                // Sign in failed. If response is null the User canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
