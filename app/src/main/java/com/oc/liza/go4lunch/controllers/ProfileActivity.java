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
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.fragments.ListFragment;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;

import butterknife.BindView;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MyFragmentPagerAdapter adapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    viewPager.setCurrentItem(0);
                    toolbar.setTitle(R.string.hungry);
                    Log.e("navigation", "pressed list" + viewPager.getCurrentItem());
                    return true;
                case R.id.navigation_list:

                    Log.e("navigation", "pressed list" + viewPager.getCurrentItem());
                    viewPager.setCurrentItem(1);
                    toolbar.setTitle(R.string.hungry);
                    Log.e("navigation", "pressed after changed list" + viewPager.getCurrentItem());
                    return true;
                case R.id.navigation_users:
                    viewPager.setCurrentItem(2);
                    toolbar.setTitle(R.string.workmates);

                    Log.e("navigation", "pressed list" + viewPager.getCurrentItem());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewpager();
        initBottomMenu();
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
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
