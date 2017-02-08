package com.bitbusters.android.speproject;

/**
 * Created by Stefan on 08/02/2017.
 */

public class PicturePoint extends Point {

    private int id;

    public PicturePoint(double latitude, double longitude, int id){
        super(latitude,longitude,"Picture_Point", "");
        this.id = id;
    }

    public int getId(){
        return id;
    }
}


