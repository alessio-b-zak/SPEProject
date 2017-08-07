package com.epimorphics.android.myrivers.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Stefan on 08/02/2017.
 */

public class Point implements ClusterItem {
    private double longitude;
    private double latitude;
    private String title;
    private String snippet;

    public Point(double latitude, double longitude, String title, String snippet) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.snippet = snippet;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public LatLng getPosition() {
        return (new LatLng(latitude, longitude));
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
