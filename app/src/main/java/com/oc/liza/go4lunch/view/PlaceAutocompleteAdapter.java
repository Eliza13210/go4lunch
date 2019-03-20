package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;

import java.util.List;

public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutoCompleteHolder> {

    private List<RestaurantDetails> mResultList;
    private Context context;

    public PlaceAutocompleteAdapter(Context context, List<RestaurantDetails> mResultList){
        this.context=context;
        this.mResultList=mResultList;
        Log.e("Adapter", "Adapter created with list " + mResultList.size());
    }


    @Override
    public PlaceAutoCompleteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search, parent, false);
        return new PlaceAutoCompleteHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceAutoCompleteHolder holder, int position) {
        holder.updateView(mResultList.get(position),context);
        Log.e("Adapter", "updateview");
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }


}
