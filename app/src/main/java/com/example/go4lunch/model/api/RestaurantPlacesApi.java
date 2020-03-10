package com.example.go4lunch.model.api;

import com.example.go4lunch.model.RestaurantPOJO;

import java.util.List;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestaurantPlacesApi
{
    @GET("location={lat},{lng}&radius={radius}&type=restaurant&key={key}")
    Observable<List<RestaurantPOJO>> getNearbyRestaurants (@Path("lat") double lat, @Path("lng") double lng,
                                                           @Path("radius") int radius, @Path("key") String key);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


}
