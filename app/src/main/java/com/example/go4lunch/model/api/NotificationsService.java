package com.example.go4lunch.model.api;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.activities.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NotificationsService extends FirebaseMessagingService
{
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    private static final String NOTIFICATIONS_SHARED_PREFERENCES = "PREF_NOTIF";
    private static final String NOTIFICATIONS_BOOLEAN = "NOTIFICATIONS_BOOLEAN";

    private User currentUser;
    private Restaurant currentRestaurant;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();

            if (getSharePreferences())
            {
                currentUser = StaticFields.CURRENT_USER;

                if (currentUser.isChooseRestaurant())
                {
                    getCurrentRestaurant(message);
                }

            }
        }
    }


    private void getCurrentRestaurant(String message)
    {
        currentRestaurant = StaticFields.RESTAURANT_CHOOSE_BY_CURRENT_USER;
        String name = currentRestaurant.getName();
        String address = currentRestaurant.getAddress();
        List<User> listWorkmates = currentRestaurant.getUserList();

        StringBuilder messageFinal = new StringBuilder();
        messageFinal.append(message).append(" ").append(name).append(" - ").append(address);

        if (listWorkmates.size() > 1)
        {
            StringBuilder nameList = new StringBuilder();
            for (int i = 0; i < listWorkmates.size(); i ++)
            {
                String nameWorkmate = listWorkmates.get(i).getName();
                nameList.append(", ");
                nameList.append(nameWorkmate);
            }
            messageFinal.append(" with ").append(nameList);
        }
        else
        {
            messageFinal.append(".");
        }

        String messageToPush = String.valueOf(messageFinal);

        sendVisualNotification(messageToPush);

    }

    private boolean getSharePreferences()
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(NOTIFICATIONS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean isOk = sharedPreferences.getBoolean(NOTIFICATIONS_BOOLEAN, true);
        return isOk;
    }

    private void sendVisualNotification(String messageBody) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("placeId", currentRestaurant.getPlaceId());
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
                        .setSmallIcon(R.drawable.ic_logo_go4lunch)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.sdk_8_notification_channel_id);
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
