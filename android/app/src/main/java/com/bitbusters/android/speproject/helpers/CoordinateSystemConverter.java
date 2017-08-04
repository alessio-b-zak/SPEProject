package com.bitbusters.android.speproject.helpers;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;

/******************************************************************
 * Created by mihajlo on 21/06/17.
 *
 * Based on:
 * https://github.com/chrisveness/geodesy
 * (c) Chris Veness 2011-2014 / MIT Licence
 *****************************************************************/

public class CoordinateSystemConverter {

    private final String osgb36 = "+proj=tmerc +lat_0=49 +lon_0=-2 +k=0.9996012717 +x_0=400000 " +
            "+y_0=-100000 +ellps=airy +towgs84=446.448,-125.157,542.060,0.1502," +
            "0.2470,0.8421,-20.4894 +units=m +no_defs";

    private final String wgs84 = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs ";

    private final CRSFactory crsFactory = new CRSFactory();

    private final CoordinateReferenceSystem crsOSGB = crsFactory.createFromParameters("EPSG:27700", osgb36);
    private final CoordinateReferenceSystem crsWGS84 = crsFactory.createFromParameters("EPSG:4326", wgs84);

    /**
     * Parse grid reference to easting/northing form
     */
    private static Pair<Double, Double> parse(String ngrGridRef) {
        ngrGridRef = ngrGridRef.trim().replaceAll(" ", "");

        // get numeric values of letter references, mapping A->0, B->1, C->2, etc:
        int l1 = Character.toUpperCase(ngrGridRef.charAt(0)) - 'A';
        int l2 = Character.toUpperCase(ngrGridRef.charAt(1)) - 'A';
        // shuffle down letters after 'I' since 'I' is not used in grid:
        if (l1 > 7) l1--;
        if (l2 > 7) l2--;

        // convert grid letters into 100km-square indexes from false origin (grid square SV):
        long e = ((l1 - 2) % 5) * 5 + (l2 % 5);
        long n = (long) ((19 - Math.floor(l1 / 5) * 5) - Math.floor(l2 / 5));
        if (e < 0 || e > 6 || n < 0 || n > 12) return null;

        ngrGridRef = ngrGridRef.substring(2);

        // append numeric part of references to grid index:
        int len = ngrGridRef.length() / 2;
        String eʹ = "" + e + ngrGridRef.substring(0, len);
        String nʹ = "" + n + ngrGridRef.substring(len);

        switch (len) {
            case 0:
                eʹ += "50000";
                nʹ += "50000";
                break;
            case 1:
                eʹ += "5000";
                nʹ += "5000";
                break;
            case 2:
                eʹ += "500";
                nʹ += "500";
                break;
            case 3:
                eʹ += "50";
                nʹ += "50";
                break;
            case 4:
                eʹ += "5";
                nʹ += "5";
                break;
            case 5:
                break;
        }

        return new Pair<Double, Double>(Double.parseDouble(eʹ), Double.parseDouble(nʹ));
    }

    public LatLng convertNgrToLatLng(String ngrGridRef) {
        Pair<Double, Double> eastNorth = parse(ngrGridRef);

        ProjCoordinate output = new ProjCoordinate();
        ProjCoordinate input = new ProjCoordinate(eastNorth.first, eastNorth.second);

        CoordinateTransform transformation = new BasicCoordinateTransform(crsOSGB, crsWGS84);
        transformation.transform(input, output);

        return new LatLng(output.y, output.x);
    }

    public LatLng convertEastNorthToLatLng(Double easting, Double northing) {
        ProjCoordinate output = new ProjCoordinate();
        ProjCoordinate input = new ProjCoordinate(easting, northing);

        CoordinateTransform transformation = new BasicCoordinateTransform(crsOSGB, crsWGS84);
        transformation.transform(input, output);

        return new LatLng(output.y, output.x);
    }

    public Pair<Double, Double> convertLatLngToEastNorth(Double latitude, Double longitude) {
        ProjCoordinate output = new ProjCoordinate();
        ProjCoordinate input = new ProjCoordinate(longitude, latitude);

        CoordinateTransform transformation = new BasicCoordinateTransform(crsWGS84, crsOSGB);
        transformation.transform(input, output);

        return new Pair<Double, Double>(output.x, output.y);
    }
}
