package com.bitbusters.android.speproject;

import android.content.res.Resources;

/**
 * Created by toddym42 on 26/11/2016.
 */

public class GalleryItem {

    private String mCaption;
    private String mId;
    private String mUrl;
    private int mResId;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getResId() {
        return mResId;
    }

    public void setResId(int resId) {
        mResId = resId;
    }
}
