package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.util.RestaurantManager;

import butterknife.BindView;
import butterknife.ButterKnife;

class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_photo)
    ImageView photo;
    @BindView(R.id.user_text)
    TextView text;

    UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateUserItem(final User user, final Context context) {
        // IS JOINING IF CONTEXT IS RESTAURANT ACTIVITY
        if (context instanceof RestaurantActivity) {
            this.text.setText(user.getUsername());
            this.text.append(context.getString(R.string.is_joining));
        } else {
            if (!user.getRestaurant().equals("not selected")) {
                this.text.setText(user.getUsername());
                this.text.append(context.getString(R.string.is_eating_at));
                this.text.append(user.getRestaurant());

                //Set on click listener to start Restaurant activity
                showRestaurantWhenClicked(user.getPlace_id(), context);
            } else {
                if (Build.VERSION.SDK_INT < 23) {
                    text.setTextAppearance(context, R.style.cursive);
                } else {
                    text.setTextAppearance(R.style.cursive);
                }
                text.setText(user.getUsername());
                text.append(context.getString(R.string.hasn_not_decided));
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

    private void showRestaurantWhenClicked(final String place_id, final Context context) {
        //when user click on view, open the article in a web view inside the app
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantManager manager = new RestaurantManager(context);
                manager.saveInfoToRestaurantActivity(place_id);
                try {
                    manager.startRestaurantActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
