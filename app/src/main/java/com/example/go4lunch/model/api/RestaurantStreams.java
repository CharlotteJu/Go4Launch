package com.example.go4lunch.model.api;


import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;
import com.google.firebase.firestore.WriteBatch;


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

    public static Observable<RestaurantPOJO> streamFetchRestaurant (double lat, double lng, int radius, String key)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);
        String location = lat + "," + lng;

        return restaurantPlacesApi.getNearbyRestaurants(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<DetailPOJO> streamDetailRestaurant(String placeId, String key)
    {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);

        return restaurantPlacesApi.getDetailRestaurants(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }

    //public static Observable

    public static Observable<DetailPOJO> twoStreams(double lat, double lng, int radius, String key, int position)
    {
        return streamFetchRestaurant(lat, lng, radius, key)
                .map(new Function<RestaurantPOJO, Restaurant>() {
                    @Override
                    public Restaurant apply(RestaurantPOJO restaurantPOJO) throws Exception {

                        List<RestaurantPOJO.Result> res = restaurantPOJO.getResults();
                        String name = res.get(position).getName();
                        String type = res.get(position).getTypes().get(0);
                        String address = res.get(position).getVicinity();
                        String photo = getPhoto(res.get(position).getPhotos().get(0).getPhotoReference(), 400, key);
                        String placeId = res.get(position).getPlaceId();
                        Restaurant restaurant = new Restaurant(name, type, address, photo, placeId);



                        return restaurant;
                    }
                })
                .flatMap(new Function<Restaurant, Observable<DetailPOJO>>() {
                    @Override
                    public Observable<DetailPOJO> apply(Restaurant restaurant) throws Exception {
                        return streamDetailRestaurant(restaurant.getPlaceId(), key);
                    }
                });
    }

    public static Observable<List<Restaurant>> test1(double lat, double lng, int radius, String key)
    {
        return streamFetchRestaurant(lat, lng, radius, key)
                .map(new Function<RestaurantPOJO, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(RestaurantPOJO restaurantPOJO) throws Exception {

                        List<RestaurantPOJO.Result> res = restaurantPOJO.getResults();

                        for (int i = 0; i < res.size(); i ++)
                        {
                            String name = res.get(i).getName();
                            String type = res.get(i).getTypes().get(0);
                            String address = res.get(i).getVicinity();
                            String photo = getPhoto(res.get(i).getPhotos().get(0).getPhotoReference(), 400, key);
                            String placeId = res.get(i).getPlaceId();

                            Restaurant restaurant = new Restaurant(name, type, address, photo, placeId);
                            restaurants.add(restaurant);
                        }

                        return restaurants;
                    }
                });
    }




    public static Function<RestaurantPOJO, Observable<DetailPOJO>> twoStreams()
    {
        return new Function<RestaurantPOJO, Observable<DetailPOJO>>() {
            @Override
            public Observable<DetailPOJO> apply(RestaurantPOJO restaurantPOJO) throws Exception {


                return null;
            }
        };
    }

    public static String getPhoto(String photoReference, int maxWidth, String key)
    {
        return "https://maps.googleapis.com/maps/api/place/photo?" + "photoreference=" + photoReference
                + "&maxwidth=" + maxWidth + "&key=" + key;
    }
}
