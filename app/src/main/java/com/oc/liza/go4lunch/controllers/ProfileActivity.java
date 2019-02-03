package com.oc.liza.go4lunch.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.controllers.fragments.MapFragment;
import com.oc.liza.go4lunch.controllers.fragments.UsersFragment;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantStream;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private Disposable mDisposable;
    private SharedPreferences pref;
    private MyFragmentPagerAdapter adapter;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initBottomMenu();
        initViewpager();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_profile;
    }

    private void initViewpager() {

        //Set adapter to be able to switch between the fragments
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        MapFragment mapFragment = new MapFragment();
        ListFragment listFragment = new ListFragment();
        UsersFragment usersFragment = new UsersFragment();
        viewPager.setAdapter(adapter);
    }


    private void initBottomMenu() {
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_list:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_users:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle(R.string.workmates);
                       break;
                }
                return false;
            }
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navigation.getMenu().getItem(0).setChecked(false);

                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //REMOVE?
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

}
