package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;

class PlaceAutoCompleteHolder extends RecyclerView.ViewHolder {

    private TextView name;

    PlaceAutoCompleteHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
    }

    void updateView(final RestaurantDetails result, final Context context) {
        Log.e("Holder", "result" + result.toString() + result.getName());
        name.setText(result.getName());
    }
}
