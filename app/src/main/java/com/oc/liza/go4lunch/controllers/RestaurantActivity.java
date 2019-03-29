package com.oc.liza.go4lunch.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.view.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @BindView(R.id.star_one)
    ImageView star_one;
    @BindView(R.id.star_two)
    ImageView star_two;
    @BindView(R.id.star_three)
    ImageView star_three;


    private SharedPreferences pref;
    private String restName;
    private String place_id;
    private List<User> users;
    private UserAdapter adapter;
    private Boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        initRestaurant();
        initRecyclerView();
        initMenu();
        initButton();
        getListOfUsers();
    }

    //Check if users are going to this restaurant
    private void getListOfUsers() {

        UserHelper.getUsersCollection()
                .whereEqualTo("place_id", place_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                // convert document to POJO
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("RestaurantA", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    private void initRecyclerView() {
        // 3.1 - Reset list
        this.users = new ArrayList<>();
        // 3.2 - Create adapter passing the list of news
        this.adapter = new UserAdapter(this.users);
        // 3.3 - Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Show restaurant photo, name and adress
    private void initRestaurant() {
        //Get restaurant info saved in shared preferences
        pref = getSharedPreferences("Go4Lunch", MODE_PRIVATE);
        //Photo
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
        //Name
        restName = pref.getString("Name", "No name");
        place_id = pref.getString("Place_id", null);
        name.setText(restName);
        //Address
        address.setText(pref.getString("Address", "Far away"));
        getRestaurantRating();
    }

    private void getRestaurantRating() {
        String restaurantRating = pref.getString("Rating", "0");
        Double rating = Double.valueOf(restaurantRating);

        if (rating >= 5) {
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
            star_three.setVisibility(View.VISIBLE);

        } else if (rating >= 2) {
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);

        } else if (rating == 1) {
            star_one.setVisibility(View.VISIBLE);
        }
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };

    }

    //Change color to green if user has clicked the button and chosen this restaurant
    private void initButton() {
        //CHECK IF USER GOING
        String uid = FirebaseAuth.getInstance().getUid();
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                if (user.getRestaurant().equals(place_id)) {
                    isClicked = true;
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClicked) {
                    isClicked = false;
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.browser_actions_bg_grey)));
                    //Update firestore with selected restaurant
                    UserHelper.updateRestaurant("Not selected", null, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .addOnFailureListener(onFailureListener());
                    getListOfUsers();
                } else {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    isClicked = true;

                    //Update firestore with selected restaurant
                    UserHelper.updateRestaurant(restName, place_id, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .addOnFailureListener(onFailureListener());
                    getListOfUsers();
                }

            }
        });
    }

    private void initMenu() {

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener
                mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_phone:
                        //Call restaurant
                        String phone = pref.getString("Phone", null);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                        return true;

                    case R.id.navigation_like:
                        UserHelper.updateLike(restName, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .addOnFailureListener(onFailureListener());
                        return true;

                    case R.id.navigation_website:
                        //Start web view activity
                        Intent startWebview = new Intent(RestaurantActivity.this, WebviewActivity.class);
                        startActivity(startWebview);
                        return true;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            startActivity(new Intent(RestaurantActivity.this, ProfileActivity.class));
        }
    }
}
