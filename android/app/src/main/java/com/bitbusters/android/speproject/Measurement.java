package com.bitbusters.android.speproject;

import org.joda.time.DateTime;

/**
 * Created by mihajlo on 21/06/2017.
 */

class Measurement {
    private String determinand;
    private Double result;
    private DateTime dateTime;

    Measurement(String determinand, Double result, DateTime dateTime) {
        this.determinand = determinand;
        this.result = result;
        this.dateTime = dateTime;
    }

    public String getDeterminand() {
        return determinand;
    }
    public Double getResult() {
        return result;
    }
    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDeterminand(String determinand) {
        this.determinand = determinand;
    }
    public void setResult(Double result) {
        this.result = result;
    }
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
