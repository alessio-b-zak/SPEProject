package com.epimorphics.android.myrivers.fragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.helpers.FragmentHelper

/**
 * Created by mihajlo on 05/07/17.
 */
open class DischargePermitDataFragment : FragmentHelper() {

    private lateinit var mToolbar: Toolbar
    private lateinit var mBackButton: ImageButton
    private lateinit var mDataTable: TableLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDischargePermitDataView: View
    private lateinit var mDataViewActivity: DataViewActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_permit_data_view, container, false)
        mDischargePermitDataView = view

        mToolbar = view.bind(R.id.permit_toolbar)

        mBackButton = view.bind(R.id.back_button_permit_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        // Initialise Recycler View and hide it
        mRecyclerView = view.bind(R.id.permit_grid_view)
        mRecyclerView.visibility = View.INVISIBLE

        val permitPoint = mDataViewActivity.selectedPermitPoint

        val permitHolderNameView: TextView = view.bind(R.id.permit_holder_name)
        permitHolderNameView.text = permitPoint.holder

        mDataTable = view.bind(R.id.permit_table)

        var rowIndex = 0
        val parentWeight = 0.35
        val childWeight = 0.65

        var tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Permit Type:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, permitPoint.effluentType, childWeight)
        mDataTable.addView(tableRow)

        tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Site Type:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, permitPoint.siteType, childWeight)
        mDataTable.addView(tableRow)

        tableRow = newTableRow(rowIndex++)
        addTextView(tableRow, "Effective Since:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, simplifyDate(permitPoint.effectiveDate), childWeight)
        mDataTable.addView(tableRow)

        return view
    }

}