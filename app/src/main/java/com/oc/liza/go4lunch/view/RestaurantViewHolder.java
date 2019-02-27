package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.DistanceCalculator;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

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


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithRestaurantItem(final Result result, final RestaurantDetails details, final Context context) {

        this.context = context;
        this.name.setText(result.getName());
        this.address.setText(details.getAddress());

        if (result.getOpening_hours() != null) {
            String open = result.getOpening_hours().getOpen_now();
            if (open == "true") {
                this.opening_hours.setText("Open");
            } else {
                this.opening_hours.setText("Closed");
            }
        }
        String distance = calculateDistance(result);
        this.distance.setText(distance);


        //set stars depending on rating
        getRestaurantRating(result.getRating());

        //Check if users going
        checkIfUser(result.getName());
        //Set photo
        getPhoto(result);
        //Set on click listener to start Restaurant activity
        showRestaurantWhenClicked(result, details, context);
    }

    public void checkIfUser(final String name) {
        number_users = 0;
        UserHelper.getUsersCollection()
                .whereEqualTo("restaurant", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("manager", document.getId() + " => " + document.getData());
                                number_users++;
                            }
                            if (number_users > 0) {
                                ic_user.setVisibility(View.VISIBLE);
                                users.setVisibility(View.VISIBLE);
                                users.setText("(" + number_users + ")");
                                Log.e("query", name + number_users);
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

    public void getRestaurantRating(double note) {

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

    public String calculateDistance(Result result) {
        Double lat = result.getGeometry().getLocation().getLat();
        Double lng = result.getGeometry().getLocation().getLng();
        LatLng latLng1 = new LatLng(lat, lng);

        //Get current location
        SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        Double currentLat = Double.parseDouble(pref.getString("CurrentLatitude", "10"));
        Double currentLng = Double.parseDouble(pref.getString("CurrentLongitude", "10"));
        LatLng latLng2 = new LatLng(currentLat, currentLng);

        //Calculate distance in meter
        DistanceCalculator calculator = new DistanceCalculator();
        double distanceDouble = calculator.greatCircleInMeters(latLng1, latLng2);
        String distance = "";
        //If distance is more than 900 m, convert to km
        if (distanceDouble > 900) {
            distanceDouble = distanceDouble / 1000;

            distance = String.valueOf(calculator.roundOneDecimale(distanceDouble) + "km");
        } else {
            distance = String.valueOf(Math.round(distanceDouble)) + "m";
        }
        return distance;

    }

    public void showRestaurantWhenClicked(final Result result, final RestaurantDetails details, final Context context) {

        //when user click on view, open the article in a web view inside the app
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //store the articles web url in shared preferences
                SharedPreferences sharedPref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                String imgUrl = context.getString(R.string.photo_url)
                        + result.getPhotos().get(0).getPhotoRef()
                        + "&key="
                        + BuildConfig.API_KEY;
                editor.putString("Name", result.getName())
                        .putString("Img", imgUrl)
                        .putString("Address", details.getAddress())
                        .putString("Phone", details.getPhone())
                        .putString("Website", details.getWebsite())
                        .putString("Rating", String.valueOf(result.getRating()));
                editor.apply();

                //Start web view activity
                Intent restaurant = new Intent(context, RestaurantActivity.class);
                context.startActivity(restaurant);
            }
        });
    }

    public void getPhoto(Result result) {
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
