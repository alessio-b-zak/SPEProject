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

    private val TAG = "WIMS_DATA_FRAGMENT"

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
        mMoreInfoButton.setOnClickListener {
            mDataViewActivity.openWIMSDetailsFragment()
        }
        mMoreInfoButton.visibility = View.INVISIBLE

        return view
    }

    fun showMoreInfoButton() {
        mMoreInfoButton.visibility = View.VISIBLE
    }

    fun setMeasurementsText(wimsPoint: WIMSPoint) {
        WIMSPointMetalsAPI(mWIMSDataFragment).execute(wimsPoint)
//        Log.i(TAG, "Number of measurements pulled: " + wimsPoint.measurementMap.size )
        val wimsName : TextView = mWIMSDataView.bind(R.id.wims_name)
        wimsName.text = wimsPoint.label

        var tableHeaderRow = newTableRow()

        addTextView(tableHeaderRow, "Determinand", 0.28, R.style.TextViewDataTableParent, Gravity.START)
        addTextView(tableHeaderRow, "Sample Dates", 0.72, R.style.TextViewDataTableParent)

        mMeasurementTable.addView(tableHeaderRow)

        for(entry in WIMSPoint.generalGroup) {
            if(wimsPoint.measurementMap.containsKey(entry) && wimsPoint.measurementMap[entry]!!.isNotEmpty()) {
                val measurementList = arrayListOf<Measurement>()
                for (measure in wimsPoint.measurementMap[entry]!!) {
                    measurementList.add(measure)
                }
                if (measurementList.size > 2) {
                    tableHeaderRow = newTableRow()

                    addTextView(tableHeaderRow, entry, 0.28, R.style.TextViewDataTableParentLight, Gravity.START)
                    addTextView(tableHeaderRow, simplifyDate(measurementList[0].date), 0.24, R.style.TextViewDataTableParentLight, Gravity.END)
                    addTextView(tableHeaderRow, simplifyDate(measurementList[1].date), 0.24, R.style.TextViewDataTableParentLight, Gravity.END)
                    addTextView(tableHeaderRow, simplifyDate(measurementList[2].date), 0.24, R.style.TextViewDataTableParentLight, Gravity.END)

                    mMeasurementTable.addView(tableHeaderRow)

                    tableHeaderRow = newTableRow()

                    val unit = measurementList[0].unit
                    addTextView(tableHeaderRow, "($unit)", 0.28, R.style.TextViewDataTableChild, Gravity.START)
                    addTextView(tableHeaderRow, measurementList[0].result.toString(), 0.24, R.style.TextViewDataTableChild, Gravity.END)
                    addTextView(tableHeaderRow, measurementList[1].result.toString(), 0.24, R.style.TextViewDataTableChild, Gravity.END)
                    addTextView(tableHeaderRow, measurementList[2].result.toString(), 0.24, R.style.TextViewDataTableChild, Gravity.END)

                    mMeasurementTable.addView(tableHeaderRow)
                }
            }
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

    fun simplifyDate(date: String) : String {
        val year = date.substring(2, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        return "$day/$month/$year"
    }

}