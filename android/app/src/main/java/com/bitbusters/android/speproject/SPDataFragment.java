package com.bitbusters.android.speproject;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class SPDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    ImageButton mMapViewButton;
    ImageButton mGridViewButton;

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        populateItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spdataview, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.dataview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mMapViewButton = (ImageButton) v.findViewById(R.id.mapview_button);
        mMapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Highlight map view button.
                mMapViewButton.setColorFilter(Color.argb(255,79,195,247));
                mMapViewButton.invalidate();
                // Unhighlight grid view button.
                mGridViewButton.setColorFilter(Color.argb(255,255,255,255));
                mGridViewButton.invalidate();

                // Hide the grid view.
                mPhotoRecyclerView.animate().translationY(mPhotoRecyclerView.getHeight());

            }
        });
        // Make map button highlighted as default.
        mMapViewButton.setColorFilter(Color.argb(255,79,195,247));
        mMapViewButton.invalidate();

        mGridViewButton = (ImageButton) v.findViewById(R.id.gridview_button);
        mGridViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Highlight grid view button.
                mGridViewButton.setColorFilter(Color.argb(255,79,195,247));
                mGridViewButton.invalidate();
                // Unhighlight map view button.
                mMapViewButton.setColorFilter(Color.argb(255,255,255,255));
                mMapViewButton.invalidate();

                // Show the grid view.
                mPhotoRecyclerView.animate().translationY(mPhotoRecyclerView.getHeight());
                mPhotoRecyclerView.animate().translationY(0);

            }
        });

        // Instantiating the grid view.  Hidden as default when marker first selected.
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.grid_view);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        mPhotoRecyclerView.setY(size.y);

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        // Adding spaces between photos.
        // from http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
        mPhotoRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);

            // When a photo in the grid is clicked.
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PhotoViewFragment fragment = new PhotoViewFragment();
                    int itemPosition = mPhotoRecyclerView.getChildLayoutPosition(v);
                    fragment.setGalleryItem(mGalleryItems.get(itemPosition));

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null).commit();
                }
            });

            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable itemImage = getResources().getDrawable(galleryItem.getResId());
            photoHolder.bindDrawable(itemImage);

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

    }

    private void populateItems() {

        for (int i = 0; i < 18; i++) {
            GalleryItem item = new GalleryItem();
            String imageName = "sample" + i;
            item.setName(imageName);
            item.setTag("Tag " + i);
            item.setComment("abcd efgh ijkl mnop qrst uvwx yz01 1234 5678 9");
            item.setResId(getResources().getIdentifier(imageName, "drawable", "com.bitbusters.android.speproject"));
            mItems.add(item);
        }

        setupAdapter();
    }

    // from http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    // TODO: Possibly should be made into separate class file?
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacingPX;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacingDP, boolean includeEdge) {
            // Convert from dp to px.
            spacingDP = Math.round(spacingDP * getResources().getDisplayMetrics().density);

            this.spanCount = spanCount;
            this.spacingPX = spacingDP;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacingPX - column * spacingPX / spanCount; // spacingPX - column * ((1f / spanCount) * spacingPX)
                outRect.right = (column + 1) * spacingPX / spanCount; // (column + 1) * ((1f / spanCount) * spacingPX)

                if (position < spanCount) { // top edge
                    outRect.top = spacingPX;
                }
                outRect.bottom = spacingPX; // item bottom
            } else {
                outRect.left = column * spacingPX / spanCount; // column * ((1f / spanCount) * spacingPX)
                outRect.right = spacingPX - (column + 1) * spacingPX / spanCount; // spacingPX - (column + 1) * ((1f /    spanCount) * spacingPX)
                if (position >= spanCount) {
                    outRect.top = spacingPX; // item top
                }
            }
        }
    }

    public List<GalleryItem> getItems() {
        return mItems;
    }
}


