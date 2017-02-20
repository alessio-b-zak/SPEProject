package com.bitbusters.android.speproject;

/**
 * Created by toddym42 on 20/02/2017.
 */

public enum PhotoTag {
    BEC("Bank erosion (cattle poaching)"),
    BED("Bank erosion (dog sliding)"),
    OS("Overshading"),
    US("Undershaded"),
    UF("Unfenced"),
    OBSTR("Obstructions"),
    VSOP("Visible signs of pollution"),
    LOBZ("Lack of \"buffer zone\""),
    INVS("Invasive species");

    private String text;

    PhotoTag(String text) {
        this.text = text;
    }
    
}




