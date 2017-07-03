package com.bitbusters.android.speproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by toddym42 on 08/11/2016.
 */

public class WIMSDataFragment extends Fragment {

    Toolbar mToolbar;  // The toolbar.
    ImageButton mBackButton;
    View mWIMSDataView;
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
        View view = inflater.inflate(R.layout.fragment_wims_data_view, container, false);
        mWIMSDataView = view;

        // Initialise Recycler View and hide it
        mRecyclerView = (RecyclerView) view.findViewById(R.id.grid_view);
        mRecyclerView.setVisibility(View.INVISIBLE);

        WIMSPoint wimsPoint = mDataViewActivity.getSelectedWIMSPoint();
        if (wimsPoint.measurementsPopulated()) {
            setMeasurementsText(wimsPoint);
        }
        else {
            new WIMSPointRatingsAPI(this).execute(wimsPoint);
        }

        TextView wimsName = (TextView) view.findViewById(R.id.wims_name);
        wimsName.setText(wimsPoint.getLabel());

//        TextView wimsType = (TextView) view.findViewById(R.id.wims_type);
//        wimsType.setText(wimsPoint.getType());

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

    public void setMeasurementsText(WIMSPoint wimsPoint) {
        TableLayout tableLayout = (TableLayout) mWIMSDataView.findViewById(R.id.wims_table);

        TextView wimsName = (TextView) mWIMSDataView.findViewById(R.id.wims_name);
        wimsName.setText(wimsPoint.getLabel());

        for (Measurement measurement : wimsPoint.getMeasurementList()) {
            TableRow row = new TableRow(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(layoutParams);

            TextView determinand = new TextView(getContext());
            TextView result = new TextView(getContext());
            TextView date = new TextView(getContext());

            determinand.setText(measurement.getDeterminand());
            result.setText(String.valueOf(measurement.getResult()));
            date.setText(measurement.getYear());

            determinand.setPadding(3,0,0,0);
            determinand.setTextAppearance(getContext(), R.style.TextViewDataTableChild);
            result.setPadding(3,0,0,0);
            result.setTextAppearance(getContext(), R.style.TextViewDataTableChild);
            date.setPadding(3,0,0,0);
            date.setTextAppearance(getContext(), R.style.TextViewDataTableChild);

            row.addView(determinand);
            row.addView(result);
            row.addView(date);

            tableLayout.addView(row);
        }
    }

}


