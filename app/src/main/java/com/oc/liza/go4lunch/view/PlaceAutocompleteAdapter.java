package com.oc.liza.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import java.util.List;

public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RestaurantDetails> mResultList;
    private Context context;
    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    public PlaceAutocompleteAdapter(Context context, List<RestaurantDetails> mResultList) {
        this.context = context;
        this.mResultList = mResultList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, parent, false);
            return new PlaceAutoCompleteHolder(itemView);
        } else {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_search_recyclerview, parent, false);
            return new FooterHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mResultList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            if (mResultList.size() > 0) {
                footerHolder.google.getLayoutParams().height = 20;
            }
        } else if (holder instanceof PlaceAutoCompleteHolder) {
            PlaceAutoCompleteHolder placeAutoCompleteHolder = (PlaceAutoCompleteHolder) holder;
            placeAutoCompleteHolder.updateView(mResultList.get(position), context);
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size() + 1;
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        ImageView google;

        FooterHolder(View itemView) {
            super(itemView);
            google = itemView.findViewById(R.id.google);
        }
    }
}
