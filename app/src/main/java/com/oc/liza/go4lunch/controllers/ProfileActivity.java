package com.oc.liza.go4lunch.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.RestaurantManager;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

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

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Set the fields to specify which types of place data to
// return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    private MyFragmentPagerAdapter adapter;
    private final FirebaseAuth currentUser = FirebaseAuth.getInstance();
    private User user;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // Initialize Places.
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

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
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
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

        //Find views in header
        ImageView user_photo = navView.findViewById(R.id.photo);
        TextView user_name = navView.findViewById(R.id.user_name);
        TextView user_email = navView.findViewById(R.id.user_email);
        //Set photo
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            String url = currentUser.getPhotoUrl().toString();
            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(user_photo);
        } catch (Exception e) {
            String defaultImg = "https://dunked.cdn.speedyrails.net/assets/prod/22884/p17s2tfgc31jte13d51pea1l2oblr3.png";

            Glide.with(this)
                    .load(defaultImg)
                    .into(user_photo);
        }
        user_name.setText(currentUser.getDisplayName());
        user_email.setText(currentUser.getEmail());
    }

    @Override
    public void onBackPressed() {
        //  Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                /** try {
                 Intent intent = new PlaceAutocomplete.IntentBuilder
                 (PlaceAutocomplete.MODE_OVERLAY)
                 .build(this);
                 startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                 } catch (GooglePlayServicesRepairableException |
                 GooglePlayServicesNotAvailableException e) {
                 e.printStackTrace();
                 }*/

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //SharedPreferences pref=getSharedPreferences("Go4Lunch", MODE_PRIVATE);
                // pref.edit().putString("Search", place.getName()).apply();
                //   startActivity(new Intent(ProfileActivity.this, SearchResultsActivity.class));
                Log.e("profile search", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("profile search", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Handle user click in drawer menu and action bar menu
     *
     * @param item the user clicked on
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        actionOnClick(item);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void actionOnClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_lunch:
                currentLunch();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_signout:
                AuthUI.getInstance()
                        .signOut(ProfileActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                            }
                        });

                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item);
        }
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

    private void currentLunch() {
        //Fetch list of restaurants
        final SharedPreferences pref = this.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        final String json = pref.getString("ListOfRestaurants", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        final List<Result> results = gson.fromJson(json, type);
        //Create a restaurant manager to get restaurant info and launch restaurant activity
        final RestaurantManager manager = new RestaurantManager(this, results);

        //Fetch current user in database
        FirebaseUser firebase = currentUser.getCurrentUser();
        String current = firebase.getUid();

        UserHelper.getUser(current).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // convert document to POJO
                    user = document.toObject(User.class);
                    Log.e("drawer", user.getRestaurant());

                    if (!user.getRestaurant().isEmpty()) {
                        Log.e("drawer", "not empty");


                        for (int i = 0; i < results.size(); i++) {
                            Log.e("drawer", results.get(i).getName());
                            if (results.get(i).getName().equals(user.getRestaurant())) {
                                Log.e("drawer", results.get(i).getName());
                                //Fetch info about restaurant, save it and start restaurant activity

                                manager.fetchRestaurantDetails(i);

                            }
                        }
                    }
                }
            }
        });


    }


}
