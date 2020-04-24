package com.example.go4lunch.model;


import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Restaurant
{

    private String name;
    private String address;
    private double rating;
    private String illustration;
    private String placeId;
    private DetailPOJO.OpeningHours openingHours;
    private List<User> userList;
    private String phoneNumber;
    private String website;
    private Boolean openNow;
    private RestaurantPOJO.Location location;
    private int distanceCurrentUser;

    //////// CONSTRUCTORS ////////

    public Restaurant(String name, String address, String illustration, String placeId, double rating,
                      DetailPOJO.OpeningHours openingHours, String phoneNumber, String website) {

        this.name = name;
        this.address = address;
        this.illustration = illustration;
        this.placeId = placeId;
        this.rating = rating;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.website = website;


    }

    /**
     * Empty constructor for Firebase
     */
    public Restaurant () {}

    /**
     * Constructor for Places' Request
     */
    public Restaurant(String name, String address, String illustration, String placeId, double rating, Boolean openNow, RestaurantPOJO.Location location)
    {
        this.name = name;
        this.address = address;
        this.illustration = illustration;
        this.placeId = placeId;
        this.rating = rating;
        this.openNow = openNow;
        this.location = location;
    }

    /**
     * Constructor to create a Restaurant in Firebase
     */
    public Restaurant (String placeId, List<User> userList, String name, String address)
    {
        this.placeId = placeId;
        this.userList = userList;
        this.name = name;
        this.address = address;
    }

    //////// GETTERS ////////


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public String getIllustration() {
        return illustration;
    }

    public String getPlaceId() {
        return placeId;
    }

    public DetailPOJO.OpeningHours getOpeningHours() {
        return openingHours;
    }

    public List<User> getUserList() {
        return userList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public RestaurantPOJO.Location getLocation() {
        return location;
    }

    public int getDistanceCurrentUser() {
        return distanceCurrentUser;
    }

    //////// SETTERS ////////


    public void setName(String name) {
        this.name = name;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setLocation(RestaurantPOJO.Location location) {
        this.location = location;
    }

    public void setDistanceCurrentUser(int distanceCurrentUser) {
        this.distanceCurrentUser = distanceCurrentUser;
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

        return Objects.equals(this.getPlaceId(),((Restaurant) obj).getPlaceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPlaceId());
    }

}
