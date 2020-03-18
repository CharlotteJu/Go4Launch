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
    /**
     * Request HTTP in Json to have nearby Restaurants
     * @param location String with latitude and longitude of the current User
     * @param radius double to define the distance around the current User
     * @param type String to matches with Restaurant
     * @param key String API key
     * @return an Observable<RestaurantPOJO>
     */
    @GET("nearbysearch/json?")
    Observable<RestaurantPOJO> getNearbyRestaurants (@Query("location") String location,
                                                           @Query("radius") int radius, @Query ("type") String type, @Query("key") String key);


    /**
     * Request HTTP in Json to have the Restaurant's Details
     * @param placeId String provide by API Google
     * @param key String API key
     * @return an Observable<DetailPOJO>
     */
    @GET("details/json?")
    Observable<DetailPOJO> getDetailRestaurants (@Query("place_id") String placeId, @Query("key") String key);


    /**
     * Create an instance of Retrofit with the base url of API Google
     */
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
