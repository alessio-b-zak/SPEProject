package com.bitbusters.android.speproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

/**
 * Created by toddym42 on 26/11/2016.
 */

public class GalleryItem extends Point {

    private String mName;
    private List<ImageTag> mTags;
    private String mComment;
    private String mId;
    private Bitmap mThumbnail;
    private String mDate;

    public GalleryItem(double latitude, double longitude, String name, List<ImageTag> tags, String comment, String id, Bitmap thumbnail, String date) {
        super(latitude, longitude, "Picture_Point", "");
        mName = name;
        mTags = tags;
        mComment = comment;
        mId = id;
        mThumbnail = thumbnail;
        mDate = date;
    }

    @Override
    public String toString() {
        return String.valueOf(mId);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<ImageTag> getTags() {
        return mTags;
    }

    public String printTags() {
        String result = "";
        for(int i = 0; i < mTags.size(); i++) {
            result = result.concat(mTags.get(i).text);
            if (i != mTags.size() - 1) {
                result = result.concat(", ");
            }
        }
        return result;
    }

//    public void setTag(String tag) {
//        mTag = tag;
//    }

    public String getComment() {
        return mComment;
    }

    public String getDate() {
        return mDate;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        mThumbnail = thumbnail;
    }
}
