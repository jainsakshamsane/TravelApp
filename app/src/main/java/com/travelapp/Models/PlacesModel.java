package com.travelapp.Models;

public class PlacesModel {
    private String id;
    private String best_place;
    private String name;
    private String city;
    private String country;
    private String date_start;
    private String date_end;
    private String image;
    private String information;
    private String no_of_days;
    private String price;
    private String season;
    String month;
    String service;
    String temprature;

    public PlacesModel(String placename, String city, String country, String price, String image) {
        this.name = placename;
        this.city = city;
        this.country = country;
        this.price = price;
        this.image = image;
    }

    public PlacesModel(String placename, String city, String country, String price, String image, String noofdays, String season,String id) {
        this.name = placename;
        this.city = city;
        this.country = country;
        this.price = price;
        this.image = image;
        this.no_of_days = noofdays;
        this.season = season;
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTemprature() {
        return temprature;
    }

    public void setTemprature(String temprature) {
        this.temprature = temprature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getNo_of_days() {
        return no_of_days;
    }

    public void setNo_of_days(String no_of_days) {
        this.no_of_days = no_of_days;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getBest_place() {
        return best_place;
    }

    public void setBest_place(String best_place) {
        this.best_place = best_place;
    }
}
