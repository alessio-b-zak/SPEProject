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
        WIMSPointMetalsAPI(mWIMSDataFragment).execute(wimsPoint)

        mToolbar = view.bind(R.id.wims_data_toolbar)

        mBackButton = view.bind(R.id.back_button_wims_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        mMoreInfoButton = view.bind(R.id.info_button_wims_data_view)
        // TODO: Change to openWIMSDetailsFragment
        mMoreInfoButton.setOnClickListener {
            Log.i(TAG, "Number of measurements pulled: " + wimsPoint.measurementMap.size )
            /*mDataViewActivity.openCDEDetailsFragment()*/
        }
        mMoreInfoButton.visibility = View.INVISIBLE

        return view
    }

    fun showMoreInfoButton() {
        mMoreInfoButton.visibility = View.VISIBLE
    }

    fun setMeasurementsText(wimsPoint: WIMSPoint) {
        Log.i(TAG, "Number of measurements pulled: " + wimsPoint.measurementMap.size )
        val wimsName : TextView = mWIMSDataView.bind(R.id.wims_name)
        wimsName.text = wimsPoint.label

        var tableHeaderRow = newTableRow()

        addTextView(tableHeaderRow, "Determinand", 0.3, R.style.TextViewDataTableParent, Gravity.START)
        addTextView(tableHeaderRow, "Unit", 0.2, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Sample Dates", 0.5, R.style.TextViewDataTableParent)

        mMeasurementTable.addView(tableHeaderRow)

        for(entry in wimsPoint.measurementMap) {
            val measurementList = arrayListOf<Measurement>()
            for(measure in entry.value) {
                measurementList.add(measure)
            }
            if(measurementList.size > 1) {
                tableHeaderRow = newTableRow()

                addTextView(tableHeaderRow, "", 0.5, R.style.TextViewDataTableParent, Gravity.START)
                addTextView(tableHeaderRow, measurementList[0].date, 0.25, R.style.TextViewDataTableParent)
                addTextView(tableHeaderRow, measurementList[1].date, 0.25, R.style.TextViewDataTableParent)

                mMeasurementTable.addView(tableHeaderRow)


                tableHeaderRow = newTableRow()

                addTextView(tableHeaderRow, entry.key, 0.3, R.style.TextViewDataTableChild, Gravity.START)
                addTextView(tableHeaderRow, measurementList[0].unit, 0.2, R.style.TextViewDataTableChild)
                addTextView(tableHeaderRow, measurementList[0].result.toString(), 0.25, R.style.TextViewDataTableChild)
                addTextView(tableHeaderRow, measurementList[1].result.toString(), 0.25, R.style.TextViewDataTableChild)

                mMeasurementTable.addView(tableHeaderRow)
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

}