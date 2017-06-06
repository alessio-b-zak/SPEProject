package com.bitbusters.android.speproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by toddym42 on 26/11/2016.
 */

public class GalleryItem extends Point {

    private String mName;
    private String mTag;
    private String mComment;
    private String mId;
    private Bitmap mThumbnail;

    public GalleryItem(double latitude, double longitude, String name, String tag, String comment, String id,Bitmap thumbnail) {
        super(latitude, longitude, "Picture_Point", "");
        mName = name;
        mTag = tag;
        mComment = comment;
        mId = id;
        mThumbnail = thumbnail;
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

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getComment() {
        return mComment;
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
