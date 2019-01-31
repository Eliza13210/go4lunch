package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.firebase.User;

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

    public void updateUserItem(User user, Context context) {
        //Set photo
        try {
            String url = user.getUrlPicture();
            Glide.with(context)
                    .load(url)
                    .into(photo);
        } catch (Exception e) {
            String defaultImg = "https://dunked.cdn.speedyrails.net/assets/prod/22884/p17s2tfgc31jte13d51pea1l2oblr3.png";

            Glide.with(context)
                    .load(defaultImg)
                    .into(photo);
        }

        // IS JOINING IF CONTEXT IS RESTAURANT ACTIVITY

        if(user.getRestaurant()!="not selected") {
            text.setText(user.getUsername());
            text.append(" is eating ");
            text.append(user.getRestaurant());
        } else{
            if (Build.VERSION.SDK_INT < 23) {
                text.setTextAppearance(context, R.style.cursive);
            } else {
                text.setTextAppearance(R.style.cursive);
            }
            text.setText(user.getUsername());
            text.append(" hasn't decided yet");
        }
    }
}
