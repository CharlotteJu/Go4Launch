package com.example.go4lunch.model;

import android.location.Location;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Restaurant
{
    private String name;
    private String address;
    private int numberReservation;
    private double rating;
    private String illustration;
    private String placeId;
    private DetailPOJO.OpeningHours openingHours;
    private List<User> userList;
    private String phoneNumber;
    private String website;
    private Boolean openNow;
    private RestaurantPOJO.Location location;

    //////// CONSTRUCTOR ////////

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

    public Restaurant ()
    {
        this.openingHours = new DetailPOJO.OpeningHours();
    }

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

    public Restaurant (String placeId, List<User> userList, String name)
    {
        this.placeId = placeId;
        this.userList = userList;
        this.name = name;
    }

    //////// GETTERS ////////


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getNumberReservation() {
        return numberReservation;
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

    //////// SETTERS ////////


    public void setName(String name) {
        this.name = name;
    }

    public void setAdress(String address) {
        this.address = address;
    }

    public void setNumberReservation(int numberRating) {
        this.numberReservation = numberRating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setOpeningHours(DetailPOJO.OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public void setLocation(RestaurantPOJO.Location location) {
        this.location = location;
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

        //return this.getPlaceId().equals(((Restaurant) obj).getPlaceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPlaceId());
    }
}
