package com.bitbusters.android.speproject

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.net.URL
import android.content.Intent
import android.net.Uri


/**
 * Created by mihajlo on 07/07/17.
 */

open class CDEDetailsFragment : FragmentHelper() {
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mFullReportButton: Button
    private lateinit var mCDEDetailsView: View
    private lateinit var mRealClassificationTable: TableLayout
    private lateinit var mObjectivesTable: TableLayout
    private lateinit var mRNAGTable: TableLayout
    private lateinit var mLinearLayout: LinearLayout
    private lateinit var mCDEDetailsFragment: CDEDetailsFragment
    private lateinit var mDataViewActivity: DataViewActivity


    companion object {
        private val TAG = "CDE_DETAILS_FRAGMENT"
        private val URL_PREFIX = "http://environment.data.gov.uk/catchment-planning/WaterBody/"
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

        mRealClassificationTable = view.bind(R.id.cde_details_table)
        mRNAGTable = view.bind(R.id.cde_rnag_table)
        mObjectivesTable = view.bind(R.id.cde_objectives_table)

        mRealClassificationTable.removeAllViewsInLayout()
        mRNAGTable.removeAllViewsInLayout()
        mObjectivesTable.removeAllViewsInLayout()

        val cdePoint = mDataViewActivity.selectedCDEPoint

        val cdePointLabel: TextView = view.bind(R.id.cde_details_title)
        cdePointLabel.text = cdePoint.label

        mToolbar = view.bind(R.id.cde_details_toolbar)

        mLinearLayout = view.bind(R.id.cde_details_linear_layout)

        mFullReportButton = view.bind(R.id.cde_full_report_button)
        mFullReportButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(URL_PREFIX + cdePoint.waterbodyId)
            startActivity(intent)
        }

        mBackButton = view.bind(R.id.back_button_cde_details_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        setRealClassificationText(cdePoint)
        setObjectivePredictedClassificationText(cdePoint)
        setRNAGText(cdePoint)

        return view
    }

    fun setRealClassificationText(cdePoint: CDEPoint) {
        var rowIndex = 0
        // Set Header Row
        val tableHeaderRow = newTableRow(rowIndex++)

        addTextView(tableHeaderRow,"", 0.35, R.style.text_view_table_parent)
        addTextView(tableHeaderRow,"Rating", 0.275, R.style.text_view_table_parent)
        addTextView(tableHeaderRow,"Certainty", 0.225, R.style.text_view_table_parent)
        addTextView(tableHeaderRow,"Year", 0.15, R.style.text_view_table_parent)

        mRealClassificationTable.addView(tableHeaderRow)

        // Add the data
        addClassificationRow(rowIndex++, cdePoint, CDEPoint.OVERALL, true, true)

        addClassificationRow(rowIndex++, cdePoint, CDEPoint.ECOLOGICAL, true, true)
        for (item in CDEPoint.ecologicalGroup) {
            addClassificationRow(rowIndex++, cdePoint, item)
        }

        addClassificationRow(rowIndex++, cdePoint, CDEPoint.CHEMICAL, true, true)
        for (item in CDEPoint.chemicalGroup) {
            addClassificationRow(rowIndex++, cdePoint, item)
        }
    }

    fun setObjectivePredictedClassificationText(cdePoint: CDEPoint) {
        var rowIndex = 0
        // Set Header Rows
        var tableHeaderRow = newTableRow(rowIndex++)

        addTextView(tableHeaderRow, "", 0.35, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Objective", 0.325, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Predicted", 0.325, R.style.text_view_table_parent)

        mObjectivesTable.addView(tableHeaderRow)

        tableHeaderRow = newTableRow(rowIndex++)

        addTextView(tableHeaderRow, "", 0.35, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Rating", 0.19, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Year", 0.135, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Rating", 0.19, R.style.text_view_table_parent)
        addTextView(tableHeaderRow, "Year", 0.135, R.style.text_view_table_parent)

        mObjectivesTable.addView(tableHeaderRow)

        // Add the data
        addClassificationRow(rowIndex++, cdePoint, CDEPoint.OVERALL, false, true)

        addClassificationRow(rowIndex++, cdePoint, CDEPoint.ECOLOGICAL, false, true)
        for (item in CDEPoint.ecologicalGroup) {
            addClassificationRow(rowIndex++, cdePoint, item, false)
        }

        addClassificationRow(rowIndex++, cdePoint, CDEPoint.CHEMICAL, false, true)
        for (item in CDEPoint.chemicalGroup) {
            addClassificationRow(rowIndex++, cdePoint, item, false)
        }
    }

    fun setRNAGText(cdePoint: CDEPoint) {
        val rnagTitle: TextView = mCDEDetailsView.bind(R.id.cde_rnag_title)
        if(cdePoint.rnagList.isNotEmpty()) {
            var rowIndex = 0
            // Adds header row
            val tableHeaderRow = newTableRow(rowIndex++)

            addTextView(tableHeaderRow, "Element", 0.25, R.style.text_view_table_parent)
            addTextView(tableHeaderRow, "Rating", 0.25, R.style.text_view_table_parent)
            addTextView(tableHeaderRow, "Activity", 0.39, R.style.text_view_table_parent)
            addTextView(tableHeaderRow, "Year", 0.11, R.style.text_view_table_parent)

            mRNAGTable.addView(tableHeaderRow)

            for (rnag in cdePoint.rnagList) {

                val tableRow = newTableRow(rowIndex++)

                addTextView(tableRow, rnag.element, 0.25)
                addTextView(tableRow, rnag.rating, 0.25)
                addTextView(tableRow, rnag.activity, 0.39)
                addTextView(tableRow, rnag.year.toString(), 0.11)

                mRNAGTable.addView(tableRow)
            }
        } else {
            rnagTitle.visibility = View.GONE
            mRNAGTable.visibility = View.GONE
        }
    }

    fun addClassificationRow(rowIndex: Int, cdePoint: CDEPoint, label: String, isReal: Boolean = true, isParent : Boolean = false) {
        // First column is always a classification label
        val tableRow: TableRow = newTableRow(rowIndex)

        if (isParent) {
            addTextView(tableRow, CDEPoint.classificationPrintValues[label], 0.35,
                    R.style.text_view_table_parent, Gravity.START)
        } else {
            addTextView(tableRow, CDEPoint.classificationPrintValues[label], 0.35,
                    R.style.text_view_table_parent, Gravity.START, 40)
        }

        if (isReal) {
            val classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[label] ?:
                    Classification("N/A", "N/A", "N/A")

            addTextView(tableRow, CDEPoint.ratingPrintValues[classification.value], 0.275)
            addTextView(tableRow, classification.certainty, 0.225)
            addTextView(tableRow, classification.year, 0.15)

            mRealClassificationTable.addView(tableRow)
        } else {
            var classification = cdePoint.getClassificationHashMap(CDEPoint.OBJECTIVE)[label] ?:
                    Classification("N/A", "N/A", "N/A")
            addTextView(tableRow, CDEPoint.ratingPrintValues[classification.value], 0.19)
            addTextView(tableRow, classification.year, 0.135)

            classification = cdePoint.getClassificationHashMap(CDEPoint.PREDICTED)[label] ?:
                    Classification("N/A", "N/A", "N/A")
            addTextView(tableRow, CDEPoint.ratingPrintValues[classification.value], 0.19)
            addTextView(tableRow, classification.year, 0.135)

            mObjectivesTable.addView(tableRow)
        }
    }
}