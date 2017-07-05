package com.bitbusters.android.speproject;

import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import static com.bitbusters.android.speproject.CDEPoint.GOOD;
import static com.bitbusters.android.speproject.CDEPoint.MODERATE;
import static com.bitbusters.android.speproject.CDEPoint.OVERALL;
import static com.bitbusters.android.speproject.CDEPoint.POOR;

/**
 * Created by mihajlo on 03/07/17.
 */

public class GeoJsonStyles {

    public static GeoJsonLineStringStyle geoJsonLineStringStyle(CDEPoint cdePoint) {
        GeoJsonLineStringStyle style = new GeoJsonLineStringStyle();
//        switch(cdePoint.getClassificationHashMap().get(OVERALL).getValue()) {
//            case(POOR):
//                style.setColor(0x66f94545);
//                break;
//            case(MODERATE):
//                style.setColor(0x66ccf945);
//                break;
//            case(GOOD):
//                style.setColor(0x664ff2ea);
//                break;
//        }
        style.setColor(0x664ff2ea);
        style.setClickable(true);
        return style;
    }

    public static GeoJsonPolygonStyle geoJsonPolygonStyle(CDEPoint cdePoint) {
        GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
//        switch(cdePoint.getClassificationHashMap().get(OVERALL).getValue()) {
//            case(POOR):
//                style.setFillColor(0x66f94545);
//                style.setStrokeColor(0xccc93030);
//                break;
//            case(MODERATE):
//                style.setFillColor(0x66ccf945);
//                style.setStrokeColor(0xccb5dd3b);
//                break;
//            case(GOOD):
//                style.setFillColor(0x664ff2ea);
//                style.setStrokeColor(0xcc38b7b1);
//                break;
//        }
        style.setFillColor(0x664ff2ea);
        style.setStrokeColor(0xcc38b7b1);
        return style;
    }

}
