package com.example.go4lunch.view_model.repositories;



import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;
import com.example.go4lunch.api.RestaurantPlacesApi;


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
    private static final String NO_RESTAURANT = "NO_RESTAURANT";

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
                .map(detailPOJO ->
                {
                    // Verify is that is a Restaurant for Place Autocomplete
                    List<String> types = detailPOJO.getResult().getTypes();
                    boolean isRestaurant = false;
                    int typesSize = types.size();
                    for (int i = 0; i < typesSize; i ++)
                    {
                        if (types.get(i).equals(type))
                        {
                            isRestaurant = true;
                            break;
                        }
                    }
                    if (!isRestaurant)
                    {
                        return new Restaurant(NO_RESTAURANT);
                    }

                    // If it's a restaurant, configure an Object Restaurant
                    String placeId1 = detailPOJO.getResult().getPlaceId();
                    String name = (detailPOJO.getResult().getName() != null ? detailPOJO.getResult().getName() : "");
                    String address = (detailPOJO.getResult().getVicinity() != null ? detailPOJO.getResult().getVicinity() : "");
                    double rating = (detailPOJO.getResult().getRating() != null ? detailPOJO.getResult().getRating() : 0);
                    String photo = (detailPOJO.getResult().getPhotos() != null ? getPhoto(detailPOJO.getResult().getPhotos().get(0).getPhotoReference(), 400, key) : "") ;
                    String phoneNumber = (detailPOJO.getResult().getInternationalPhoneNumber() != null ? detailPOJO.getResult().getInternationalPhoneNumber() : "");
                    String website = (detailPOJO.getResult().getWebsite() != null ? detailPOJO.getResult().getWebsite() : "");

                    return new Restaurant(name, address, photo, placeId1, rating, phoneNumber, website);
                });
    }

    @Override
    public Observable<List<Restaurant>> streamFetchRestaurantInList(double lat, double lng, int radius, String key) {
        return streamFetchRestaurant(lat, lng, radius, key)
                .map(restaurantPOJO -> {

                    restaurants = new ArrayList<>();
                    List<RestaurantPOJO.Result> res = restaurantPOJO.getResults();
                    int size = res.size();
                    for (int i = 0; i < size; i ++)
                    {
                        String placeId = res.get(i).getPlaceId();

                        String name = (res.get(i).getName() != null ? res.get(i).getName() : "");
                        String address = (res.get(i).getVicinity() != null ? res.get(i).getVicinity() : "");
                        String photo = (res.get(i).getPhotos() != null ? getPhoto(res.get(i).getPhotos().get(0).getPhotoReference(), 400, key) : "") ;
                        double rating = (res.get(i).getRating() != null ? res.get(i).getRating() : 0);
                        Boolean openNow = (res.get(i).getOpeningHours() != null ? res.get(i).getOpeningHours().getOpenNow() : false);

                        if (res.get(i).getGeometry().getLocation() != null)
                        {
                            RestaurantPOJO.Location location = res.get(i).getGeometry().getLocation();
                            Restaurant restaurant = new Restaurant(name, address, photo, placeId, rating, openNow, location);
                            restaurants.add(restaurant);
                        }
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
