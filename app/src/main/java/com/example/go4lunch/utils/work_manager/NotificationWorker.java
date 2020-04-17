package com.example.go4lunch.utils.work_manager;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseInterface;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class NotificationWorker extends Worker
{

    private UserFirebaseRepository userFirebaseRepository;
    private RestaurantFirebaseRepository restaurantFirebaseRepository;

    private User currentUser;
    private Restaurant currentRestaurant;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.userFirebaseRepository = new UserFirebaseRepository();
        this.restaurantFirebaseRepository = new RestaurantFirebaseRepository();
    }

    @NonNull
    @Override
    public Result doWork() {

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
        this.restaurantFirebaseRepository.getRestaurant(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentRestaurant = documentSnapshot.toObject(Restaurant.class);
                getCurrentRestaurant();
            }
        });
    }

    private void getCurrentRestaurant()
    {
        String name = currentRestaurant.getName();
        String address = currentRestaurant.getAddress();
        List<User> listWorkmates = currentRestaurant.getUserList();

        StringBuilder messageFinal = new StringBuilder();
        messageFinal.append("Test MESSAGE").append(" ").append(name).append(" - ").append(address);

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

        showNotification("GO4Lunch", messageToPush);

    }




    private void showNotification(String task, String message)
    {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_channel";
        String channelName = "task_name";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(manager).createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(task)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        Objects.requireNonNull(manager).notify(1, builder.build());
    }
}
