package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.util.RestaurantManager;

class PlaceAutoCompleteHolder extends RecyclerView.ViewHolder {

    private TextView name;

    PlaceAutoCompleteHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
    }

    void updateView(final RestaurantDetails result, final Context context) {
        Log.e("Holder", "result" + result.toString() + result.getName());
        name.setText(result.getName());

        showRestaurantWhenClicked(result, context);
    }

    private void showRestaurantWhenClicked(final RestaurantDetails result, final Context context) {
        final RestaurantManager manager = new RestaurantManager(context);


        //when user click on view, start restaurant activity
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fetch info about restaurant, save it and start restaurant activity
                manager.saveInfoToRestaurantActivity(result.getPlace_id());
                manager.resetFullListOfRestaurants();
                try {
                    manager.startRestaurantActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
