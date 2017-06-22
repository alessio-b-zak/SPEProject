package com.bitbusters.android.speproject;

/**
 * Created by toddym42 on 20/02/2017.
 */

public enum ImageTag {
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

    ImageTag(String text) {
        this.text = text;
    }



    public static ImageTag fromString(String text) {
        for (ImageTag pt : ImageTag.values()) {
            if (pt.name().equalsIgnoreCase(text)) {
                return pt;
            }
        }
        return ImageTag.NA;
    }


    @Override
    public String toString() {
        return text;
    }


}




