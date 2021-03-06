package com.epimorphics.android.myrivers.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.helpers.FragmentHelper

/**
 * A fragment occupying top part of the screen showcasing all data stored within a DischargePermitPoint.
 * It is initiated by clicking a DischargePermitPoint marker in DataViewActivity
 *
 * @see <a href="https://github.com/alessio-b-zak/myRivers/blob/master/graphic assets/screenshots/permit_data_view.png">Screenshot</a>
 * @see DataViewActivity
 */
open class DischargePermitDataFragment : FragmentHelper() {

    private lateinit var mToolbar: Toolbar
    private lateinit var mDataTable: TableLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDischargePermitDataView: View
    private lateinit var mDataViewActivity: DataViewActivity

    /**
     * Called when a fragment is created. Initiates mDataViewActivity
     *
     * @param savedInstanceState Saved state of the fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    /**
     * Called when a fragment view is created. Initiates and manipulates all required layout elements.
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Saved state of the fragment
     *
     * @return inflated and fully populated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_permit_data_view, container, false)
        mDischargePermitDataView = view

        mToolbar = view.bind(R.id.permit_toolbar)

        val mBackButton: ImageButton = view.bind(R.id.back_button_permit_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        // Initialise Recycler View and hide it so that map is visible
        mRecyclerView = view.bind(R.id.permit_grid_view)
        mRecyclerView.visibility = View.INVISIBLE

        val permitPoint = mDataViewActivity.selectedPermitPoint

        // Links to the EPR web view
        val mFullReportButton: Button = view.bind(R.id.full_report_button_permit_data_view)
        mFullReportButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(permitPoint.id)
            startActivity(intent)
        }

        val permitHolderNameView: TextView = view.bind(R.id.permit_holder_name)
        permitHolderNameView.text = permitPoint.holder

        // Initiates the data table and populates it
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

        tableRow = newTableRow(rowIndex)
        addTextView(tableRow, "Effective Since:", parentWeight, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, simplifyDate(permitPoint.effectiveDate), childWeight)
        mDataTable.addView(tableRow)

        return view
    }

}