package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User
{
    private String name;
    private String email;
    private String illustration;
    private boolean chooseRestaurant;
    private Restaurant restaurantChoose;
    private List<Restaurant> restaurantListFavorites;

    //////// CONSTRUCTORS ////////

    /**
     * Empty constructor for Firebase
     */
    public User() {}

    /**
     * Constructor to create an User in Firebase
     */
    public User(String email, String name,  String illustration) {
        this.name = name;
        this.email = email;
        this.illustration = illustration;

        this.chooseRestaurant = false;
        this.restaurantListFavorites = new ArrayList<>();
    }


    //////// GETTERS ////////

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getIllustration() {
        return illustration;
    }

    public boolean isChooseRestaurant() {
        return chooseRestaurant;
    }

    public List<Restaurant> getRestaurantListFavorites() {
        return restaurantListFavorites;
    }

    public Restaurant getRestaurantChoose() {
        return restaurantChoose;
    }

    //////// SETTERS ////////


    public void setName(String name) {
        this.name = name;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public void setRestaurantChoose(Restaurant restaurantChoose) {
        this.restaurantChoose = restaurantChoose;
        this.chooseRestaurant = true;
    }

    public void unSetRestaurantChoose()
    {
        this.restaurantChoose = null;
        this.chooseRestaurant = false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getEmail());
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || obj.getClass() != getClass())
        {
            return false;
        }

        return Objects.equals(this.getEmail(), ((User) obj).getEmail());
    }
}
