package com.bitbusters.android.speproject;

import android.content.res.Resources;
import android.view.View;

/**
 * Created by toddym42 on 26/11/2016.
 */

public class GalleryItem {

    private String mTag;
    private String mComment;
    private int mResId;

    @Override
    public String toString() {
        return String.valueOf(mResId);
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

    public int getResId() {
        return mResId;
    }

    public void setResId(int resId) {
        mResId = resId;
    }
}
