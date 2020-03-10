package com.example.go4lunch.model.api;



import com.example.go4lunch.R;
import com.example.go4lunch.model.RestaurantPOJO;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RestaurantStreams {

    private static String keyGOOGLE = String.valueOf(R.string.google_api_key);

    public static Observable<List<RestaurantPOJO>> streamFetchRestaurant (double lat, double lng, int radius)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);
        return restaurantPlacesApi.getNearbyRestaurants(lat, lng, radius, keyGOOGLE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
