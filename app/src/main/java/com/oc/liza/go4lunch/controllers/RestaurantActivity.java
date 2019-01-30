package com.oc.liza.go4lunch.controllers;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.view.RecyclerViewAdapter;
import com.oc.liza.go4lunch.view.UserAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantActivity extends AppCompatActivity {

    @BindView(R.id.rest_photo)
    ImageView photo;
    @BindView(R.id.rest_address)
    TextView address;
    @BindView(R.id.rest_name)
    TextView name;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view_users)
    RecyclerView recyclerView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private TextView mTextMessage;
    private SharedPreferences pref;
    private String restName;
    private int restId;
    private List<User> users;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
        initRestaurant();
        initMenu();
        initButton();
        initRecyclerView();
    }

    private void initRecyclerView() {
        String json = pref.getString("List", "Empty list");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        users = gson.fromJson(json, type);


        // 3.1 - Reset list
        this.users = new ArrayList<>();
        // 3.2 - Create adapter passing the list of news
        this.adapter = new UserAdapter(this.users);
        // 3.3 - Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initRestaurant() {
        pref = getSharedPreferences("Go4Lunch", MODE_PRIVATE);
        String defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg";
        restName = pref.getString("Name", "No name");
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
        name.setText(restName);
        address.setText(pref.getString("Address", "Far away"));

    }


    //BaseActivity?
    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };

    }

    private void initButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                UserHelper.updateRestaurant(restName, FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addOnFailureListener(onFailureListener());
            }
        });
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
                    case R.id.navigation_phone:
                        mTextMessage.setText(R.string.phone);
                        pref.getString("Phone", null);
                        return true;

                    case R.id.navigation_like:
                        mTextMessage.setText(R.string.like);
                        return true;

                    case R.id.navigation_website:
                        mTextMessage.setText(R.string.website);
                        pref.getString("Website", null);
                        return true;
                }
                return false;
            }
        };
    }
}
