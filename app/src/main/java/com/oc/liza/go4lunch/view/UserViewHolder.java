package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.BuildConfig;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.models.firebase.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_photo)
    ImageView photo;
    @BindView(R.id.user_text)
    TextView text;


    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateUserItem(final User user, final Context context) {

        // IS JOINING IF CONTEXT IS RESTAURANT ACTIVITY
        if (context.equals(RestaurantActivity.class)) {
            this.text.setText(user.getUsername());
            this.text.append(" is joining! ");
        } else {
            if (!user.getRestaurant().equals("not selected")) {
                this.text.setText(user.getUsername());
                this.text.append(" is eating at ");
                this.text.append(user.getRestaurant());

                //Set on click listener to start Restaurant activity
                showRestaurantWhenClicked(user.getRestaurant(), context);
            } else {
                if (Build.VERSION.SDK_INT < 23) {
                    text.setTextAppearance(context, R.style.cursive);
                } else {
                    text.setTextAppearance(R.style.cursive);
                }
                text.setText(user.getUsername());
                text.append(" hasn't decided yet");
            }
        }
        //Set photo
        try {
            String url = user.getUrlPicture();
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(photo);
        } catch (Exception e) {
            String defaultImg = "https://dunked.cdn.speedyrails.net/assets/prod/22884/p17s2tfgc31jte13d51pea1l2oblr3.png";

            Glide.with(context)
                    .load(defaultImg)
                    .into(photo);
        }
    }

    public void showRestaurantWhenClicked(final String restaurant, final Context context) {


        //when user click on view, open the article in a web view inside the app
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                //Fetch restaurant information and save in shared pref
                sharedPref = context.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
                String json = sharedPref.getString("ListOfRestaurants", null);

                Gson gson = new Gson();
                Type type = new TypeToken<List<Result>>() {
                }.getType();

                List<Result> listRestaurants;
                listRestaurants = gson.fromJson(json, type);

                //Fetch restaurant information and save in shared pref
                String jsonDetails = sharedPref.getString("ListOfDetails", null);

                Gson gsonDetails = new Gson();
                Type typeDetails = new TypeToken<List<RestaurantDetails>>() {
                }.getType();

                List<RestaurantDetails> listDetails;
                listDetails = gsonDetails.fromJson(jsonDetails, typeDetails);

                for (int i = 0; i < listRestaurants.size(); i++) {
                    Log.e("match", listRestaurants.get(i).getName() +"="+restaurant );
                    if (listRestaurants.get(i).getName().equals(restaurant)) {

                        String imgUrl = context.getString(R.string.photo_url)
                                + listRestaurants.get(i).getPhotos().get(0).getPhotoRef()
                                + "&key="
                                + BuildConfig.API_KEY;
                        editor.putString("Name", listRestaurants.get(i).getName())
                                .putString("Img", imgUrl)
                                .putString("Address", listDetails.get(i).getAddress())
                                .putString("Phone", listDetails.get(i).getPhone())
                                .putString("Website", listDetails.get(i).getWebsite())
                                .putString("Rating", String.valueOf(listRestaurants.get(i).getRating()));
                        editor.apply();
                    }
                }
                //Start web view activity
                Intent restaurant = new Intent(context, RestaurantActivity.class);
                context.startActivity(restaurant);
            }
        });
    }

}
