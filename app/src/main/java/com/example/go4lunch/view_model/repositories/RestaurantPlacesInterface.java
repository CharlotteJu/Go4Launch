package com.example.go4lunch.view_model.repositories;

import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;

import java.util.List;

import io.reactivex.Observable;

public interface RestaurantPlacesInterface
{
    /**
     * Add the Request HTTP with Retrofit to have nearby Restaurant
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @param key String API key
     * @return an Observable<RestaurantPOJO>
     */
    Observable<RestaurantPOJO> streamFetchRestaurant(double lat, double lng, int radius, String key);

    /**
     * Add the Request HTTP with Retrofit to have Restaurant's Detail
     * @param placeId String provide by API Google
     * @param key String API key
     * @return an Observable<DetailPOJO>
     */
    Observable<DetailPOJO> streamDetailRestaurant(String placeId, String key);

    /**
     * Transform first Observable<DetailPOJO> in Observable<Restaurant>
     * @param key String API key
     * @return an Observable<Restaurant>
     */
    Observable<Restaurant> streamDetailRestaurantToRestaurant(String placeId, String key);

    /**
     * Transform first Observable<RestaurantPOJO> in Observable<List<Restaurants>
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @param key String API key
     * @return an Observable<List<Restaurants>
     */
    Observable<List<Restaurant>> streamFetchRestaurantInList(double lat, double lng, int radius, String key);

    /**
     * Have the Restaurant's Url Photo
     * @param photoReference String photoReference provide by API Google
     * @param maxWidth int to give the same width for all photos
     * @param key String API key
     * @return the String URL to the photoReference
     */
    String getPhoto(String photoReference, int maxWidth, String key);
}

