package com.example.go4lunch.model.api;


import android.location.Location;

import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RestaurantStreams {

    private static final String type = "restaurant";
    private static final Boolean openingHoursBoolean = true;
    private static List<Restaurant> restaurants = new ArrayList<>();

    /**
     * Add the Request HTTP with Retrofit to have nearby Restaurant
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @param key String API key
     * @return an Observable<RestaurantPOJO>
     */
    public static Observable<RestaurantPOJO> streamFetchRestaurant (double lat, double lng, int radius, String key)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);
        String location = lat + "," + lng;

        return restaurantPlacesApi.getNearbyRestaurants(location, radius, type, openingHoursBoolean, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    /**
     * Add the Request HTTP with Retrofit to have Restaurant's Detail
     * @param placeId String provide by API Google
     * @param key String API key
     * @return an Observable<DetailPOJO>
     */
    public static Observable<DetailPOJO> streamDetailRestaurant(String placeId, String key)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);

        return restaurantPlacesApi.getDetailRestaurants(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }

    public static Observable<Restaurant> streamDetailRestaurantToRestaurant(String placeId, String key)
    {
        return streamDetailRestaurant(placeId, key)
                .map(detailPOJO -> {
                    String name = detailPOJO.getResult().getName();
                    String address = detailPOJO.getResult().getVicinity();
                    String photo = getPhoto(detailPOJO.getResult().getPhotos().get(0).getPhotoReference(), 400, key);
                    String placeId1 = detailPOJO.getResult().getPlaceId();
                    double rating = detailPOJO.getResult().getRating();
                    DetailPOJO.OpeningHours openingHours = detailPOJO.getResult().getOpeningHours();
                    String phoneNumber = detailPOJO.getResult().getInternationalPhoneNumber();
                    String website = detailPOJO.getResult().getWebsite();

                    Restaurant restaurant = new Restaurant(name, address, photo, placeId1, rating, openingHours, phoneNumber, website);
                    return restaurant;
                });
    }

    /**
     * Transform first Observable<RestaurantPOJO> in Observable<List<Restaurants>
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @param key String API key
     * @return an Observable<List<Restaurants>
     */
    public static Observable<List<Restaurant>> streamFetchRestaurantInList(double lat, double lng, int radius, String key)
    {
        return streamFetchRestaurant(lat, lng, radius, key)
                .map(restaurantPOJO -> {

                    restaurants = new ArrayList<>();
                    List<RestaurantPOJO.Result> res = restaurantPOJO.getResults();

                    for (int i = 0; i < res.size(); i ++)
                    {
                        String name = res.get(i).getName();
                        String address = res.get(i).getVicinity();
                        String photo;
                        String placeId = res.get(i).getPlaceId();
                        double rating = res.get(i).getRating();
                        Boolean openNow;
                        RestaurantPOJO.Location location = res.get(i).getGeometry().getLocation();

                        if (res.get(i).getPhotos() != null)
                        {
                            photo = getPhoto(res.get(i).getPhotos().get(0).getPhotoReference(), 400, key);
                        }
                        else
                        {
                            photo = "";
                        }

                        if (res.get(i).getOpeningHours() != null)
                        {
                            openNow = res.get(i).getOpeningHours().getOpenNow();
                        }
                        else
                        {
                            openNow = false;
                        }


                        Restaurant restaurant = new Restaurant(name, address, photo, placeId, rating, openNow, location);
                        restaurants.add(restaurant);
                    }

                    return restaurants;
                });
    }


    /**
     * Add the two streams to have on Request HTTP for nearby and Details
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @param key String API key
     * @return an Observable<List<Restaurants>
     */
    public static Observable<List<Restaurant>> streamRestaurantListFinal(double lat, double lng, int radius, String key)
    {
        return  streamFetchRestaurantInList(lat, lng, radius, key)
                .flatMapIterable(restaurantList -> restaurants)
                .flatMap(restaurant -> streamDetailRestaurant(restaurant.getPlaceId(), key))
                .toList()
                .map(detailPOJOS -> {
                    restaurants.clear();
                    for (int i = 0; i < detailPOJOS.size(); i ++)
                    {
                        String name = detailPOJOS.get(i).getResult().getName();
                        String address = detailPOJOS.get(i).getResult().getVicinity();
                        String photo = getPhoto(detailPOJOS.get(i).getResult().getPhotos().get(0).getPhotoReference(), 400, key);
                        String placeId = detailPOJOS.get(i).getResult().getPlaceId();
                        double rating = detailPOJOS.get(i).getResult().getRating();
                        DetailPOJO.OpeningHours openingHours = detailPOJOS.get(i).getResult().getOpeningHours();
                        String phoneNumber = detailPOJOS.get(i).getResult().getInternationalPhoneNumber();
                        String website = detailPOJOS.get(i).getResult().getWebsite();

                        Restaurant restaurant = new Restaurant(name, address, photo, placeId, rating, openingHours, phoneNumber, website);
                        restaurants.add(restaurant);
                    }
                    return restaurants;
                })
                .toObservable();
    }


    /**
     * Have the Restaurant's Url Photo
     * @param photoReference String photoReference provide by API Google
     * @param maxWidth int to give the same width for all photos
     * @param key String API key
     * @return
     */
    private static String getPhoto(String photoReference, int maxWidth, String key)
    {
        return "https://maps.googleapis.com/maps/api/place/photo?" + "photoreference=" + photoReference
                + "&maxwidth=" + maxWidth + "&key=" + key;
    }


}
