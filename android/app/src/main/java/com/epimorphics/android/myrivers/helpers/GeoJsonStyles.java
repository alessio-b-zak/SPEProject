package com.epimorphics.android.myrivers.helpers;

import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

/**
 * Contains default styles for GeoJsonFeatures
 */
public class GeoJsonStyles {

    /**
     * @return GeoJsonLineStringStyle
     */
    public static GeoJsonLineStringStyle geoJsonLineStringStyle() {
        GeoJsonLineStringStyle style = new GeoJsonLineStringStyle();
        style.setColor(0xCC19A1F9);
        style.setClickable(true);
        return style;
    }

    /**
     * @return GeoJsonPolygonStyle
     */
    public static GeoJsonPolygonStyle geoJsonPolygonStyle() {
        GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
        style.setFillColor(0x664FF2EA);
        style.setStrokeColor(0xCC38B7B1);
        return style;
    }

}
