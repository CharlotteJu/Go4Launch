package com.example.go4lunch.model.api;

import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.RestaurantPOJO;

import java.util.List;


import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RestaurantPlacesApi
{
    @GET("nearbysearch/json?")
    Observable<RestaurantPOJO> getNearbyRestaurants (@Query("location") String location,
                                                           @Query("radius") int radius, @Query ("type") String type, @Query("key") String key);

    @GET("details/json?")
    Observable<DetailPOJO> getDetailRestaurants (@Query("place_id") String placeId, @Query("key") String key);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


}
