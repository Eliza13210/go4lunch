package com.oc.liza.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.util.RestaurantManager;
import com.oc.liza.go4lunch.view.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @BindView(R.id.recycler_view_restaurants)
    RecyclerView recyclerView;

    private List<RestaurantDetails> listRestaurants = new ArrayList<>();

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        this.getListOfRestaurants();
        return view;
    }

    private void getListOfRestaurants() {
        //Fetch list of nearby restaurants
        RestaurantManager restaurantManager = new RestaurantManager(Objects.requireNonNull(getActivity()));
        listRestaurants = restaurantManager.getListOfRestaurants();
        //Show in recycler view
        configureRecyclerView();
    }

    //  Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // Create adapter passing the list of news
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this.listRestaurants);
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
