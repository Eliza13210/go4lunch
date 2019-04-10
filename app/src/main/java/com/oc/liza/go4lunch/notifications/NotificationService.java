package com.oc.liza.go4lunch.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            String message = remoteMessage.getData().get("lunch");
            Log.e("tag", remoteMessage.toString());
            //Check if message is about lunch notification
            assert message != null;
            if (message.equals("lunch")) {
                FirebaseNotificationManager notify=new FirebaseNotificationManager(this);
                notify.getInfoAboutLunch();
            }
        }
    }
}
