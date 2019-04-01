package com.oc.liza.go4lunch.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.controllers.fragments.UsersFragment;
import com.oc.liza.go4lunch.util.DrawerManager;
import com.oc.liza.go4lunch.util.SearchManager;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;

import java.util.List;
import java.util.Vector;

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
    @BindView(R.id.list_search)
    RecyclerView recyclerView;

    //For drawer menu
    private DrawerManager manager;
    //Viewpager
    private MyFragmentPagerAdapter fragmentAdapter;

    private SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // Search function - Initialize Places.
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);

        initViewpager();
        initBottomMenu();
        configureDrawerLayout();
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
        //Add fragments to adapter
        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
        //Use search manager with fragment adapter
        searchManager = new SearchManager(this, fragmentAdapter, recyclerView);
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; the search view
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Show all the restaurants when search view is closed
                searchManager.resetList();
                fragmentAdapter.notifyDataSetChanged();
                return true;
            }
        });
        //Use search manager to initialize search function
        SearchView sv = (SearchView) item.getActionView();
        if (sv != null) {
            searchManager.initSearch(sv);
        }
        return true;
    }

    //Autocomplete search
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //For search function
        int AUTOCOMPLETE_REQUEST_CODE = 1;
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

    @Override
    protected void onResume() {
        super.onResume();
        fragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchManager.disposeWhenDestroy();
    }
}
