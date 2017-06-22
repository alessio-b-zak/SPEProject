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

public class CDEDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    View mCDEDataView;
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
        View view = inflater.inflate(R.layout.fragment_cde_data_view, container, false);
        mCDEDataView = view;

        // Initialise Recycler View and hide it
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cde_grid_view);
        mRecyclerView.setVisibility(View.INVISIBLE);

        CDEPoint cdePoint = mDataViewActivity.getSelectedCDEPoint();
        new CDEPointRatingsAPI().execute(cdePoint);

//        TextView cdePointLabel = (TextView) view.findViewById(R.id.cd_label);
//        String idAddress = cdePoint.getLabel();
//        String idNum = idAddress[idAddress.length-1];
//        String s = "<b>ID: </b>" + idNum;
//        cdePointLabel.setText(Html.fromHtml(s));
//
//        TextView samplePointType = (TextView) view.findViewById(R.id.sp_data1);
//        s = "<b>TYPE: </b>" + mDataViewActivity.getSelectedSamplingPoint().getSamplingPointType();
//        samplePointType.setText(Html.fromHtml(s));

        mToolbar = (Toolbar) view.findViewById(R.id.cde_toolbar);

        mBackButton = (ImageButton) view.findViewById(R.id.back_button_cde_data_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void setChemBioText(SamplingPoint samplePoint) {

        TextView pollutionText = (TextView) mCDEDataView.findViewById(R.id.sp_data2);

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

    }

}


