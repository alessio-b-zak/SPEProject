package com.bitbusters.android.speproject

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView


/**
 * Created by mihajlo on 07/07/17.
 */

open class CDEDetailsFragment : Fragment() {
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mCDEDetailsView: View
    private lateinit var mCDEDetailsTable: TableLayout
    private lateinit var mRNAGTable: TableLayout
    private lateinit var mCDEDetailsFragment: CDEDetailsFragment
    private lateinit var mDataViewActivity: DataViewActivity

    companion object {
        private val TAG = "CDE_DETAILS_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_cde_details_view, container, false)
        mCDEDetailsView = view
        mCDEDetailsFragment = this

        val cdePoint = mDataViewActivity.selectedCDEPoint
        CDEPointDetailRatingsAPI(this).execute(cdePoint)
        CDERnagAPI(this).execute(cdePoint)

        val cdePointLabel : TextView = view.bind(R.id.cde_details_title)
        cdePointLabel.text = cdePoint.label

        mToolbar = view.bind(R.id.cde_details_toolbar)

        mCDEDetailsTable = view.bind(R.id.cde_details_table)
        mRNAGTable = view.bind(R.id.cde_rnag_table)

        mBackButton = view.bind(R.id.back_button_cde_details_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        return view
    }

    fun setClassificationText(cdePoint: CDEPoint) {
        // Set Header Row
        val tableHeaderRow = newTableRow()

        addTextView(tableHeaderRow,"", 0.4, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow,"Rating", 0.25, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow,"Certainty", 0.25, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow,"Year", 0.1, R.style.TextViewDataTableParent)

        mCDEDetailsTable.addView(tableHeaderRow)

        // Add the data
        addClassificationRow(CDEPoint.OVERALL, cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.OVERALL], true)

        addClassificationRow(CDEPoint.ECOLOGICAL, cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.ECOLOGICAL], true)
        for (item in cdePoint.getClassificationHashMap(CDEPoint.ECOLOGICAL)) {
            addClassificationRow(item.key, item.value)
        }

        addClassificationRow(CDEPoint.CHEMICAL, cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.CHEMICAL], true)
        for (item in cdePoint.getClassificationHashMap(CDEPoint.CHEMICAL)) {
            addClassificationRow(item.key, item.value)
        }
    }

    fun setRNAGText(cdePoint: CDEPoint) {
        // Adds header row
        val tableHeaderRow = newTableRow()

        addTextView(tableHeaderRow, "Element", 0.25, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Rating", 0.25, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Activity", 0.4, R.style.TextViewDataTableParent)
        addTextView(tableHeaderRow, "Year", 0.1, R.style.TextViewDataTableParent)

        mRNAGTable.addView(tableHeaderRow)

        for(rnag in cdePoint.rnagList) {

            val tableRow = newTableRow()

            addTextView(tableRow, rnag.element, 0.25)
            addTextView(tableRow, rnag.rating, 0.25)
            addTextView(tableRow, rnag.activity, 0.4)
            addTextView(tableRow, rnag.year.toString(), 0.1)

            mRNAGTable.addView(tableRow)
        }

    }

    fun newTableRow() : TableRow {
        val tableRow: TableRow = TableRow(mDataViewActivity)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)

        tableRow.layoutParams = lp
        tableRow.gravity = Gravity.CENTER
        tableRow.setPadding(3, 10, 3, 0)

        return tableRow
    }

    fun addClassificationRow(label: String, classification: Classification?, isParent : Boolean = false) {
        if (classification !is Classification) return

        val tableRow: TableRow = newTableRow()
        if (isParent) {
            addTextView(tableRow, CDEPoint.classificationPrintValues[label], 0.4,
                    R.style.TextViewDataTableParent, Gravity.START)
        } else {
            addTextView(tableRow, CDEPoint.classificationPrintValues[label], 0.4,
                    R.style.TextViewDataTableParent, Gravity.START, 40)
        }
        addTextView(tableRow, CDEPoint.ratingPrintValues[classification.value], 0.25)
        addTextView(tableRow, classification.certainty, 0.25)
        addTextView(tableRow, classification.year, 0.1)

        mCDEDetailsTable.addView(tableRow);
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

    fun <T : View> Activity.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }

    fun <T : View> View.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }
}