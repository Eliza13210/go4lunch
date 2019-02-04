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
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

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

    private Context context;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Log.e("viewholder rest", "create");
    }

    public void updateWithRestaurantItem(final Result result, final RestaurantDetails details, final Context context) {

        this.context = context;
        this.name.setText(result.getName());
        this.address.setText(details.getAddress());

        String open=String.valueOf(result.getOpening_hours().getOpen_now());
        this.opening_hours.setText(open);

               String distance = calculateDistance(result);
            this.distance.setText(distance);


        //set stars depending on rating
        //   getRestaurantRating(result.getRating());

        //Set photo
        getPhoto(result);
        showRestaurantWhenClicked(result, details, context);
    }

    public void getRestaurantRating(double note) {

        if (note >= 4) {
            ImageView star = new ImageView(context);
            star.setImageResource(R.drawable.ic_star);
            this.rating.addView(star);
            this.rating.addView(star);
            this.rating.addView(star);
        } else if (note >= 2) {
            ImageView star = new ImageView(context);
            star.setImageResource(R.drawable.ic_star);
            this.rating.addView(star);
            this.rating.addView(star);
        } else if (note == 1) {
            ImageView star = new ImageView(context);
            star.setImageResource(R.drawable.ic_star);
            this.rating.addView(star);

        }
    }

    public String calculateDistance(Result result) {
        Double lat=result.getGeometry().getLocation().getLng();
        Double lng=result.getGeometry().getLocation().getLng();

        String distance = "";

        //Get current location
        SharedPreferences pref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        Double currentLat = Double.parseDouble(pref.getString("CurrentLatitude", "10"));
        Double currentLng = Double.parseDouble(pref.getString("CurrentLongitude", "10"));

        final double AVERAGE_RADIUS_OF_EARTH = 6371;

        double latDistance = Math.toRadians(currentLat - lat);
        double lngDistance = Math.toRadians(currentLng - lng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(currentLat))) *
                        (Math.cos(Math.toRadians(lat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        long i = (Math.round(AVERAGE_RADIUS_OF_EARTH * c));

        distance = String.valueOf(i) + " m";

        Log.e("calculate", "result:" + distance);
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
                editor.putString("Name", result.getName())
                        .putString("Img", result.getPhotos().get(0).getPhotoRef())
                        .putString("Address", details.getAddress())
                        .putString("Phone", details.getPhone())
                        .putString("Website", details.getWebsite());
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
    public void getUsers(){

    }

}
