package com.oc.liza.go4lunch.controllers;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.chat.ChatAdapter;
import com.oc.liza.go4lunch.chat.MessageHelper;
import com.oc.liza.go4lunch.models.firebase.Message;
import com.oc.liza.go4lunch.models.firebase.User;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerView;
    @BindView(R.id.chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.chat_image_chosen_preview)
    ImageView imageViewPreview;

    // Declaring Adapter and data
    private ChatAdapter ChatAdapter;
    @Nullable
    private User modelCurrentUser;
    private FirebaseUser currentUser;
    // Static data for permission to get image
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    // Uri of image selected by user
    private Uri uriImageSelected;
    private static final int RC_CHOOSE_PHOTO = 200;
    private String pathImageSavedInFirebase;

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Calling the appropriate method after activity result
        this.handleResponse(requestCode, resultCode, data);
    }

    // --------------------
    // ACTIONS
    // --------------------
    @OnClick(R.id.activity_chat_add_file_button)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() {
        this.chooseImageFromPhone();
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
        }
    }

    // --------------------
    // FILE MANAGEMENT
    // --------------------
    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        // Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageViewPreview);
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.chat_send_button)
    public void onClickSendMessage() {
        Date date = Calendar.getInstance().getTime();

        // Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
            // Check if the ImageView is set
            if (this.imageViewPreview.getDrawable() == null) {
                // Create a new Message to Firestore
                MessageHelper.createMessageForChat(editTextMessage.getText().toString(), modelCurrentUser, date)
                        .addOnFailureListener(this.onFailureListener());
                // Reset text field
                this.editTextMessage.setText("");
            } else {
                // SEND A IMAGE + TEXT IMAGE
                this.uploadPhotoInFirebaseAndSendMessage(editTextMessage.getText().toString(), date);
                this.editTextMessage.setText("");
                this.imageViewPreview.setImageDrawable(null);
            }
        }
    }

    // Upload a picture in Firebase and send a message
    private void uploadPhotoInFirebaseAndSendMessage(final String message, final Date date) {
        // UPLOAD TO GCS
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Uri file = uriImageSelected;
        final StorageReference imageRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), getString(R.string.chat_error) + exception, Toast.LENGTH_SHORT).show();
            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    pathImageSavedInFirebase = downloadUri.toString();
                    // SAVE MESSAGE IN FIRESTORE
                    MessageHelper.createMessageWithImageForChat
                            (pathImageSavedInFirebase, message, modelCurrentUser, date).addOnFailureListener(onFailureListener());
                }
            }
        });
    }


    // --------------------
    // REST REQUESTS
    // --------------------
    //Get Current User from Firestore
    private void getCurrentUserFromFirestore() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
    // Configure RecyclerView with a Query
    private void configureRecyclerView() {
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

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
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
        // Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.ChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

    }

}
