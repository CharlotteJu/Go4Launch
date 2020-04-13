package com.example.go4lunch.view_model.repositories;



import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;
import com.example.go4lunch.model.api.RestaurantPlacesApi;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.schedulers.Schedulers;

public class RestaurantPlacesRepository implements RestaurantPlacesInterface{

    private static final String type = "restaurant";
    private static final Boolean openingHoursBoolean = true;
    private static List<Restaurant> restaurants = new ArrayList<>();

    @Override
    public Observable<RestaurantPOJO> streamFetchRestaurant(double lat, double lng, int radius, String key) {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);
        String location = lat + "," + lng;

        return restaurantPlacesApi.getNearbyRestaurants(location, radius, type, openingHoursBoolean, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    @Override
    public Observable<DetailPOJO> streamDetailRestaurant(String placeId, String key) {
        RestaurantPlacesApi restaurantPlacesApi = RestaurantPlacesApi.retrofit.create(RestaurantPlacesApi.class);

        return restaurantPlacesApi.getDetailRestaurants(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }

    @Override
    public Observable<Restaurant> streamDetailRestaurantToRestaurant(String placeId, String key) {
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

    @Override
    public Observable<List<Restaurant>> streamFetchRestaurantInList(double lat, double lng, int radius, String key) {
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

    @Override
    public String getPhoto(String photoReference, int maxWidth, String key) {
        return "https://maps.googleapis.com/maps/api/place/photo?" + "photoreference=" + photoReference
                + "&maxwidth=" + maxWidth + "&key=" + key;
    }

}
