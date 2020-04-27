package com.example.go4lunch.notifications;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public abstract class WorkerNotificationController
{
    private static final String WORK_REQUEST_NAME = "WORK_REQUEST_NAME_Go4Lunch";
    private static final String WORK_REQUEST_TAG = "WORK_REQUEST_TAG_Go4Lunch";
    private static final int NOTIFICATION_HOUR = 12;
    private static final int NOTIFICATION_MINUTE = 0;
    private static final int NOTIFICATION_FREQUENCY_DAY = 1;


    public static void startWorkRequest(Context context)
    {
        PeriodicWorkRequest workRequest = configureWorkRequest();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_REQUEST_NAME, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }

    public static void stopWorkRequest(Context context)
    {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_REQUEST_TAG);
    }


    /**
     * Build a PeriodicWorkRequest with Delay and Constraints
     * @return the PeriodicWorkRequest for the notification
     */
    private static PeriodicWorkRequest configureWorkRequest()
    {
        // InitialDelay
        final Calendar calendar = Calendar.getInstance();

        final long nowTimeInMillis = calendar.getTimeInMillis();

        //  NOTIFICATION_HOUR = 12 & NOTIFICATION_MINUTE = 0
        //  if Now >= 12h00 -> + 1 day
        if (calendar.get(Calendar.HOUR_OF_DAY) > NOTIFICATION_HOUR
                || (calendar.get(Calendar.HOUR_OF_DAY) == NOTIFICATION_HOUR && calendar.get(Calendar.MINUTE) > NOTIFICATION_MINUTE))
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        calendar.set(Calendar.MINUTE, NOTIFICATION_MINUTE);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final long initialDelay = calendar.getTimeInMillis() - nowTimeInMillis;

        // Constraints
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        // PeriodicWorkRequest
        return new PeriodicWorkRequest.Builder(NotificationWorker.class,
                NOTIFICATION_FREQUENCY_DAY, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(WORK_REQUEST_TAG)
                .build();
    }


}
