package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private List<RestaurantDetails> listOfDetails;
    private Context context;

    public RecyclerViewAdapter(List<Result> listOfRestaurants, List<RestaurantDetails> listOfDetails) {
        this.listOfRestaurants = listOfRestaurants;
        this.listOfDetails = listOfDetails;
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
        restaurantViewHolder.updateWithRestaurantItem(this.listOfRestaurants.get(i), this.listOfDetails.get(i), context);
    }

    @Override
    public int getItemCount() {
        Log.e("list adapter", "size " + listOfRestaurants.size());
        return this.listOfRestaurants.size();
    }
}
