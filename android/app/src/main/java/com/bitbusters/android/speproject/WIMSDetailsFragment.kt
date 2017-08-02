package com.bitbusters.android.speproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


/**
 * Created by mihajlo on 07/07/17.
 */

open class WIMSDetailsFragment : FragmentHelper() {
    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mWIMSDetailsFragment: WIMSDetailsFragment
    private lateinit var mWIMSDetailsView: View
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mFullReportButton: Button
    private lateinit var mGeneralTable: TableLayout
    private lateinit var mDissolvedOxygenTable: TableLayout
    private lateinit var mOxygenDemandTable: TableLayout
    private lateinit var mNitratesTable: TableLayout
    private lateinit var mPhosphatesTable: TableLayout
    private lateinit var mMetalsTable: TableLayout
    private lateinit var mSolidsTable: TableLayout

    private val TAG = "WIMS_DETAILS_FRAGMENT"
    private val URL_PREFIX = "http://environment.data.gov.uk/water-quality/id/sampling-point/"


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

        mFullReportButton = view.bind(R.id.wims_full_report_button)
        raiseButton(mFullReportButton)
        mFullReportButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(URL_PREFIX + wimsPoint.id + ".html")
            startActivity(intent)
        }

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
                var rowIndex = 0
                // Set Header Row
                var tableHeaderRow = newTableRow(rowIndex++)

                addTextView(tableHeaderRow, "Determinand", 0.3, R.style.text_view_table_parent, Gravity.START)
                addTextView(tableHeaderRow, "Unit", 0.2, R.style.text_view_table_parent)
                addTextView(tableHeaderRow, "Result", 0.2, R.style.text_view_table_parent)
                addTextView(tableHeaderRow, "Date", 0.3, R.style.text_view_table_parent)

                groupTable.addView(tableHeaderRow)

                for (entry: String in groupDeterminandList) {
                    if (wimsPoint.measurementMap.containsKey(entry)) {
                        tableHeaderRow = newTableRow(rowIndex++)

                        addTextView(tableHeaderRow, entry, 0.3, R.style.text_view_table_child, Gravity.START)
                        addTextView(tableHeaderRow, wimsPoint.measurementMap[entry]!![0].unit, 0.2, R.style.text_view_table_child)
                        addTextView(tableHeaderRow, wimsPoint.measurementMap[entry]!![0].result.toString(), 0.2, R.style.text_view_table_child)
                        addTextView(tableHeaderRow, simplifyDate(wimsPoint.measurementMap[entry]!![0].date), 0.3, R.style.text_view_table_child)

                        groupTable.addView(tableHeaderRow)
                    }
                }
            } else {
                groupTitle.visibility = View.GONE
                groupTable.visibility = View.GONE
            }
        }
    }
}