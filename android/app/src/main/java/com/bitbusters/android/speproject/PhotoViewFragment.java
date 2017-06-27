package com.bitbusters.android.speproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by toddym42 on 04/12/2016.
 */

public class PhotoViewFragment extends Fragment {
    private static final String TAG = "PHOTO_VIEW_FRAGMENT";

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;

    private GalleryItem mGalleryItem;
//    private TextView mNameText;
    private TextView mTagText;
    private TextView mCommentText;
    private TextView mDateText;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_full_photo_view, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.photoview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button_photo_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ImageView iv = (ImageView) v.findViewById(R.id.photo);

        new ImageDownloader(iv).execute(mGalleryItem.getId());
        //iv.setImageResource(mGalleryItem.getResId());

//        mNameText = (TextView) v.findViewById(R.id.photo_name);
//        mNameText.setText(mGalleryItem.getName());

        mTagText = (TextView) v.findViewById(R.id.photo_tag);
        mTagText.setText(mGalleryItem.printTags());

        mCommentText = (TextView) v.findViewById(R.id.photo_comment);
        mCommentText.setText(mGalleryItem.getComment());

        mDateText = (TextView) v.findViewById(R.id.photo_date);
        mDateText.setText(mGalleryItem.getDate());

        return v;
    }

    public void setGalleryItem(GalleryItem galleryItem) {
        mGalleryItem = galleryItem;
        Log.i(TAG, galleryItem.getName());
    }

}
