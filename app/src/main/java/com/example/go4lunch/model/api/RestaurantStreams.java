package com.example.go4lunch.model.api;


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

    private static String type = "restaurant";
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

        return restaurantPlacesApi.getNearbyRestaurants(location, radius, type, key)
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
                .map(new Function<RestaurantPOJO, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(RestaurantPOJO restaurantPOJO) throws Exception {

                        List<RestaurantPOJO.Result> res = restaurantPOJO.getResults();

                        for (int i = 0; i < res.size(); i ++)
                        {
                            String placeId = res.get(i).getPlaceId();
                            Restaurant restaurant = new Restaurant(placeId);
                            restaurants.add(restaurant);
                        }

                        return restaurants;
                    }
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
                .map(new Function<List<DetailPOJO>, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(List<DetailPOJO> detailPOJOS) throws Exception {
                        restaurants.clear();
                        for (int i = 0; i < detailPOJOS.size(); i ++)
                        {
                            String name = detailPOJOS.get(i).getResult().getName();
                            String type = detailPOJOS.get(i).getResult().getTypes().get(0);
                            String address = detailPOJOS.get(i).getResult().getVicinity();
                            String photo = getPhoto(detailPOJOS.get(i).getResult().getPhotos().get(0).getPhotoReference(), 400, key);
                            String placeId = detailPOJOS.get(i).getResult().getPlaceId();
                            DetailPOJO.OpeningHours openingHours = detailPOJOS.get(i).getResult().getOpeningHours();

                            Restaurant restaurant = new Restaurant(name, type, address, photo, placeId);
                            restaurant.setOpeningHours(openingHours);
                            restaurants.add(restaurant);
                        }
                        return restaurants;
                    }
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
