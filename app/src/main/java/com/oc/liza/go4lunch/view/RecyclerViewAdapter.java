package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    // FOR DATA
    private List<Result> listOfRestaurants;
    private Context context;

    public RecyclerViewAdapter(List<Result> listOfRestaurants) {
        this.listOfRestaurants = listOfRestaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_restaurant, viewGroup, false);

        return new RestaurantViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder restaurantViewHolder, int i) {
        restaurantViewHolder.updateWithNewsItem(this.listOfRestaurants.get(i), context);
    }

    @Override
    public int getItemCount() {
        return this.listOfRestaurants.size();
    }
}
