package com.oc.liza.go4lunch.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.MessageHelper;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.chat.ChatAdapter;
import com.oc.liza.go4lunch.models.firebase.Message;
import com.oc.liza.go4lunch.models.firebase.User;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerView;
    @BindView(R.id.chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.chat_image_chosen_preview)
    ImageView imageViewPreview;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter ChatAdapter;
    @Nullable
    private User modelCurrentUser;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getCurrentUserFromFirestore();
        this.configureRecyclerView();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_chat;
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.chat_send_button)
    public void onClickSendMessage() {
        Calendar c=Calendar.getInstance();
        Date date=c.getTime();
            // 1 - Check if text field is not empty and current user properly downloaded from Firestore
            if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null){
                // 2 - Create a new Message to Firestore
                MessageHelper.createMessageForChat(editTextMessage.getText().toString(), modelCurrentUser, date)
                        .addOnFailureListener(this.onFailureListener());
                // 3 - Reset text field
                this.editTextMessage.setText("");
            }
    }


    @OnClick(R.id.activity_mentor_chat_add_file_button)
    public void onClickAddFile() { }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore(){
        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        assert currentUser != null;
        UserHelper.getUser(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(){
        //Configure Adapter & RecyclerView
        this.ChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat()),
                Glide.with(this), this, this.currentUser.getUid());
        ChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(ChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.ChatAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.ChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

    }

}
