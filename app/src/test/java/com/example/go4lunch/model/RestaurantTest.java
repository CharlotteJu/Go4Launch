package com.example.go4lunch.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RestaurantTest
{
    private String placeIdTest = "placeIdTest";
    private List<User> listTest = new ArrayList<>();
    private String nameTest = "NameTest";
    private String addressTest = "123 Street";

    Restaurant restaurantForTest = new Restaurant(placeIdTest, listTest, nameTest, addressTest);

    @Test
    public void RestaurantEquals_False()
    {
        // GIVEN : Create a new restaurant without the same placeId
        Restaurant restaurantToCompare = new Restaurant("placeId2", listTest, nameTest, addressTest);

        // THEN : The 2 restaurants are not equals
        assertNotEquals(restaurantForTest, restaurantToCompare);
    }

    @Test
    public void RestaurantEquals_True()
    {
        // GIVEN : Create a new restaurant without the same placeId
        Restaurant restaurantToCompare = new Restaurant(placeIdTest, listTest, "Name2", "Address2");

        // THEN : The 2 restaurants are not equals
        assertEquals(restaurantForTest, restaurantToCompare);
    }
}