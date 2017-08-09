package com.epimorphics.android.myrivers.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Class used to represent points on the Google Map. Implements ClusterItem
 *
 * @see ClusterItem
 */
public class Point implements ClusterItem {
    private double longitude;
    private double latitude;
    private String title;
    private String snippet;

    /**
     * Data constractor which initiates all the properties
     *
     * @param latitude latitude
     * @param longitude longitude
     * @param title title
     * @param snippet snippet
     */
    public Point(double latitude, double longitude, String title, String snippet) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.snippet = snippet;
    }

    /**
     * @param longitude longitude of a point
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @param latitude latitude of a point
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return LatLng position of a point
     */
    @Override
    public LatLng getPosition() {
        return (new LatLng(latitude, longitude));
    }

    /**
     * @return String title of a point
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @return String snippet of a point
     */
    @Override
    public String getSnippet() {
        return snippet;
    }
}
