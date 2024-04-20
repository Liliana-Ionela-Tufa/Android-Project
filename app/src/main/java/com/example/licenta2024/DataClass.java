package com.example.licenta2024;

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
