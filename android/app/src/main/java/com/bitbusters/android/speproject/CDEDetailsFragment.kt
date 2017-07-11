package com.bitbusters.android.speproject

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
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
    private lateinit var mCDEDetailsFragment: CDEDetailsFragment
    private lateinit var mDataViewActivity: DataViewActivity

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

        val cdePointLabel : TextView = view.bind(R.id.cde_details_title)
        cdePointLabel.text = cdePoint.label

        mToolbar = view.bind(R.id.cde_details_toolbar)

        mCDEDetailsTable = view.bind(R.id.cde_details_table)

        mBackButton = view.bind(R.id.back_button_cde_details_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        return view
    }

    fun setSubClassificationText(cdePoint: CDEPoint) {
        var i: Int = 1
        for (entry in cdePoint.getClassificationHashMap(CDEPoint.CHEMICAL)) {
            i = addRow(cdePoint, entry.key, entry.value, i)
        }
        i = addRow(cdePoint, CDEPoint.CHEMICAL,
                cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.CHEMICAL], i, true)
        for (entry in cdePoint.getClassificationHashMap(CDEPoint.ECOLOGICAL)) {
            i = addRow(cdePoint, entry.key, entry.value, i)
        }
        i = addRow(cdePoint, CDEPoint.ECOLOGICAL,
                cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.ECOLOGICAL], i, true)
        i = addRow(cdePoint, CDEPoint.OVERALL,
                cdePoint.getClassificationHashMap(CDEPoint.GENERAL)[CDEPoint.OVERALL], i, true)
    }

    fun addRow(cdePoint: CDEPoint, label: String, classification: Classification?, _i: Int, isParent : Boolean = false): Int {
        var i = _i
        val tableRow: TableRow = TableRow(mDataViewActivity)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
        tableRow.layoutParams = lp

        val classificationView : TextView = TextView(mDataViewActivity)
        classificationView.text = CDEPoint.classificationPrintValues[label]
        classificationView.setTextAppearance(context, R.style.TextViewDataTableParent)
        if(!isParent) {
            classificationView.setPadding(40, 0, 0, 0)
        }

        tableRow.addView(classificationView)

        val ratingView : TextView = TextView(mDataViewActivity)
        ratingView.text = CDEPoint.ratingPrintValues[classification!!.value]
        ratingView.setTextAppearance(context, R.style.TextViewDataTableChild)
        ratingView.gravity = Gravity.CENTER
        ratingView.setPadding(10, 0, 10, 0)
        tableRow.addView(ratingView)

        val certaintyView : TextView = TextView(mDataViewActivity)
        certaintyView.text = classification.certainty
        certaintyView.setTextAppearance(context, R.style.TextViewDataTableChild)
        certaintyView.gravity = Gravity.CENTER
        certaintyView.setPadding(10, 0, 10, 0)
        tableRow.addView(certaintyView)

        val yearView : TextView = TextView(mDataViewActivity)
        yearView.setTextAppearance(context, R.style.TextViewDataTableChild)
        yearView.text = classification.year
        yearView.gravity = Gravity.CENTER
        yearView.setPadding(10, 0, 10, 0)
        tableRow.addView(yearView)

        mCDEDetailsTable.addView(tableRow, i);
        return i++
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