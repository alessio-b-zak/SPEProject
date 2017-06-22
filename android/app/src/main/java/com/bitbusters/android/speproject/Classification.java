package com.bitbusters.android.speproject;

/**
 * Created by mihajlo on 21/06/2017.
 */

class Classification {
    private String value;
    private String certainty;
    private String year;

    Classification(String value, String certainty, String year) {
        this.value = value;
        this.certainty = certainty;
        this.year = year;
    }

    public String getValue() {
        return value;
    }
    public String getCertainty() {
        return certainty;
    }
    public String getYear() {
        return year;
    }

    public void setValue(String value) { this.value = value; }
    public void setCertainty(String certainty) { this.certainty = certainty; }
    public void setYear(String year) { this.year = year; }
}
