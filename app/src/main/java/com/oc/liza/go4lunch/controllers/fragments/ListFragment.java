package com.oc.liza.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.Result;
import com.oc.liza.go4lunch.network.RestaurantStream;
import com.oc.liza.go4lunch.view.RecyclerViewAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListFragment extends Fragment {

    @BindView(R.id.recycler_view_restaurants)
    RecyclerView recyclerView;

    private List<Result> listRestaurants = new ArrayList<>();
    private ArrayList<RestaurantDetails> listOfDetails = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private Disposable disposable;
    private SharedPreferences pref;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        this.getListOfRestaurants();
        return view;
    }

    private void getListOfRestaurants() {
        pref = getActivity().getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String json = pref.getString("ListOfRestaurants", null);
        Log.e("listF", json);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();

        listRestaurants = gson.fromJson(json, type);
        if (!listRestaurants.isEmpty()) {
            for (Result r : listRestaurants) {
                getRestaurantDetails(r.getPlace_id());
            }
        }
    }

    //  Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.2 - Create adapter passing the list of news
        this.adapter = new RecyclerViewAdapter(this.listRestaurants, this.listOfDetails);
        // 3.3 - Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getRestaurantDetails(String place_id) {
        this.disposable = RestaurantStream.fetchDetailsStream((place_id))
                .subscribeWith(new DisposableObserver<NearbySearchObject>() {

                    @Override
                    public void onNext(NearbySearchObject nearbySearchObject) {
                        //  listOfDetails.add(nearbySearchObject.getDetails());
                        addToList(nearbySearchObject);
                        if (listOfDetails.size() == listRestaurants.size()) {
                            configureRecyclerView();
                        }
                        Log.e("onnext", "size " + listOfDetails.size());
                        Log.e("ListofRest", "size" + listRestaurants.size());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private void addToList(NearbySearchObject nearbySearchObject) {

        //Add restaurants results from fetched nearby search object to the list
        listOfDetails.add(nearbySearchObject.getDetails());
        //Save the list of restaurants
        Gson gson = new Gson();
        String json = gson.toJson(listOfDetails);
        pref.edit().putString("ListOfDetails", json).apply();
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
}
