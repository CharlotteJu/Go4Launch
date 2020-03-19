package com.example.go4lunch.model;

import java.util.List;

public class Restaurant
{
    private String name;
    private String type;
    private String address;
    private int hours;
    private int numberRating;
    private double rating;
    private String illustration;
    private String placeId;
    private DetailPOJO.OpeningHours openingHours;

    //////// CONSTRUCTOR ////////

    public Restaurant(String name, String type, String address, String illustration, String placeId, double rating, DetailPOJO.OpeningHours openingHours) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.illustration = illustration;
        this.placeId = placeId;
        this.rating = rating;
        this.openingHours = openingHours;

        this.numberRating = 0;
    }

    public Restaurant(String placeId) {
        this.placeId = placeId;
    }

    //////// GETTERS ////////


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public int getHours() {
        return hours;
    }

    public int getNumberRating() {
        return numberRating;
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

    //////// SETTERS ////////


    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAdress(String address) {
        this.address = address;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setNumberRating(int numberRating) {
        this.numberRating = numberRating;
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

}
