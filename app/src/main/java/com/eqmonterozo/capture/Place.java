package com.eqmonterozo.capture;


public class Place {
    String placeName, description, images, latitude, longitude, address, timestamp, addedBy;


    public Place(String placeName, String description, String images, String latitude, String longitude, String address, String timestamp, String addedBy) {
        this.placeName = placeName;
        this.description = description;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.timestamp = timestamp;
        this.addedBy = addedBy;
    }


    @Override
    public String toString() {
        return "{" +
                "place_name:" + "\"" + placeName + "\"" +
                ", description:" + "\"" + description + "\"" +
                ", images:" + "\"" + images + "\"" +
                ", latitude:" + "\"" + latitude + "\"" +
                ", longitude:" + "\"" + longitude + "\"" +
                ", address:" + "\"" + address + "\"" +
                ", timestamp:" + "\"" + timestamp + "\"" +
                ", added_by:" + "\"" + addedBy + "\"" +
                "}";
    }
}
