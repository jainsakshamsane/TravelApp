package com.travelapp.Models;

public class TransactionModel {
    private String paymentId;
    private String userId;
    private String placeName;
    private String numberOfPeople;
    private String card_id;
    private String date;
    private String id;

    public TransactionModel(String placename, String userId) {
        this.placeName = placename;
        this.userId = userId;
    }

    public TransactionModel(String placename, String userId, String date, String card_id, String id) {
        this.placeName = placename;
        this.userId = userId;
        this.date = date;
        this.card_id = card_id;
        this.id = id;
    }

    public TransactionModel(String placename, String userId, String placeid, String people) {
        this.placeName = placename;
        this.userId = userId;
        this.id = placeid;
        this.numberOfPeople = people;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }


}
