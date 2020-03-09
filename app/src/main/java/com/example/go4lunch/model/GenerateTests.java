package com.example.go4lunch.model;

import java.util.ArrayList;
import java.util.List;

public abstract class GenerateTests
{
    private static List<Restaurant> restaurants;

    private static Restaurant restaurant1 = new Restaurant("restaurant1", "français", "12 rue de Ménilmontant", 15, "https://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png");
    private static Restaurant restaurant2 = new Restaurant("restaurant2", "asiatique", "12 rue de Bellevue", 13, "https://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png");

    public static List<Restaurant> getRestaurants ()
    {
        restaurants = new ArrayList<>();
        restaurants.add(restaurant1);
        restaurants.add(restaurant2);
        return restaurants;
    }

    private static List<User> users;

    private static User user1 = new User("user1", "user1@gmail.com", "www.google.com");
    private static User user2 = new User("user2", "user2@gmail.com", "www.google.com");

    public static List<User> getUsers ()
    {
        users = new ArrayList<>();
        users.add(user1);
        user2.setRestaurantChoose(restaurant1);
        users.add(user2);

        return users;
    }



}
