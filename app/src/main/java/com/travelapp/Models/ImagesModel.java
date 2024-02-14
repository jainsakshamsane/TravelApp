package com.travelapp.Models;

public class ImagesModel {

    String username, imagepath, timestamp, id;

    public ImagesModel(String username, String imagepath, String timestamp, String id) {
        this.username = username;
        this.imagepath = imagepath;
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
