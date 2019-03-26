package com.oc.liza.go4lunch.controllers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.SphericalUtil;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.controllers.fragments.UsersFragment;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantService;
import com.oc.liza.go4lunch.network.RestaurantStream;
import com.oc.liza.go4lunch.util.DrawerManager;
import com.oc.liza.go4lunch.util.LocationManager;
import com.oc.liza.go4lunch.util.RestaurantManager;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;
import com.oc.liza.go4lunch.view.PlaceAutocompleteAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_profile_nav_view)
    NavigationView navigationView;
    @BindView(R.id.navigation_bottom)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.list_search)
    RecyclerView recyclerView;

    //For search function
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<RestaurantDetails> results = new ArrayList<>();
    private Disposable disposable;
    private PlaceAutocompleteAdapter adapter;
    private String query;
    private RectangularBounds bounds;
    AutocompleteSessionToken token;
    PlacesClient placesClient;
    SearchView textview;
    SharedPreferences pref;

    private final FirebaseAuth currentUser = FirebaseAuth.getInstance();
    private DrawerManager manager;
    //Viewpager
    private MyFragmentPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        //Search function
        // Initialize Places.
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);
        // Create a new Places client instance.
        configureRecyclerView();

        initViewpager();
        initBottomMenu();
        configureDrawerLayout();
        initFirebase();
    }

    private void initSearch(SearchView view) {
        pref = getSharedPreferences("Go4Lunch", MODE_PRIVATE);
        this.textview = view;
        placesClient = Places.createClient(this);
        token = AutocompleteSessionToken.newInstance();
        setBounds();
        textview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RestaurantManager restaurantManager = new RestaurantManager(getApplicationContext());
                restaurantManager.updateListAfterSearch(results);
                fragmentAdapter.notifyDataSetChanged();
                textview.onActionViewCollapsed();
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                query = textview.getQuery().toString();
                if (query.isEmpty()) {
                    results.clear();
                    adapter.notifyDataSetChanged();
                }
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationRestriction(bounds)
                        //.setLocationRestriction(bounds)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(query)
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse response) {

                        //If no results
                        if (response.getAutocompletePredictions().isEmpty()) {
                            recyclerView.removeAllViews();
                            Log.e("predictions", "is empty");
                        } else {
                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                //If result
                                Log.i("cust", prediction.getPrimaryText(null).toString());

                                String search = prediction.getPlaceId();
                                Log.e("string", search);
                                disposable = RestaurantStream.fetchDetailsStream(search)
                                        .subscribeWith(new DisposableObserver<NearbySearchObject>() {
                                            @Override
                                            public void onNext(NearbySearchObject nearbySearchObject) {
                                                for (int j = 0; j < nearbySearchObject.getDetails().getTypes().size(); j++) {
                                                    if (nearbySearchObject.getDetails().getTypes().get(j).equals("restaurant")) {
                                                        results.add(nearbySearchObject.getDetails());
                                                    }
                                                }
                                                Log.e("result", " result " + nearbySearchObject.toString() + " " + results.size());
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("Main", "Error fetching restaurants " + e);
                                            }

                                            @Override
                                            public void onComplete() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                            }
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.e("failure", "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
                results.clear();
                return true;
            }
        });
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }


    public void setBounds() {
        // Create a RectangularBounds object.
        LocationManager locationManager = new LocationManager(this);
        LatLng center = locationManager.getCurrentLatLng();
        Double radiusInMeters = Double.parseDouble(RestaurantService.RADIUS);

        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        bounds = RectangularBounds.newInstance(southwestCorner, northeastCorner);
    }

    private void configureRecyclerView() {
        adapter = new PlaceAutocompleteAdapter(this, results);
        // 3.3 - Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(adapter);
        // 3.4 - Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_profile;
    }

    private void initViewpager() {
        // Creation of the list of fragments
        List<Fragment> fragments = new Vector<>();

        // Add Fragments in a list
        fragments.add(Fragment.instantiate(this, MapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ListFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, UsersFragment.class.getName()));
        //Set adapter to be able to switch between the fragments
        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
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
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = (SearchView) item.getActionView();
        if (sv != null) {
            initSearch(sv);
        }
        return true;
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
