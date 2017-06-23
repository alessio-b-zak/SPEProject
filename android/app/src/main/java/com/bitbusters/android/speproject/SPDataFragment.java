package com.bitbusters.android.speproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class SPDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    View mSPDataView;
    private RecyclerView mRecyclerView;

    private DataViewActivity mDataViewActivity;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDataViewActivity = (DataViewActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spdataview, container, false);
        mSPDataView = view;

        // Initialise Recycler View and hide it
        mRecyclerView = (RecyclerView) view.findViewById(R.id.grid_view);
        mRecyclerView.setVisibility(View.INVISIBLE);

        WIMSPoint wimsPoint = mDataViewActivity.getSelectedWIMSPoint();
        if (wimsPoint.getRatingsSet()) {
            setChemBioText(wimsPoint);
        }
        else {
            new WIMSPointRatingsAPI(this).execute(wimsPoint);
        }

        TextView samplePointName = (TextView) view.findViewById(R.id.sp_name);
        String[] idAddress = mDataViewActivity.getSelectedWIMSPoint().getId().split("/");
        String idNum = idAddress[idAddress.length-1];
        String s = "<b>ID: </b>" + idNum;
        samplePointName.setText(Html.fromHtml(s));

        TextView samplePointType = (TextView) view.findViewById(R.id.sp_data1);
        s = "<b>TYPE: </b>" + mDataViewActivity.getSelectedWIMSPoint().getSamplingPointType();
        samplePointType.setText(Html.fromHtml(s));

        mToolbar = (Toolbar) view.findViewById(R.id.dataview_toolbar);

        mBackButton = (ImageButton) view.findViewById(R.id.back_button_sp_data_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void setChemBioText(WIMSPoint samplePoint) {

        TextView pollutionText = (TextView) mSPDataView.findViewById(R.id.sp_data2);

        String chemRating = mDataViewActivity.getSelectedWIMSPoint().getChemicalRating();
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

        String ecoRating = mDataViewActivity.getSelectedWIMSPoint().getEcologicalRating();
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

    }

}


