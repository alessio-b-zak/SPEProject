package com.bitbusters.android.speproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Button
import android.widget.TextView

/**
 * Created by mihajlo on 18/07/17.
 */
class MyAreaFragment: FragmentHelper() {

    private val TAG = "MY_AREA_VIEW"

    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mMyAreaView: View
    private lateinit var myArea: MyArea
    private lateinit var mBackButton: ImageButton
    private lateinit var mWIMSPointButton: Button
    private lateinit var mPermitPointButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_my_area, container, false)

        mMyAreaView = view;

        myArea = mDataViewActivity.myArea

        mBackButton = view.bind(R.id.back_button_my_area_view)
        mBackButton.setOnClickListener {
            activity.onBackPressed()
        }

        mPermitPointButton = view.bind(R.id.my_area_permit_button)
        mPermitPointButton.visibility = View.GONE

        mWIMSPointButton = view.bind(R.id.my_area_wims_button)
        mWIMSPointButton.visibility = View.GONE

        populateCDEData()
        populateWIMSData()
        populatePermitData()

        return view
    }


    fun populateCDEData() {
        val waterbodyView: TextView = mMyAreaView.bind(R.id.my_area_waterbody_value)
        waterbodyView.text = myArea.waterbody

        val operationalView: TextView = mMyAreaView.bind(R.id.my_area_operational_value)
        operationalView.text = myArea.operationalCatchment

        val managementView: TextView = mMyAreaView.bind(R.id.my_area_management_value)
        managementView.text = myArea.managementCatchment

        val districtView: TextView = mMyAreaView.bind(R.id.my_area_district_value)
        districtView.text = myArea.riverBasinDistrict
    }

    fun populateWIMSData() {
        mWIMSPointButton.setOnClickListener {
            mDataViewActivity.setCameraFocusOnMarker(myArea.wimsPoint)
        }
        mWIMSPointButton.visibility = View.VISIBLE
    }

    fun populatePermitData() {
        mPermitPointButton.setOnClickListener {
            mDataViewActivity.setCameraFocusOnMarker(myArea.permitPoint)
        }
        mPermitPointButton.visibility = View.VISIBLE
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
    }
}