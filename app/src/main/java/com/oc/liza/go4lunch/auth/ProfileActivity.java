package com.oc.liza.go4lunch.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView mTextMessage;
    /**
     * Get User info
     * <p>
     * <p>
     * private void updateUIWhenCreating(){
     * <p>
     * if (this.getCurrentUser() != null){
     * <p>
     * //Get picture URL from Firebase
     * if (this.getCurrentUser().getPhotoUrl() != null) {
     * Glide.with(this)
     * .load(this.getCurrentUser().getPhotoUrl())
     * .apply(RequestOptions.circleCropTransform())
     * .into(imageViewProfile);
     * }
     * <p>
     * //Get email & username from Firebase
     * String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
     * String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
     * <p>
     * //Update views with data
     * this.textInputEditTextUsername.setText(username);
     * this.textViewEmail.setText(email);
     * }
     * }
     */

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initBottomMenu();
    }

    private void initBottomMenu() {
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        mTextMessage.setText(R.string.title_home);
                        AuthUI.getInstance()
                                .signOut(ProfileActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                    }
                                });
                        return true;
                    case R.id.navigation_dashboard:
                        mTextMessage.setText(R.string.title_dashboard);
                        return true;
                    case R.id.navigation_notifications:
                        mTextMessage.setText(R.string.title_notifications);
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * @Override public void onMapReady(GoogleMap googleMap) {
     * mMap = googleMap;
     * <p>
     * // Add a marker in Sydney, Australia, and move the camera.
     * <p>
     * LatLng place = new LatLng(mProviderLatitude, mProviderLongitude);
     * mMap.addMarker(new MarkerOptions().position(place).title("Marker"));
     * mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
     * }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                //User chose the "Search" item
                break;
        }
        return true;

    }

}
