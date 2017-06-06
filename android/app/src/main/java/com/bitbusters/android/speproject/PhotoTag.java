package com.bitbusters.android.speproject;

/**
 * Created by toddym42 on 20/02/2017.
 */

public enum PhotoTag {
    NA("No tag set"),
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



    public static PhotoTag fromString(String text) {
        for (PhotoTag pt : PhotoTag.values()) {
            if (pt.name().equalsIgnoreCase(text)) {
                return pt;
            }
        }
        return PhotoTag.NA;
    }


    @Override
    public String toString() {
        return text;
    }


}




