package com.bitbusters.android.speproject;
import android.graphics.Bitmap;


/**
 * Created by cp153 on 06/12/2016.
 */

class ImageLocation {
    private double latitude;
    private double longitude;
    private String id;

    ImageLocation(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
