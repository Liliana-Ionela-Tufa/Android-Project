package com.example.licenta2024;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataAttractionAvailability implements Parcelable {
    private String attractionID;
    private String name;
    private String availability;
    private String city;
    private boolean isChecked;
    private Double latitude, longitude;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;


    public DataAttractionAvailability(String attractionID, String date)
    {
        this.attractionID = attractionID;
        this.date = date;
    }
    public DataAttractionAvailability(String attractionID, String name, String availability, String city, Double latitude, Double longitude, boolean isChecked) {
        this.attractionID = attractionID;
        this.name = name;
        this.availability = availability;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isChecked = isChecked;
    }

    protected DataAttractionAvailability(Parcel in) {
        attractionID = in.readString();
        name = in.readString();
        availability = in.readString();
        city = in.readString();
        isChecked = in.readByte() != 0;
        latitude = in.readByte() == 0 ? null : in.readDouble();
        longitude = in.readByte() == 0 ? null : in.readDouble();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(attractionID);
        parcel.writeString(name);
        parcel.writeString(availability);
        parcel.writeString(city);
        parcel.writeByte((byte) (isChecked ? 1 : 0));
        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(latitude);
        }
        if (longitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(longitude);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataAttractionAvailability> CREATOR = new Creator<DataAttractionAvailability>() {
        @Override
        public DataAttractionAvailability createFromParcel(Parcel in) {
            return new DataAttractionAvailability(in);
        }

        @Override
        public DataAttractionAvailability[] newArray(int size) {
            return new DataAttractionAvailability[size];
        }
    };

    // Getters and setters...
    public String getAttractionID() {
        return attractionID;
    }

    public void setAttractionID(String attractionID) {
        this.attractionID = attractionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

