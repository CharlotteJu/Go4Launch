package com.example.go4lunch.utils;

import android.location.Location;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public abstract class StaticFields
{
    public static Location CURRENT_LOCATION;

    public static User CURRENT_USER;

    public static String IUD_USER;

    public static Restaurant RESTAURANT_CHOOSE_BY_CURRENT_USER;

    public static List<Restaurant> RESTAURANTS_LIST = new ArrayList<>();

    public static List<Restaurant> RESTAURANTS_LIST_WITH_WORKMATES = new ArrayList<>();





}
