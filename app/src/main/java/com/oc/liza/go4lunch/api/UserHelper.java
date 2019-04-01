package com.oc.liza.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oc.liza.go4lunch.models.firebase.User;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String urlPicture, String restaurant) {
        User userToCreate = new User(uid, username, urlPicture, restaurant);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }


    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateRestaurant(String restaurant, String place_id, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("place_id", place_id, "restaurant", restaurant);
    }

    public static Task<Void> updateLike(String restaurant, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("like",
                FieldValue.arrayUnion(restaurant));
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
