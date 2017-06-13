package com.bitbusters.android.speproject;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class SPDataFragment extends Fragment implements ImgLocDowListener{

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    ImageButton mMapViewButton;
    ImageButton mGridViewButton;
    Boolean mInGridView;
    View mSPDataView;


    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    private DataViewActivity mDataViewActivity;
    private String placeHolder = "                  ";

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDataViewActivity = (DataViewActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spdataview, container, false);
        mSPDataView = v;

        SamplingPoint samplingPoint = mDataViewActivity.getSelectedSamplingPoint();
        if (samplingPoint.getRatingsSet()) {
            setChemBioText(samplingPoint);
        }
        else {
            new SamplingPointRatingsAPI(this).execute(samplingPoint);
        }

        TextView samplePointName = (TextView) v.findViewById(R.id.sp_name);
        String[] idAddress = mDataViewActivity.getSelectedSamplingPoint().getId().split("/");
        String idNum = idAddress[idAddress.length-1];
        String s = "<b>ID: </b>" + idNum;
        samplePointName.setText(Html.fromHtml(s));

        TextView samplePointType = (TextView) v.findViewById(R.id.sp_data1);
        s = "<b>TYPE: </b>" + mDataViewActivity.getSelectedSamplingPoint().getSamplingPointType();
        samplePointType.setText(Html.fromHtml(s));

        mToolbar = (Toolbar) v.findViewById(R.id.dataview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button_sp_data_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mInGridView) {
                    mPhotoRecyclerView.setVisibility(View.INVISIBLE);
                }
                getActivity().onBackPressed();
            }
        });

        mMapViewButton = (ImageButton) v.findViewById(R.id.map_view_button);
        mMapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInGridView = false;

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
        mInGridView = false;

        mGridViewButton = (ImageButton) v.findViewById(R.id.grid_view_button);
        mGridViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInGridView = true;

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

        // span count is setting the columns. GridLayoutManager can then be scrolled downwards.
        int spanCount = 3;
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));

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

    @Override
    public void imagesDownloaded() {
        new PopulateItemsTask().execute(); // separate thread.
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
            //Drawable itemImage = getResources().getDrawable(R.drawable.sample0);
            // Drawable itemImage = getResources().getDrawable(galleryItem.getResId());
            Drawable itemImage = new BitmapDrawable(galleryItem.getThumbnail());
            photoHolder.bindDrawable(itemImage);

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
            /*
            List<GalleryItem> items = new ArrayList<>();

            for (GalleryItem pp : mDataViewActivity.getPhotoMarkers()) {
                GalleryItem item = new GalleryItem();
                //String imageName = "sample" + "7";
                item.setName(pp.getId());
                //item.setTag("Tag");
                //item.setComment("abcd efgh ijkl mnop qrst uvwx yz01 1234 5678 9");
                //item.setResId(getResources().getIdentifier(imageName, "drawable", "com.bitbusters.android.speproject"));
                item.setId(pp.getId());
                items.add(item);
            }


            for (int i = 0; i < 18; i++) {
                GalleryItem item = new GalleryItem();
                String imageName = "sample" + "7";
                item.setName(imageName);
                item.setTag("Tag " + i);
                item.setComment("abcd efgh ijkl mnop qrst uvwx yz01 1234 5678 9");
                item.setResId(getResources().getIdentifier(imageName, "drawable", "com.bitbusters.android.speproject"));
                items.add(item);
            }


            Log.e("items in task" + String.valueOf(items.size()), "1");

            return items;
            */
            return mDataViewActivity.getPhotoMarkers();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }

    }

    // from http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
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


    public GalleryItem getGalleryItem(String id) {
        for (GalleryItem gi : mItems) {
            if (id.equals(gi.getId())) {
                return gi;
            }
        }

        Log.e("mItems size: " + String.valueOf(mItems.size()), "1");
        return null; // TODO: PROPER ERROR HANDLING.
    }

    public void setChemBioText(SamplingPoint samplePoint) {

        TextView pollutionText = (TextView) mSPDataView.findViewById(R.id.sp_data2);

        String chemRating = mDataViewActivity.getSelectedSamplingPoint().getChemicalRating();
        if (chemRating.equals("Poor")) {
            chemRating = "<font color=\"#ff0000\">Poor</font>";      // Red
        }
        else if (chemRating.equals("Moderate")) {
            chemRating = "<font color=\"#ffa500\">Moderate</font>";  // Orange
        }
        else if (chemRating.equals("Good")) {
            chemRating = "<font color=\"#00ff00\">Good</font>";      // Green
        }
        String chemString = "<b>Chemical Pollution Est.</b> &nbsp -- " + chemRating;

        String ecoRating = mDataViewActivity.getSelectedSamplingPoint().getEcologicalRating();
        if (ecoRating.equals("Poor")) {
            ecoRating = "<font color=\"#ff0000\">Poor</font>";      // Red
        }
        else if (ecoRating.equals("Moderate")) {
            ecoRating = "<font color=\"#ffa500\">Moderate</font>";  // Orange
        }
        else if (ecoRating.equals("Good")) {
            ecoRating = "<font color=\"#00ff00\">Good</font>";      // Green
        }
        String ecoString = "<b>Ecological Pollution Est.</b> -- " + ecoRating;

        pollutionText.setText(Html.fromHtml(chemString + "<br />" + ecoString));

        // Show all photo markers currently on screen.
        mDataViewActivity.showPhotoMarkersInView();

    }

}


