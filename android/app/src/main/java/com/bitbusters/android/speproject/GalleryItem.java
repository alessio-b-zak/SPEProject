package com.bitbusters.android.speproject;

import android.content.res.Resources;
import android.view.View;

/**
 * Created by toddym42 on 26/11/2016.
 */

public class GalleryItem {

    private String mName;
    private String mTag;
    private String mComment;
    private String mId;
    private int mResId;  // For testing.

    @Override
    public String toString() {
        return String.valueOf(mResId);
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

    // For testing
    public int getResId() {
        return mResId;
    }

    public void setResId(int resId) {
        mResId = resId;
    }
}
