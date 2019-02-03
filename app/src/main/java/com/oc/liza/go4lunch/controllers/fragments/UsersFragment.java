package com.oc.liza.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.view.RecyclerViewAdapter;
import com.oc.liza.go4lunch.view.UserAdapter;
import com.oc.liza.go4lunch.view.UserViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends Fragment {

    @BindView(R.id.recycler_view_users)
    RecyclerView recyclerView;

    private List<User> users;
    private UserAdapter adapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);
        this.initRecyclerView();
        this.getListOfUsers();
        return view;
    }

    private void getListOfUsers() {
        //Do list of UID
        UserHelper.getUsersCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // convert document to POJO
                                User user = document.toObject(User.class);
                                users.add(user);
                                Log.d("RestaurantA", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("RestaurantA", "Error getting documents: ", task.getException());
                        }
                        adapter.notifyDataSetChanged();
                        Log.e("notify", "size " + users.size());
                    }
                });

        /**String[] listUid = {"1", "2", "3", "4"};
         //Create list of UID
         for (String s : listUid) {
         DocumentSnapshot document = UserHelper.getUser(s).getResult();
         if (document.exists()) {
         // convert document to POJO
         User user = document.toObject(User.class);
         users.add(user);
         }
         }
         SharedPreferences pref = getActivity().getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
         SharedPreferences.Editor prefsEditor = pref.edit();
         Gson gson = new Gson();
         String json = gson.toJson(users);
         prefsEditor.putString("ListUsers", json);
         prefsEditor.apply(); */
    }

    private void initRecyclerView() {
        // 3.1 - Reset list
        users = new ArrayList<>();
        // 3.2 - Create adapter passing the list of news
        this.adapter = new UserAdapter(this.users);
        // 3.3 - Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
