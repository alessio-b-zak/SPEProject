package com.bitbusters.android.speproject

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import org.w3c.dom.Text


/**
 * Created by mihajlo on 07/07/17.
 */

open class WIMSDataFragment : Fragment() {
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mMoreInfoButton: Button
    private lateinit var mWIMSDataView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mWIMSDataFragment : WIMSDataFragment
    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mMeasurementTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_wims_data_view, container, false)
        mWIMSDataView = view
        mWIMSDataFragment = this

        // Initialise Recycler View and hide it
        mRecyclerView = view.bind(R.id.wims_recycler_view)
        mRecyclerView.visibility = View.INVISIBLE

        mMeasurementTable = view.bind(R.id.wims_table)

        val wimsPoint = mDataViewActivity.selectedWIMSPoint
        WIMSPointRatingsAPI(mWIMSDataFragment).execute(wimsPoint)

        mToolbar = view.bind(R.id.wims_data_toolbar)

        mBackButton = view.bind(R.id.back_button_wims_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        mMoreInfoButton = view.bind(R.id.info_button_wims_data_view)
        // TODO: Change to openWIMSDetailsFragment
        mMoreInfoButton.setOnClickListener { mDataViewActivity.openCDEDetailsFragment() }
        mMoreInfoButton.visibility = View.INVISIBLE

        return view
    }

    fun setMeasurementsText(wimsPoint: WIMSPoint) {
        val wimsName : TextView = mWIMSDataView.bind(R.id.wims_name)
        wimsName.text = wimsPoint.label

        var tableHeaderRow = newTableRow()

        addTextView(tableHeaderRow, "Determinand", 0.4, R.style.TextViewDataTableParent, Gravity.START)
        addTextView(tableHeaderRow, "Unit", 0.15, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Result", 0.15, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Date", 0.3, R.style.TextViewDataTableParent)

        mMeasurementTable.addView(tableHeaderRow)

        for(measurement in wimsPoint.measurementMap) {

            tableHeaderRow = newTableRow()

            addTextView(tableHeaderRow, measurement.key, 0.4, R.style.TextViewDataTableChild, Gravity.START)
            addTextView(tableHeaderRow, measurement.value.unit, 0.15, R.style.TextViewDataTableChild)
            addTextView(tableHeaderRow, measurement.value.result.toString(), 0.15, R.style.TextViewDataTableChild)
            addTextView(tableHeaderRow, measurement.value.date, 0.3, R.style.TextViewDataTableChild)

            mMeasurementTable.addView(tableHeaderRow)
        }
    }

    fun addTextView(tableRow: TableRow, value: String?, weight: Double = 1.0,
                    style: Int = R.style.TextViewDataTableChild, gravity: Int = Gravity.CENTER,
                    leftPadding: Int = 0) {
        val textView : TextView = TextView(mDataViewActivity)
        val params = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight.toFloat())

        textView.layoutParams = params
        textView.text = value
        textView.setTextAppearance(context, style)
        textView.gravity = gravity
        textView.setPadding(leftPadding, 0, 0, 0)

        tableRow.addView(textView)
    }

    fun newTableRow() : TableRow {
        val tableRow: TableRow = TableRow(mDataViewActivity)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)

        tableRow.layoutParams = lp
        tableRow.gravity = Gravity.CENTER
        tableRow.setPadding(3, 10, 3, 0)

        return tableRow
    }

    fun <T : View> View.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }

}