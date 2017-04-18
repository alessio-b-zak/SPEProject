package com.bitbusters.android.speproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by toddym42 on 18/04/2017.
 */

public class InfoFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    private TextView mInfoText;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_infoview, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.infoview_toolbar);

        mBackButton = (ImageButton) v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mInfoText = (TextView) v.findViewById(R.id.info_text);

        return v;
    }

}
