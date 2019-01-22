package com.oc.liza.go4lunch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.oc.liza.go4lunch.auth.ProfileActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
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
        // Check if user is signed in (non-null) and update UI accordingly.
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

        Log.e("button", "clicked");
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
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

/**
 // --------------------
 // NAVIGATION
 // --------------------

 // 2 - Launch Sign-In Activity
 private void startSignInActivity() {
 // Configure Google Sign In
 GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
 .requestIdToken(getString(R.string.client_id_google))
 .requestEmail()
 .build();
 // Build a GoogleSignInClient with the options specified by gso.
 mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
 }

 private void signIn() {
 Intent signInIntent = mGoogleSignInClient.getSignInIntent();
 startActivityForResult(signInIntent, RC_SIGN_IN);
 Log.e("clicked", "sign in");
 }

 @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);

 // Pass the activity result back to the Facebook SDK
 mCallbackManager.onActivityResult(requestCode, resultCode, data);

 // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
 if (requestCode == RC_SIGN_IN) {
 Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
 try {
 // Google Sign In was successful, authenticate with Firebase
 GoogleSignInAccount account = task.getResult(ApiException.class);
 firebaseAuthWithGoogle(account);
 } catch (ApiException e) {
 // Google Sign In failed, update UI appropriately
 Log.w("SIGN IN", "Google sign in failed", e);
 // ...
 }
 }
 }

 private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
 Log.d("success", "firebaseAuthWithGoogle:" + acct.getId());

 AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
 mAuth.signInWithCredential(credential)
 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
 @Override public void onComplete(@NonNull Task<AuthResult> task) {
 if (task.isSuccessful()) {
 // Sign in success, update UI with the signed-in user's information
 Log.d("success", "signInWithCredential:success");
 FirebaseUser user = mAuth.getCurrentUser();
 Snackbar.make(findViewById(R.id.activity_main), "Authentication Succeded.", Snackbar.LENGTH_SHORT).show();

 // updateUI(user);
 } else {
 // If sign in fails, display a message to the user.
 Log.w("fail", "signInWithCredential:failure", task.getException());
 Snackbar.make(findViewById(R.id.activity_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
 }

 // ...
 }
 });
 }
 public void startActivityFacebook() {
 // Initialize Facebook Login button
 mCallbackManager = CallbackManager.Factory.create();
 LoginButton loginButton = findViewById(R.id.button_facebook);
 loginButton.setReadPermissions("email", "public_profile");
 loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
 @Override public void onSuccess(LoginResult loginResult) {
 Log.d("login facebook", "facebook:onSuccess:" + loginResult);
 handleFacebookAccessToken(loginResult.getAccessToken());
 }

 @Override public void onCancel() {
 Log.d("cancel", "facebook:onCancel");
 // ...
 }

 @Override public void onError(FacebookException error) {
 Log.d("onerror", "facebook:onError", error);
 // ...
 }
 });
 // ...

 }
 private void handleFacebookAccessToken(AccessToken token) {
 Log.d("token", "handleFacebookAccessToken:" + token);

 AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
 mAuth.signInWithCredential(credential)
 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
 @Override public void onComplete(@NonNull Task<AuthResult> task) {
 if (task.isSuccessful()) {
 // Sign in success, update UI with the signed-in user's information
 Log.d("success", "signInWithCredential:success");
 FirebaseUser user = mAuth.getCurrentUser();

 } else {
 // If sign in fails, display a message to the user.
 Log.w("credential", "signInWithCredential:failure", task.getException());
 Toast.makeText(MainActivity.this, "Authentication failed.",
 Toast.LENGTH_SHORT).show();

 }

 // ...
 }
 });
 }*/

