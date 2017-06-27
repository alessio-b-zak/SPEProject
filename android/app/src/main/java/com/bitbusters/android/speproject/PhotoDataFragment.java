package com.bitbusters.android.speproject;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihajlo on 08/11/2016.
 */

public class PhotoDataFragment extends Fragment implements ThumbnailsDownloadListener {

    View mPhotoDataView;

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    private DataViewActivity mDataViewActivity;

    private int mPhotoLayoutViewHeight;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDataViewActivity = (DataViewActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        mPhotoDataView = view;

        // Instantiating the list view and a layout manager
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_list);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        try {
            mPhotoLayoutViewHeight = container.getChildAt(R.id.photo_list_linear).getHeight();
        } catch (NullPointerException e) {

        }
        // Hide the map holder in order to be able to use the map in the background activity
        RecyclerView mMapHolder = (RecyclerView) view.findViewById(R.id.map_holder);
        mMapHolder.animate().translationY(mMapHolder.getHeight());

        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void imagesDownloaded() {
        new PopulateItemsTask().execute(); // separate thread.
    }

    private class DataHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;
        private TextView mComment;
        private TextView mTag;

        private DataHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.list_image);
            mComment = (TextView) itemView.findViewById(R.id.comment);
            mTag = (TextView) itemView.findViewById(R.id.tag);
        }

        private void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<DataHolder> {

        private List<GalleryItem> mGalleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public DataHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.single_photo_list_item, viewGroup, false);

            // When a photo in the grid is clicked.
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PhotoViewFragment fragment = new PhotoViewFragment();
                    int itemPosition = mPhotoRecyclerView.getChildLayoutPosition(v);
                    fragment.setGalleryItem(mGalleryItems.get(itemPosition));

                    mDataViewActivity.displayHomeButtons(false);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null).commit();
                }
            });

            return new DataHolder(view);
        }

        @Override
        public void onBindViewHolder(DataHolder dataHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable itemImage = new BitmapDrawable(galleryItem.getThumbnail());
            dataHolder.bindDrawable(itemImage);
            dataHolder.mComment.setText(galleryItem.getComment());
            dataHolder.mTag.setText(galleryItem.printTags());

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

    }

    // Populate items in separate thread.
    private class PopulateItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return mDataViewActivity.getPhotoMarkers();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }

    }

    public List<GalleryItem> getItems() {
        return mItems;
    }


    public GalleryItem getGalleryItem(String id) {
        for (GalleryItem gi : mItems) {
            if (id.equals(gi.getId())) {
                return gi;
            }
        }

        Log.e("mItems size: " + String.valueOf(mItems.size()), "1");
        return null; // TODO: PROPER ERROR HANDLING.
    }

    public int getPhotoLayoutViewHeight() {
        return mPhotoLayoutViewHeight;
    }
}


