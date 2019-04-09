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
import com.oc.liza.go4lunch.controllers.ProfileActivity;
import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.firebase.User;
import com.oc.liza.go4lunch.util.RestaurantManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

    //Info to use in notification message
    private RestaurantManager manager;
    private String restaurant;
    private String address;
    private String message;
    private String place_id;
    private List<User> users = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            String message = remoteMessage.getData().get("lunch");
            Log.e("tag", remoteMessage.toString());
            //Check if message is about lunch notification
            assert message != null;
            if (message.equals("lunch")) {
                this.getInfoAboutLunch();
            }
        }
    }

    public void getInfoAboutLunch() {
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
                    if (user.getRestaurant() != null && !user.getRestaurant().equals("not selected")) {
                        restaurant = user.getRestaurant();
                        place_id = user.getPlace_id();
                        address = manager.getRestaurantAddress(place_id);
                    } else {
                        restaurant = getString(R.string.drawer_lunch);
                        address = "";
                    }
                    getListOfUsers();
                }
            }
        });
    }

    private void getListOfUsers() {
        //Get info about workmates going to the same restaurant
        UserHelper.getUsersCollection()
                .whereEqualTo("place_id", place_id)
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
                            //User has chosen a restaurant
                            if (!restaurant.equals(getString(R.string.drawer_lunch))) {
                                //Create notification message
                                message = getString(R.string.you_are_going) + restaurant + ", " + address + getString(R.string.with_workmates) + users;
                            } else {
                                //User hasn't chosen a restaurant
                                message = getString(R.string.drawer_lunch);
                            }
                            //Send visual message
                            sendVisualNotification(message);
                        } else {
                            Log.d("RestaurantA", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void sendVisualNotification(String messageBody) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        PendingIntent pendingIntent;
        //User has chosen a restaurant
        if (!restaurant.equals(getString(R.string.drawer_lunch))) {
            RestaurantManager manager = new RestaurantManager(this);
            manager.saveInfoToRestaurantActivity(place_id);
            Intent intent = new Intent(this, RestaurantActivity.class);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        } else { //User hasn't chosen a restaurant
            Intent intent = new Intent(this, ProfileActivity.class);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
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
        int NOTIFICATION_ID = 7;
        String NOTIFICATION_TAG = "FIREBASEOC";
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
