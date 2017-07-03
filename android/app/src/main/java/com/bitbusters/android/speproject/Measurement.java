package com.bitbusters.android.speproject;

import org.joda.time.DateTime;

/**
 * Created by mihajlo on 21/06/2017.
 */

class Measurement {
    private String determinand;
    private Double result;
    private String year;

    Measurement(String determinand, Double result, String year) {
        this.determinand = determinand;
        this.result = result;
        this.year = year;
    }

    public String getDeterminand() {
        return determinand;
    }
    public Double getResult() {
        return result;
    }
    public String getYear() {
        return year;
    }

    public void setDeterminand(String determinand) {
        this.determinand = determinand;
    }
    public void setResult(Double result) {
        this.result = result;
    }
    public void setYear(String year) {
        this.year = year;
    }
}
