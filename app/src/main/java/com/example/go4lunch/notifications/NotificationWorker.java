package com.example.go4lunch.notifications;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationWorker extends Worker
{

    private UserFirebaseRepository userFirebaseRepository;
    private RestaurantFirebaseRepository restaurantFirebaseRepository;
    private User currentUser;
    private Restaurant currentRestaurant;
    private Context context;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
        this.context = context;
        this.userFirebaseRepository = new UserFirebaseRepository();
        this.restaurantFirebaseRepository = new RestaurantFirebaseRepository();
    }

    @NonNull
    @Override
    public Result doWork()
    {
        this.getCurrentUserFromFirebase();
        return Result.success();
    }

    private void getCurrentUserFromFirebase()
    {
        String uid = FirebaseAuth.getInstance().getUid();
        this.userFirebaseRepository.getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            currentUser = documentSnapshot.toObject(User.class);
            if (Objects.requireNonNull(currentUser).isChooseRestaurant())
            {
                getCurrentRestaurantFromFirebase(currentUser.getRestaurantChoose().getPlaceId());
            }
        });
    }

    private void getCurrentRestaurantFromFirebase(String placeId)
    {
        this.restaurantFirebaseRepository.getRestaurant(placeId).addOnSuccessListener(documentSnapshot -> {
            currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            createMessageWithRestaurantChoose();
        });
    }

    //TODO : TESTS UNITAIRES ?

    /**
     * Create the Message with information of RestaurantChoose
     */
    private void createMessageWithRestaurantChoose()
    {
        List<String> stringForNotification = new ArrayList<>();
        StringBuilder workmatesListString = new StringBuilder();

        String name = currentRestaurant.getName();
        String address = currentRestaurant.getAddress();
        stringForNotification.add(name);
        stringForNotification.add(address);

        List<User> listWorkmates = currentRestaurant.getUserList();

        if (listWorkmates.size() > 1)
        {
            workmatesListString.append(context.getResources().getString(R.string.notification_workmates_with));
            int size = listWorkmates.size();
            for (int i = 0; i < size; i ++)
            {
                User userToCompare = listWorkmates.get(i);
                //Remove current user from the list of workmates in Notification
                if (!userToCompare.getIllustration().equals(currentUser.getIllustration()) || !userToCompare.getName().equals(currentUser.getName()))
                {
                    String nameWorkmate = userToCompare.getName();
                    workmatesListString.append(" ").append(nameWorkmate).append(",");
                }
            }
            //Remove the last ","
            workmatesListString.deleteCharAt(workmatesListString.length()-1);

            stringForNotification.add(workmatesListString.toString());
        }
        showNotification(stringForNotification);
    }

    /**
     * Build the notification
     * @param listWorkmatesString List<String> with the RestaurantChoose's names of Workmates
     */
    private void showNotification(List<String> listWorkmatesString)
    {
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("placeId", currentRestaurant.getPlaceId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Channel (Android 8)
        String channelId = "task_channel";
        String channelName = "task_name";

        // 3 - Build a Notification object
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getResources().getString(R.string.notification_message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 4 - Display the workmatesList or not
        if (listWorkmatesString.size() == 2)
        {
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .addLine(listWorkmatesString.get(0))
                    .addLine(listWorkmatesString.get(1)));
        }
        else if (listWorkmatesString.size() == 3)
        {
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .addLine(listWorkmatesString.get(0))
                    .addLine(listWorkmatesString.get(1))
                    .addLine(listWorkmatesString.get(2)));

        }

        // 5 - Add the Notification to the Notification Manager and show it
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            Objects.requireNonNull(manager).createNotificationChannel(channel);
        }

        Objects.requireNonNull(manager).notify(1, builder.build());
    }

}
