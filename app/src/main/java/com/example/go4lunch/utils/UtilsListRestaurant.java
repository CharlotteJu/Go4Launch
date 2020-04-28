package com.example.go4lunch.utils;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class UtilsListRestaurant
{

    /**
     * Update star's visibility with Restaurant's rating
     */
    public static void updateRating(ImageView star1, ImageView star2, ImageView star3, Restaurant restaurant)
    {
        double rating = restaurant.getRating();

        if (rating > 3.75)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);

        }
        else if (rating > 2.5)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.INVISIBLE);

        }
        else if (rating > 1.25)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        }
        else
        {
            star1.setVisibility(View.INVISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Sort the restaurants according to their distance from the CurrentUser
     */
    public static void sortProximity(List<Restaurant> restaurantList)
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Integer o1DistanceCurrentUser = o1.getDistanceCurrentUser();
            Integer o2DistanceCurrentUser = o2.getDistanceCurrentUser();

            return o1DistanceCurrentUser.compareTo(o2DistanceCurrentUser);
        });
    }

    /**
     * Sort the restaurants according to their rating
     */
    public static void sortRatingReverse(List<Restaurant> restaurantList)
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Double o1Rating = o1.getRating();
            Double o2Rating = o2.getRating();

            return o1Rating.compareTo(o2Rating);
        });

        Collections.reverse(restaurantList);
    }

    /**
     * Sort the restaurants according to their name
     */
    public static void sortName(List<Restaurant> restaurantList)
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            String o1Name = o1.getName();
            String o2Name = o2.getName();

            return o1Name.compareTo(o2Name);
        });
    }

    /**
     * Update the attribute DistanceCurrentUser for each restaurant
     */
    public static void updateDistanceToCurrentLocation(Location currentLocation, List<Restaurant> restaurantList)
    {
        //Location restaurantLocation = new Location("fusedLocationProvider");
        int size = restaurantList.size();
        for (int i = 0; i < size; i++)
        {
           //TODO : Obligé de passer par une méthode en dessous ? Pas plus de travail ?

            int distanceLocation = (int) updateDistanceForOneRestaurant(currentLocation, restaurantList.get(i));
            restaurantList.get(i).setDistanceCurrentUser(distanceLocation);

            /*//Get the restaurant's location
            restaurantLocation.setLatitude(restaurantList.get(i).getLocation().getLat());
            restaurantLocation.setLongitude(restaurantList.get(i).getLocation().getLng());
            //Get the distance between currentLocation and restaurantLocation
            int distanceLocation = (int) currentLocation.distanceTo(restaurantLocation);

            restaurantList.get(i).setDistanceCurrentUser(distanceLocation);*/
        }
    }

    public static float updateDistanceForOneRestaurant (Location currentLocation, Restaurant restaurant)
    {
        Location restaurantLocation = new Location("fusedLocationProvider");
        //Get the restaurant's location
        restaurantLocation.setLatitude(restaurant.getLocation().getLat());
        restaurantLocation.setLongitude(restaurant.getLocation().getLng());
        //Get the distance between currentLocation and restaurantLocation
        return (int) currentLocation.distanceTo(restaurantLocation);
    }



}
