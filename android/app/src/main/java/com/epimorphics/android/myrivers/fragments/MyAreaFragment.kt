package com.epimorphics.android.myrivers.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.Characteristic
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.helpers.FragmentHelper


/**
 * Created by mihajlo on 18/07/17.
 */
class MyAreaFragment : FragmentHelper() {

    private val TAG = "MY_AREA_VIEW"

    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mMyAreaView: View
    private lateinit var mCDETable: TableLayout
    private lateinit var mCharacteristicsTitle: TextView
    private lateinit var mCharacteristicsTable: TableLayout
    private lateinit var myArea: MyArea
    private lateinit var mBackButton: ImageButton
    private lateinit var mWIMSPointButton: Button
    private lateinit var mPermitPointButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_my_area_view, container, false)

        mMyAreaView = view;

        myArea = mDataViewActivity.myArea

        mBackButton = view.bind(R.id.back_button_my_area_view)
        mBackButton.setOnClickListener {
            activity.onBackPressed()
        }

        mCDETable = view.bind(R.id.my_area_summary_table)
        mCharacteristicsTitle = view.bind(R.id.my_area_characteristics_title)
        mCharacteristicsTable = view.bind(R.id.my_area_characteristics_table)

        mPermitPointButton = view.bind(R.id.my_area_permit_button)
//        setButtonColor(mPermitPointButton)
        mPermitPointButton.visibility = View.GONE

        mWIMSPointButton = view.bind(R.id.my_area_wims_button)
//        setButtonColor(mWIMSPointButton)
        mWIMSPointButton.visibility = View.GONE

        populateCDEData()
        populateWIMSData()
        populatePermitData()

        return view
    }

    fun populateCDEData() {
        if(myArea.hasWaterbody) {
            populateWaterbodyDetails()
            populateCharacteristicsDetails()
        } else {
            mCDETable.visibility = View.GONE
            mCharacteristicsTable.visibility = View.GONE
            mCharacteristicsTitle.text = context.resources.getString(R.string.my_area_unable_to_find_waterbody)
        }
    }

    fun populateWaterbodyDetails() {
        var rowIndex = 0
        val parentWeight = 0.3
        val childWeight = 0.7

        var tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Nearest Waterbody:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.waterbody, childWeight, R.style.text_view_table_child, Gravity.START)
        mCDETable.addView(tableRow)

        tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Operational Catchment:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.operationalCatchment, childWeight, R.style.text_view_table_child, Gravity.START)
        mCDETable.addView(tableRow)

        tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Management Catchment:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.managementCatchment, childWeight, R.style.text_view_table_child, Gravity.START)
        mCDETable.addView(tableRow)

        tableRow = newTableRow(rowIndex)
        addTextView(tableRow, "River Basin District:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.riverBasinDistrict, childWeight, R.style.text_view_table_child, Gravity.START)
        mCDETable.addView(tableRow)
    }

    fun populateCharacteristicsDetails() {
        var rowIndex = 0
        val parentWeight = 0.4
        val childWeight = 0.6
        for(characteristic: Characteristic in myArea.characteristicList) {
            val tableRow: TableRow = newTableRow(rowIndex++)
            addTextView(tableRow, characteristic.label, parentWeight,
                        R.style.text_view_table_parent, Gravity.START)
            addTextView(tableRow, "${characteristic.value} ${characteristic.unit}", childWeight,
                        R.style.text_view_table_child, Gravity.START)
            mCharacteristicsTable.addView(tableRow)
        }
    }

    fun populateWIMSData() {
        mWIMSPointButton.setOnClickListener {
            mDataViewActivity.setCameraFocusOnMarker(myArea.wimsPoint)
        }
        mWIMSPointButton.visibility = View.VISIBLE
        val buttonDescription: String = resources.getString(R.string.my_area_wims_button, myArea.wimsPoint.distance)
        mWIMSPointButton.text = buttonDescription
    }

    fun populatePermitData() {
        mPermitPointButton.setOnClickListener {
            mDataViewActivity.setCameraFocusOnMarker(myArea.permitPoint)
        }
        mPermitPointButton.visibility = View.VISIBLE
        val buttonDescription: String = resources.getString(R.string.my_area_permit_button, myArea.permitPoint.distance)
        mPermitPointButton.text = buttonDescription
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
    }
}