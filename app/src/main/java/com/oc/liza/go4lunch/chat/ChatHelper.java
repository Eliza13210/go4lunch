package com.oc.liza.go4lunch.chat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

class ChatHelper {

    private static final String COLLECTION_NAME = "chat";

    // --- COLLECTION REFERENCE ---

    static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

}
