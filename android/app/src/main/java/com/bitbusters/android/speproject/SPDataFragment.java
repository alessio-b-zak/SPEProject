package com.bitbusters.android.speproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class SPDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    ImageButton mGridViewButton;
    ImageButton mMapViewButton;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spdataview, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.dataview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the data and grid fragments.
                Toast.makeText(getActivity(), "I don't want to die!", Toast.LENGTH_SHORT).show();

                getActivity().onBackPressed();
            }
        });

        mMapViewButton = (ImageButton) v.findViewById(R.id.mapview_button);
        mMapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Map View!", Toast.LENGTH_SHORT).show();

                // Highlight map view button.
                mMapViewButton.setColorFilter(Color.argb(255,0,204,204));
                mMapViewButton.invalidate();
                // Unhighlight grid view button.
                mGridViewButton.setColorFilter(Color.argb(255,255,255,255));
                mGridViewButton.invalidate();
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                // If switching to map view from grid view, hide the grid view.
                if (fragment instanceof PhotoGridFragment) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(fragment).commit();

                }
            }
        });
        // Make map button highlighted as default.
        mMapViewButton.setColorFilter(Color.argb(255,0,204,204));
        mMapViewButton.invalidate();

        mGridViewButton = (ImageButton) v.findViewById(R.id.gridview_button);
        mGridViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Grid View!", Toast.LENGTH_SHORT).show();

                // Highlight grid view button.
                mGridViewButton.setColorFilter(Color.argb(255,0,204,204));
                mGridViewButton.invalidate();
                // Unhighlight map view button.
                mMapViewButton.setColorFilter(Color.argb(255,255,255,255));
                mMapViewButton.invalidate();
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                // If loading grid view for the first time, add to back stack.
                if (fragment instanceof SPDataFragment) {
                    fragment = new PhotoGridFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                // Else just show it again (after previously being hidden).
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
                }
            }
        });

        return v;
    }

}


