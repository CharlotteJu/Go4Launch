package com.example.go4lunch.model.api;



import android.app.Instrumentation;

import com.example.go4lunch.R;
import com.example.go4lunch.model.RestaurantPOJO;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RestaurantStreams {

    private static String type = "restaurant";

    public static Observable<RestaurantPOJO> streamFetchRestaurant (double lat, double lng, int radius, String key)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);
        String location = lat + "," + lng;

        return restaurantPlacesApi.getNearbyRestaurants(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
