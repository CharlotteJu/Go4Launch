package com.example.go4lunch.model;

import java.util.ArrayList;
import java.util.List;

public abstract class GenerateTests
{
    private static List<Restaurant> restaurants;

    private static Restaurant restaurant1 = new Restaurant("restaurant1", "français", "12 rue de Ménilmontant", 15, "https://www.google.com/imgres?imgurl=https%3A%2F%2Fwww.basque-immobilier.com%2Fwp-content%2Fuploads%2F2019%2F03%2Fplace-category-cover-pictures-restaurant.jpg&imgrefurl=https%3A%2F%2Fwww.basque-immobilier.com%2Fla-semaine-des-restaurants%2F&tbnid=26uGfc0y7-t-DM&vet=10CAMQxiAoAGoXChMIsPnlpKj85wIVAAAAAB0AAAAAEAY..i&docid=jpf9s6qM1HwM1M&w=1280&h=720&itg=1&q=restaurant%20image&client=safari&ved=0CAMQxiAoAGoXChMIsPnlpKj85wIVAAAAAB0AAAAAEAY");
    private static Restaurant restaurant2 = new Restaurant("restaurant2", "asiatique", "12 rue de Bellevue", 13, "https://www.google.com/imgres?imgurl=http%3A%2F%2Fwww.wapi.cd%2Fuploads%2Fimages%2Frest_1.jpg&imgrefurl=http%3A%2F%2Fwww.wapi.cd%2Findex.php%2Ffr%2Fads%2F5b51d3e3d3e4f%2FRestaurants%2FCHEZ-BINTOU&tbnid=M1EvmDZxkcW6fM&vet=10CAcQxiAoBWoXChMIsPnlpKj85wIVAAAAAB0AAAAAEAY..i&docid=NJ7T_uMUoGalEM&w=440&h=293&itg=1&q=restaurant%20image&client=safari&ved=0CAcQxiAoBWoXChMIsPnlpKj85wIVAAAAAB0AAAAAEAY");

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
