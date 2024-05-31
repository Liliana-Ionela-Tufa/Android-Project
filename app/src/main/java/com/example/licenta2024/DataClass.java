package com.example.licenta2024;

import java.util.ArrayList;

public class DataClass {

    private String dataName;
    private String dataDescription;
    private String dataType;
    private String dataCity;
    private String dataCountry;
    private String latitude;
    private String longitude;
    private String imageURL;

    private  String uuid;

    private  String user;

    private String firstName, lastName, phone;

    private String attractionID;
    private String userID;
    private String reviewID;
    private String title;
    private String review;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    private ArrayList<String> picturesURL;

    public DataClass( String userID) {
        this.userID = userID;
    }

    public DataClass(String attractionID, String userID, String reviewID, String title, String review, ArrayList<String> picturesURL, String date) {
        this.attractionID = attractionID;
        this.userID = userID;
        this.reviewID = reviewID;
        this.title = title;
        this.review = review;
        this.picturesURL = picturesURL;
        this.date =date;
    }

    public String getAttractionID() {
        return attractionID;
    }

    public void setAttractionID(String attractionID) {
        this.attractionID = attractionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public ArrayList<String> getPicturesURL() {
        return picturesURL;
    }

    public void setPicturesURL(ArrayList<String> picturesURL) {
        this.picturesURL = picturesURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DataClass(String dataName, String dataDescription, String dataType, String dataCity, String dataCountry, String latitude, String longitude, String imageURL, String uuid, String user) {
        this.dataName = dataName;
        this.dataDescription = dataDescription;
        this.dataType = dataType;
        this.dataCity = dataCity;
        this.dataCountry = dataCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
        this.uuid = uuid;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DataClass(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataDescription() {
        return dataDescription;
    }

    public void setDataDescription(String dataDescription) {
        this.dataDescription = dataDescription;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataCity() {
        return dataCity;
    }

    public void setDataCity(String dataCity) {
        this.dataCity = dataCity;
    }

    public String getDataCountry() {
        return dataCountry;
    }

    public void setDataCountry(String dataCountry) {
        this.dataCountry = dataCountry;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public DataClass(String dataName, String dataDescription, String dataType, String dataCity, String dataCountry, String latitude, String longitude, String imageURL) {
        this.dataName = dataName;
        this.dataDescription = dataDescription;
        this.dataType = dataType;
        this.dataCity = dataCity;
        this.dataCountry = dataCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
    }


    public DataClass(String dataName, String dataType, String dataCity, String imageURL) {
        this.dataName = dataName;
        this.dataType = dataType;
        this.dataCity = dataCity;
        this.imageURL = imageURL;
    }

    public DataClass(String dataName, String dataDescription, String dataType, String dataCity, String dataCountry, String latitude, String longitude, String imageURL, String uuid) {
        this.dataName = dataName;
        this.dataDescription = dataDescription;
        this.dataType = dataType;
        this.dataCity = dataCity;
        this.dataCountry = dataCountry;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
        this.uuid = uuid;
    }
}
