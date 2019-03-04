package com.oc.liza.go4lunch.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.util.DrawerManager;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_profile_nav_view)
    NavigationView navigationView;
    @BindView(R.id.navigation_bottom)
    BottomNavigationView bottomNavigationView;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Set the fields to specify which types of place data to
// return after the user has made a selection.
    //List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    private final FirebaseAuth currentUser = FirebaseAuth.getInstance();
    private DrawerManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // Initialize Places.
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);

        // Create a new Places client instance.
        // PlacesClient placesClient = Places.createClient(this);

        initViewpager();
        initBottomMenu();
        configureDrawerLayout();
        initFirebase();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_profile;
    }

    private void initViewpager() {
        //Set adapter to be able to switch between the fragments
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private void initBottomMenu() {
        //Initialise the bottom navigation menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle(R.string.hungry);
                        return true;
                    case R.id.navigation_list:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle(R.string.hungry);
                        return true;
                    case R.id.navigation_users:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle(R.string.workmates);
                        return true;
                }
                return false;
            }
        });
    }

    // Configure Drawer Layout
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        initDrawerHeader();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initDrawerHeader() {
        //Inflate header layout
        View navView = navigationView.inflateHeaderView(R.layout.drawer_nav_header);
        manager = new DrawerManager(this);
        manager.initHeader(navView);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                //Start Autocomplete Search Widget
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("profile search", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("profile search", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e("Profile Activity", "User canceled");
            }
        }
    }

    /**
     * Handle user click in drawer menu and action bar menu
     *
     * @param item the user clicked on
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        manager.actionOnClick(item);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initFirebase() {
        currentUser.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentUser != null) {
                    System.out.println("User logged in");
                } else {
                    System.out.println("User not logged in");
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
            }
        });
    }

}
