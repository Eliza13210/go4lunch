package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;
import com.oc.liza.go4lunch.models.NearbySearchObject;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.network.RestaurantService;
import com.oc.liza.go4lunch.network.RestaurantStream;
import com.oc.liza.go4lunch.view.MyFragmentPagerAdapter;
import com.oc.liza.go4lunch.view.PlaceAutocompleteAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class SearchManager {
    //API
    private List<RestaurantDetails> results = new ArrayList<>();
    private Disposable disposable;

    //For Autocomplete
    private RectangularBounds bounds;
    private SearchView searchView;
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;

    private Context context;
    private RestaurantManager restaurantManager;
    private MyFragmentPagerAdapter fragmentAdapter;
    private PlaceAutocompleteAdapter adapter;
    private RecyclerView recyclerView;

    //To delay
    private CountDownTimer timer = null;


    public SearchManager(Context context, MyFragmentPagerAdapter fragmentAdapter, RecyclerView recyclerView) {
        this.context = context;
        this.fragmentAdapter = fragmentAdapter;
        this.recyclerView = recyclerView;
        restaurantManager = new RestaurantManager(context);
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        adapter = new PlaceAutocompleteAdapter(context, results);
        // Attach the adapter to the recycler view to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void setBounds() {
        // Create a RectangularBounds object.
        LocationManager locationManager = new LocationManager(context);
        LatLng center = locationManager.getCurrentLatLng();
        Double radiusInMeters = Double.parseDouble(RestaurantService.RADIUS);

        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        bounds = RectangularBounds.newInstance(southwestCorner, northeastCorner);
    }

    public void initSearch(SearchView view) {
        this.searchView = view;
        placesClient = Places.createClient(context);
        token = AutocompleteSessionToken.newInstance();
        setBounds();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                restaurantManager.updateListAfterSearch(results);
                fragmentAdapter.notifyDataSetChanged();
                searchView.onActionViewCollapsed();
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(1500, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        //do what you wish
                        search();
                        results.clear();
                    }
                }.start();

                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                resetList();
                fragmentAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void search() {
        String query = searchView.getQuery().toString();
        if (query.isEmpty()) {
            results.clear();
            adapter.notifyDataSetChanged();
        }
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {

                //If no results
                if (response.getAutocompletePredictions().isEmpty()) {
                    recyclerView.removeAllViews();
                    Log.e("predictions", "is empty");
                } else {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        //If Autocomplete gave result...
                        Log.i("cust", prediction.getPrimaryText(null).toString());
                        String search = prediction.getPlaceId();
                        disposable = RestaurantStream.fetchDetailsStream(search)
                                .subscribeWith(new DisposableObserver<NearbySearchObject>() {
                                    @Override
                                    public void onNext(NearbySearchObject nearbySearchObject) {
                                        //...Check if it is a restaurant
                                        for (int j = 0; j < nearbySearchObject.getDetails().getTypes().size(); j++) {
                                            if (nearbySearchObject.getDetails().getTypes().get(j).equals("restaurant")) {
                                                results.add(nearbySearchObject.getDetails());
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("Main", "Error fetching restaurants " + e);
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("failure", "Place not found: " + apiException.getStatusCode());
                }
            }
        });
        //Show all the restaurants again when closing searchview
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                resetList();
                fragmentAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public void resetList() {
        restaurantManager.resetFullListOfRestaurants();
    }

    public void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}

