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
    INVS("Invasive species"),
    NA("No tag set");

    private String text;

    PhotoTag(String text) {
        this.text = text;
    }

    public static PhotoTag fromString(String text) {
        for (PhotoTag pt : PhotoTag.values()) {
            if (pt.text.equalsIgnoreCase(text)) {
                return pt;
            }
        }
        return PhotoTag.NA;
    }
    
}




