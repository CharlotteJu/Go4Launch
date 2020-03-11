package com.example.go4lunch.model;

public class Restaurant
{
    private String name;
    private String type;
    private String address;
    private int hours;
    private int numberRating;
    private int rating;
    private String illustration;

    //////// CONSTRUCTOR ////////

    public Restaurant(String name, String type, String address, String illustration) {
        this.name = name;
        this.type = type;
        this.address = address;
        //this.hours = hours;
        this.illustration = illustration;

        this.rating = 0;
        this.numberRating = 0;
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

    public int getRating() {
        return rating;
    }

    public String getIllustration() {
        return illustration;
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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }
}
