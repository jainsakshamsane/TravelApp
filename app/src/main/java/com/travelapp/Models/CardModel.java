package com.travelapp.Models;

import java.util.List;

public class CardModel {
    private String card_id;
    private String userid;
    private String expiry_date;
    private String cvv;
    private String card_number;
    private String total_price;

    public CardModel(String cardnumber, String expirydate, String total_price, String userid) {
        this.card_number = cardnumber;
        this.expiry_date = expirydate;
        this.total_price = total_price;
        this.userid = userid;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }
}
