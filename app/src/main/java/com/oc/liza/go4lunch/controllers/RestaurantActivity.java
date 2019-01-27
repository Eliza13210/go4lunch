package com.oc.liza.go4lunch.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.auth.ProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.rest_photo)
    ImageView photo;
    @BindView(R.id.rest_address)
    TextView address;
    @BindView(R.id.rest_name)
    TextView name;
    @BindView(R.id.menu)
    NavigationView menu;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
        initRestaurant();
      //  initMenu();
    }

    private void initRestaurant() {
        SharedPreferences pref = getSharedPreferences("Go4Lunch", MODE_PRIVATE);
        String defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg";

        try {
            String url = pref.getString("Img", defaultImg);
            Glide.with(this)
                    .load(url)
                    .into(photo);
        } catch (Exception e) {
            Glide.with(this)
                    .load(defaultImg)
                    .into(photo);
        }
        name.setText(pref.getString("Name", "No name"));
        address.setText(pref.getString("Address", "Far away"));

    }

    private void initMenu() {
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
                                .signOut(RestaurantActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(RestaurantActivity.this, MainActivity.class));
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
}
