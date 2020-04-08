package com.example.go4lunch.view;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;

import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.activities.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Go4LunchApplication extends MultiDexApplication
{



    @Override
    public void onCreate() {
        super.onCreate();
        this.getCurrentUser();
    }



    private void getCurrentUser()
    {
        String uidUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        UserHelper.getUser(uidUser).addOnSuccessListener(documentSnapshot -> {
            StaticFields.CURRENT_USER = documentSnapshot.toObject(User.class);
            StaticFields.IUD_USER = uidUser;
        });
    }
}
