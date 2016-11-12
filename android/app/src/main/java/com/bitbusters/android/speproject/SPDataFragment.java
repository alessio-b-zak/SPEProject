package com.bitbusters.android.speproject;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class SPDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    Button mBackButton;
    Button mGridViewButton;
    Button mMapViewButton;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spdataview, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.dataview_toolbar);

        mBackButton = (Button) v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the data and grid fragments.
                Toast.makeText(getActivity(), "I don't want to die!", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();

                // Re-show the buttons.
                FloatingActionButton gpsButton = (FloatingActionButton) getActivity().findViewById(R.id.gps_button);
                gpsButton.show();
                FloatingActionButton camButton = (FloatingActionButton) getActivity().findViewById(R.id.cam_button);
                camButton.show();
            }
        });

        mGridViewButton = (Button) v.findViewById(R.id.gridview_button);
        mGridViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Grid View!", Toast.LENGTH_SHORT).show();

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (fragment instanceof SPDataFragment) {
                    fragment = new PhotoGridFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
                }
            }
        });

        mMapViewButton = (Button) v.findViewById(R.id.mapview_button);
        mMapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Map View!", Toast.LENGTH_SHORT).show();

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (fragment instanceof PhotoGridFragment) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(fragment).commit();
                }
            }
        });

        return v;
    }

}


