package com.oc.liza.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.util.RestaurantManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";

    //Info to use in notification message
    private RestaurantManager manager;
    private String restaurant;
    private String address;
    private List<User> users = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            Log.e("tag", message);
            //Check if message is about lunch notification
            assert message != null;
            if (message.equals("Lunch")) {
                this.getInfoAboutLunch();
            }
        }
    }

    private void getInfoAboutLunch() {
        manager = new RestaurantManager(getApplicationContext());
        String current = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //Get info about current users restaurant
        UserHelper.getUser(current).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // convert document to POJO
                    assert document != null;
                    User user = document.toObject(User.class);

                    assert user != null;
                    restaurant = user.getRestaurant();
                    if (restaurant!= null) {
                        address = manager.getRestaurantAddress(restaurant);
                        getListOfUsers();
                    }else{
                        Log.e("Notification", "User has not chosen a restaurant for lunch");
                    }
                }
            }
        });
    }

    private void getListOfUsers() {
        //Get info about workmates going to the same restaurant
        UserHelper.getUsersCollection()
                .whereEqualTo("restaurant", restaurant)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                // convert document to POJO
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            //Create notification message
                            String message = "Your are going to: " + restaurant + ", " + address + "With your workmates: " + users;
                            //Send visual message
                            sendVisualNotification(message);
                        } else {
                            Log.d("RestaurantA", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    // ---

    private void sendVisualNotification(String messageBody) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, RestaurantActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.addLine(messageBody);

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.coffe_cup)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message from Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
