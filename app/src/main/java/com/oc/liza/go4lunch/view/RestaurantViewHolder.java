package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.util.DistanceCalculator;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.util.OpeningHoursManager;
import com.oc.liza.go4lunch.util.RestaurantManager;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

class RestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.opening_hours)
    TextView opening_hours;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.rating)
    LinearLayout rating;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.number_users)
    TextView users;
    @BindView(R.id.star_one)
    ImageView star_one;
    @BindView(R.id.star_two)
    ImageView star_two;
    @BindView(R.id.star_three)
    ImageView star_three;
    @BindView(R.id.ic_user)
    ImageView ic_user;

    private Context context;
    private int number_users;


    RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithRestaurantItem(RestaurantDetails result, Context context) {
        this.context = context;
        this.name.setText(result.getName());
        this.address.setText(result.getAddress());

        if (result.getOpening_hours() != null) {
            OpeningHoursManager openingHoursManager = new OpeningHoursManager(result, opening_hours, context);
            openingHoursManager.checkOpening();
        }
        //Calculate distance in meter
        DistanceCalculator calculator = new DistanceCalculator();
        String distance = calculator.calculateDistance(result, context);
        this.distance.setText(distance);

        //set stars depending on rating
        getRestaurantRating(result.getRating());

        //Check if users going
        checkIfUser(result.getPlace_id());
        //Set photo
        getPhoto(result);
        //Set on click listener to start Restaurant activity
        showRestaurantWhenClicked(result, context);
    }

    private void checkIfUser(final String place_id) {
        number_users = 0;
        UserHelper.getUsersCollection()
                .whereEqualTo("place_id", place_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                number_users++;
                            }
                            if (number_users > 0) {
                                ic_user.setVisibility(View.VISIBLE);
                                users.setVisibility(View.VISIBLE);
                                String numberOfUsers = "(" + number_users + ")";
                                users.setText(numberOfUsers);
                            } else {
                                ic_user.setVisibility(View.INVISIBLE);
                                users.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Log.d("manager", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getRestaurantRating(double note) {

        if (note >= 4.5) {
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
            star_three.setVisibility(View.VISIBLE);

        } else if (note > 2) {
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);

        } else if (note < 2) {
            star_one.setVisibility(View.VISIBLE);
        }
    }

    private void showRestaurantWhenClicked(final RestaurantDetails result, final Context context) {
        final RestaurantManager manager = new RestaurantManager(context);

        //when user click on view, start restaurant activity
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fetch info about restaurant, save it and start restaurant activity
                manager.saveInfoToRestaurantActivity(result.getPlace_id());
                try {
                    manager.startRestaurantActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getPhoto(RestaurantDetails result) {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + result.getPhotos().get(0).getPhotoRef()
                    + "&key="
                    + BuildConfig.API_KEY;
            Glide.with(context)
                    .load(url)
                    .into(photo);
        } catch (Exception e) {
            String defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg";
            Glide.with(context)
                    .load(defaultImg)
                    .into(photo);
        }

    }
}
