package com.bitbusters.android.speproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

    public String text;

    ImageTag(String text) {
        this.text = text;
    }

    private static final String TAG = "IMAGE_TAG";

    public static ImageTag fromString(String text) {
        for (ImageTag pt : ImageTag.values()) {
            if (pt.text.equalsIgnoreCase(text)) {
                return pt;
            }
        }
        return ImageTag.NA;
    }

    public static ImageTag fromKey(String text) {
        for (ImageTag pt : ImageTag.values()) {
            if (pt.name().equalsIgnoreCase(text)) {
                return pt;
            }
        }
        return ImageTag.NA;
    }

    public static List<String> getAllTags() {
        List<String> allTags = new ArrayList<>();
        for (ImageTag imageTag : ImageTag.values()) {
            allTags.add(imageTag.text);
        }
        return allTags;
    }

    @Override
    public String toString() {
        return text;
    }


}




