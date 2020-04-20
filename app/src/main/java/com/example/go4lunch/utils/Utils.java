package com.example.go4lunch.utils;

import android.location.Location;
import android.view.View;
import android.widget.ImageView;

import com.example.go4lunch.model.Restaurant;

import java.util.Collections;
import java.util.List;

public abstract class Utils
{

    /**
     * Update star's visibility with rating
     * @param restaurant
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

    public static void triProximity (List<Restaurant> restaurantList)
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Integer o1DistanceCurrentUser = o1.getDistanceCurrentUser();
            Integer o2DistanceCurrentUser = o2.getDistanceCurrentUser();

            return o1DistanceCurrentUser.compareTo(o2DistanceCurrentUser);
        });
    }

    public static void triRatingReverse(List<Restaurant> restaurantList)
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Double o1Rating = o1.getRating();
            Double o2Rating = o2.getRating();

            return o1Rating.compareTo(o2Rating);
        });

        Collections.reverse(restaurantList);
    }

    public static void triName (List<Restaurant> restaurantList)
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

        Location restaurantLocation = new Location("fusedLocationProvider");

        for (int i = 0; i < restaurantList.size(); i++)
        {
            //Get the restaurant's location
            restaurantLocation.setLatitude(restaurantList.get(i).getLocation().getLat());
            restaurantLocation.setLongitude(restaurantList.get(i).getLocation().getLng());
            //Get the distance between currentLocation and restaurantLocation
            int distanceLocation = (int) currentLocation.distanceTo(restaurantLocation);

            restaurantList.get(i).setDistanceCurrentUser(distanceLocation);
        }
    }


}
