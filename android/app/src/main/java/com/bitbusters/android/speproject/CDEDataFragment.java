package com.bitbusters.android.speproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
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
        new CDEPointRatingsAPI(this).execute(cdePoint);

        TextView cdePointLabel = (TextView) view.findViewById(R.id.cd_label);
        String label = cdePoint.getLabel();
        cdePointLabel.setText(label);

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

    public void setClassificationText(CDEPoint cdePoint) {
        //Overall
        TextView overallValue = (TextView) mCDEDataView.findViewById(R.id.cde_table_overall_value);
        TextView overallCertainty = (TextView) mCDEDataView.findViewById(R.id.cde_table_overall_certainty);
        TextView overallYear = (TextView) mCDEDataView.findViewById(R.id.cde_table_overall_year);

        Classification overallClassification = cdePoint.getClassificationHashMap().get(CDEPoint.OVERALL);

        overallValue.setText(overallClassification.getValue());
        overallCertainty.setText(overallClassification.getCertainty());
        overallYear.setText(overallClassification.getYear());

        //Ecological
        TextView ecologicalValue = (TextView) mCDEDataView.findViewById(R.id.cde_table_ecological_value);
        TextView ecologicalCertainty = (TextView) mCDEDataView.findViewById(R.id.cde_table_ecological_certainty);
        TextView ecologicalYear = (TextView) mCDEDataView.findViewById(R.id.cde_table_ecological_year);

        Classification ecologicalClassification = cdePoint.getClassificationHashMap().get(CDEPoint.ECOLOGICAL);

        ecologicalValue.setText(ecologicalClassification.getValue());
        ecologicalCertainty.setText(ecologicalClassification.getCertainty());
        ecologicalYear.setText(ecologicalClassification.getYear());

        //Chemical
        TextView chemicalValue = (TextView) mCDEDataView.findViewById(R.id.cde_table_chemical_value);
        TextView chemicalCertainty = (TextView) mCDEDataView.findViewById(R.id.cde_table_chemical_certainty);
        TextView chemicalYear = (TextView) mCDEDataView.findViewById(R.id.cde_table_chemical_year);

        Classification chemicalClassification = cdePoint.getClassificationHashMap().get(CDEPoint.CHEMICAL);

        chemicalValue.setText(chemicalClassification.getValue());
        chemicalCertainty.setText(chemicalClassification.getCertainty());
        chemicalYear.setText(chemicalClassification.getYear());

    }

}


