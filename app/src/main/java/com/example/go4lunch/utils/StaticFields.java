package com.example.go4lunch.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public abstract class StaticFields
{
    public static Location CURRENT_LOCATION;

    public static User CURRENT_USER;

    public static String IUD_USER;

    public static Restaurant RESTAURANT_CHOOSE_BY_CURRENT_USER;





}
