package com.bitbusters.android.speproject;
import android.graphics.Bitmap;

import java.util.List;


/**
 * Created by cp153 on 06/12/2016.
 */

class Image {
    private String id;
    private Bitmap image;
    private double latitude;
    private double longitude;
    private String comment;
    private List<ImageTag> tags;
    private String date;

    Image(String id, Bitmap image, double latitude, double longitude, String comment, List<ImageTag> tags, String date) {
        this.id = id;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.tags = tags;
        this.date = date;
    }

    Image(String id, Bitmap image, double latitude, double longitude, String comment, List<ImageTag> tags) {
        this.id = id;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.tags = tags;
        this.date = "01/01/1900 12:00:00";
    }

    public String getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public List<ImageTag> getTags() {
        return tags;
    }

    public String getTagsString() {
        String result = "";
        for(int i = 0; i < tags.size(); i++) {
            result = result.concat(tags.get(i).name());
            if (i != tags.size() - 1) {
                result = result.concat(",");
            }
        }
        return result;
    }

    public String printTags() {
        String result = "";
        for(int i = 0; i < tags.size(); i++) {
            result = result.concat(tags.get(i).text);
            if (i != tags.size() - 1) {
                result = result.concat(",\n");
            }
        }
        return result;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
