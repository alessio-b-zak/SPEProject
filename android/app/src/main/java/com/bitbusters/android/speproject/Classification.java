package com.bitbusters.android.speproject;

/**
 * Created by mihajlo on 21/06/2017.
 */

class Classification {
    private String name;
    private String value;
    private String certainty;
    private Integer year;

    Classification(String name) {
        this.name = name;
        this.value = "N/A";
        this.certainty = "N/A";
        this.year = 0;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getCertainty() {
        return certainty;
    }

    public Integer getYear() {
        return year;
    }

    public void setName(String name) { this.name = name; }

    public void setValue(String value) { this.value = value; }

    public void setCertainty(String certainty) { this.certainty = certainty; }

    public void setYear(Integer year) { this.year = year; }
}
