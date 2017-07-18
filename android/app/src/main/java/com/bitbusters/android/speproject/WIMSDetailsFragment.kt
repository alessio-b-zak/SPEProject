package com.bitbusters.android.speproject

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text


/**
 * Created by mihajlo on 07/07/17.
 */

open class WIMSDetailsFragment : Fragment() {
    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mWIMSDetailsFragment: WIMSDetailsFragment
    private lateinit var mWIMSDetailsView: View
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mGeneralTable: TableLayout
    private lateinit var mDissolvedOxygenTable: TableLayout
    private lateinit var mOxygenDemandTable: TableLayout
    private lateinit var mNitratesTable: TableLayout
    private lateinit var mPhosphatesTable: TableLayout
    private lateinit var mMetalsTable: TableLayout
    private lateinit var mSolidsTable: TableLayout

    private val TAG = "WIMS_DETAILS_FRAGMENT"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_wims_details_view, container, false)
        mWIMSDetailsView = view
        mWIMSDetailsFragment = this

        mGeneralTable = view.bind(R.id.wims_details_general_table)
        mDissolvedOxygenTable = view.bind(R.id.wims_details_diss_oxygen_table)
        mOxygenDemandTable = view.bind(R.id.wims_details_oxygen_demand_table)
        mNitratesTable = view.bind(R.id.wims_details_nitrates_table)
        mPhosphatesTable = view.bind(R.id.wims_details_phosphates_table)
        mMetalsTable = view.bind(R.id.wims_details_metals_table)
        mSolidsTable = view.bind(R.id.wims_details_solids_table)

        val wimsPoint = mDataViewActivity.selectedWIMSPoint

        val wimsPointLabel : TextView = view.bind(R.id.wims_details_title)
        wimsPointLabel.text = wimsPoint.label

        mToolbar = view.bind(R.id.wims_details_toolbar)

        mBackButton = view.bind(R.id.back_button_wims_details_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        setText(wimsPoint)

        return view
    }

    fun setText(wimsPoint: WIMSPoint) {
        val groupList = arrayListOf<String>("general", "diss_oxygen", "oxygen_demand",
                                             "nitrates", "phosphates", "metals", "solids");

        for(group in groupList) {
            var groupDeterminandList = ArrayList<String>()
            var groupTable = mGeneralTable
            val groupTitle : TextView = mWIMSDetailsView.bind(resources.getIdentifier(
                                        "wims_details_" + group + "_title",
                                        "id", mDataViewActivity.packageName))

            when (group) {
                "general" -> {
                    groupDeterminandList = WIMSPoint.generalGroup
                    groupTable = mGeneralTable
                }
                "diss_oxygen" -> {
                    groupDeterminandList = WIMSPoint.dissolvedOxygenGroup
                    groupTable = mDissolvedOxygenTable
                }
                "oxygen_demand" -> {
                    groupDeterminandList = WIMSPoint.oxygenDemandGroup
                    groupTable = mOxygenDemandTable
                }
                "nitrates" -> {
                    groupDeterminandList = WIMSPoint.nitrateGroup
                    groupTable = mNitratesTable
                }
                "phosphates" -> {
                    groupDeterminandList = WIMSPoint.phosphateGroup
                    groupTable = mPhosphatesTable
                }
                "solids" -> {
                    groupDeterminandList = WIMSPoint.solidGroup
                    groupTable = mSolidsTable
                }
                else -> {
                    val filterMap = wimsPoint.measurementMap.filterKeys {
                        key : String -> !WIMSPoint.nonMetalGroup.contains(key)
                    }
                    groupDeterminandList = ArrayList(filterMap.keys)
                    groupTable = mMetalsTable
                }
            }

            val groupHasRecords = wimsPoint.measurementMap.any {
                entry: Map.Entry<String, ArrayList<Measurement>> ->  groupDeterminandList.contains(entry.key)
            }

            if(groupHasRecords) {
                // Set Header Row
                var tableHeaderRow = newTableRow()

                addTextView(tableHeaderRow, "Determinand", 0.3, R.style.TextViewDataTableParent, Gravity.START)
                addTextView(tableHeaderRow, "Unit", 0.2, R.style.TextViewDataTableParent)
                addTextView(tableHeaderRow, "Result", 0.2, R.style.TextViewDataTableParent)
                addTextView(tableHeaderRow, "Date", 0.3, R.style.TextViewDataTableParent)

                groupTable.addView(tableHeaderRow)

                for (entry: String in groupDeterminandList) {
                    if (wimsPoint.measurementMap.containsKey(entry)) {
                        tableHeaderRow = newTableRow()

                        addTextView(tableHeaderRow, entry, 0.3, R.style.TextViewDataTableChild, Gravity.START)
                        addTextView(tableHeaderRow, wimsPoint.measurementMap[entry]!![0].unit, 0.2, R.style.TextViewDataTableChild)
                        addTextView(tableHeaderRow, wimsPoint.measurementMap[entry]!![0].result.toString(), 0.2, R.style.TextViewDataTableChild)
                        addTextView(tableHeaderRow, simplifyDate(wimsPoint.measurementMap[entry]!![0].date), 0.3, R.style.TextViewDataTableChild)

                        groupTable.addView(tableHeaderRow)
                    }
                }
            } else {
                groupTitle.visibility = View.GONE
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