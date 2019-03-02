package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.RestaurantManager;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.models.firebase.User;

import java.util.List;
import java.util.Objects;

public class DrawerManager {

    private Context context;
    private User user;
    private FirebaseUser currentUser;
    private List<Result> results;
    private RestaurantManager manager;

    public DrawerManager(Context context) {
        this.context = context;
    }

    public void initHeader(View navView) {
        //Find views in header
        ImageView user_photo = navView.findViewById(R.id.photo);
        TextView user_name = navView.findViewById(R.id.user_name);
        TextView user_email = navView.findViewById(R.id.user_email);
        //Set photo
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            assert currentUser != null;
            String url = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(user_photo);
        } catch (Exception e) {
            String defaultImg = "https://cdn.onlinewebfonts.com/svg/img_227642.png";
            Glide.with(context)
                    .load(defaultImg)
                    .into(user_photo);
        }
        user_name.setText(currentUser.getDisplayName());
        user_email.setText(currentUser.getEmail());
    }


    /**
     * Handle User click on item in drawer
     *
     * @param item is clicked
     */
    public void actionOnClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_lunch:
                //Show the restaurant that the user has chosen
                currentLunch();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_signout:
                //Sign out user from Firebase and return to Main Activity
                AuthUI.getInstance()
                        .signOut(context)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                context.startActivity(new Intent(context, MainActivity.class));
                            }
                        });
                break;
        }
    }

    private void currentLunch() {
        //Create a restaurant manager
        manager = new RestaurantManager(context);
        //Fetch list of restaurants
        results = manager.getListOfRestaurants();
        //Get information about witch restaurant user has chosen
        String current = currentUser.getUid();
        UserHelper.getUser(current).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // convert document to POJO
                    assert document != null;
                    user = document.toObject(User.class);
                    assert user != null;
                    //Find the chosen restaurant in list
                    if (!user.getRestaurant().isEmpty()) {
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).getName().equals(user.getRestaurant())) {
                                //Fetch info about restaurant, save it and start restaurant activity
                                manager.fetchRestaurantDetails(i);
                                context.startActivity(new Intent(context, RestaurantActivity.class));
                            }
                        }
                    } else {
                        Log.e("Drawer", "user hasn't chosen any restaurant");
                    }
                }
            }
        });
    }
}
