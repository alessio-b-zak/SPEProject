package com.bitbusters.android.speproject;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by toddym42 on 04/12/2016.
 */

public class PhotoViewFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;

    private GalleryItem mGalleryItem;
    private TextView mNameText;
    private TextView mTagText;
    private TextView mCommentText;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photoview, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.photoview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //Toast.makeText(getActivity(), String.valueOf(mGalleryItem.getResId()), Toast.LENGTH_SHORT).show();

        ImageView iv = (ImageView) v.findViewById(R.id.photo);
        iv.setImageResource(mGalleryItem.getResId());

        mNameText = (TextView) v.findViewById(R.id.photo_name);
        mNameText.setText(mGalleryItem.getName());

        mTagText = (TextView) v.findViewById(R.id.photo_tag);
        mTagText.setText(mGalleryItem.getTag());

        mCommentText = (TextView) v.findViewById(R.id.photo_comment);
        mCommentText.setText(mGalleryItem.getComment());

        return v;
    }

    public GalleryItem getGalleryItem() {
        return mGalleryItem;
    }

    public void setGalleryItem(GalleryItem galleryItem) {
        mGalleryItem = galleryItem;
    }


}
