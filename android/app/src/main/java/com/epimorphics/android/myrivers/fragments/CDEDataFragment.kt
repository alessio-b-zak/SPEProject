package com.epimorphics.android.myrivers.fragments

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
import com.epimorphics.android.myrivers.apis.CDERiverLineAPI
import com.epimorphics.android.myrivers.apis.CDERnagAPI
import com.epimorphics.android.myrivers.data.CDEPoint
import com.epimorphics.android.myrivers.data.Classification
import com.epimorphics.android.myrivers.helpers.FragmentHelper


/**
 * A fragment occupying top part of the screen showcasing basic CDEPoint Classifications.
 * It is initiated by clicking a geoJSON shape in DataViewActivity
 *
 * @see <a href="https://github.com/alessio-b-zak/myRivers/blob/master/graphic%20assets/screenshots/cde_data_view.png">Screenshot</a>
 * @see DataViewActivity
 */
open class CDEDataFragment : FragmentHelper() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mBackButton: ImageButton
    private lateinit var mMoreInfoButton: Button
    private lateinit var mCDEDataView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCDEDataFragment: CDEDataFragment
    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mDetailsTable: TableLayout
    private lateinit var isDataLoaded: HashMap<String, Boolean>

    /**
     * Called when a fragment is created. Initiates mDataViewActivity
     *
     * @param savedInstanceState Saved state of the fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val view = inflater!!.inflate(R.layout.fragment_cde_data_view, container, false)
        mCDEDataView = view
        mCDEDataFragment = this

        // Initialise Recycler View and hide it so that map is visible
        mRecyclerView = view.bind(R.id.cde_grid_view)
        mRecyclerView.visibility = View.INVISIBLE

        mDetailsTable = view.bind(R.id.cde_table)

        val cdePoint = mDataViewActivity.selectedCDEPoint
        CDERiverLineAPI(mDataViewActivity).execute(cdePoint)

        val cdePointLabel: TextView = view.bind(R.id.cde_label)
        cdePointLabel.text = cdePoint.label

        mToolbar = view.bind(R.id.cde_toolbar)

        mBackButton = view.bind(R.id.back_button_cde_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        mMoreInfoButton = view.bind(R.id.info_button_cde_data_view)
        mMoreInfoButton.setOnClickListener { mDataViewActivity.openCDEDetailsFragment() }
        mMoreInfoButton.visibility = View.INVISIBLE

        isDataLoaded = HashMap()
        isDataLoaded.put(CDEPoint.REAL, false)
        isDataLoaded.put(CDEPoint.OBJECTIVE, false)
        isDataLoaded.put(CDEPoint.PREDICTED, false)
        isDataLoaded.put(CDEPoint.RNAG, false)

        // Checks if rnagList is populated
        // If it is [then fragment is opened by going back from the CDEDetailsFragment]
        //      data is already loaded and moreInfoButton should be visible
        // else [fragment is initialised by clicking on geoJSON feature in DataViewActivity]
        //      populate rnagList
        if (cdePoint.rnagList.isNotEmpty()) {
            mMoreInfoButton.visibility = View.VISIBLE
        } else {
            CDERnagAPI(this).execute(cdePoint)
        }

        return view
    }

    /**
     * Populates CDEPoint Classification data
     *
     * @param cdePoint CDEPoint containing required data
     */
    fun setClassificationText(cdePoint: CDEPoint) {
        var rowIndex = 0
        // Add header row
        var tableRow = newTableRow(rowIndex++)

        addTextView(tableRow, "", 0.25, R.style.text_view_table_parent)
        addTextView(tableRow, "Rating", 0.30, R.style.text_view_table_parent)
        addTextView(tableRow, "Certainty", 0.30, R.style.text_view_table_parent)
        addTextView(tableRow, "Year", 0.15, R.style.text_view_table_parent)

        mDetailsTable.addView(tableRow)

        // Add data rows
        val ratingList = arrayListOf<String>("Overall", "Ecological", "Chemical")
        for (rating in ratingList) {
            var classification: Classification? = null

            when (rating) {
                "Overall" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.OVERALL]
                "Chemical" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.CHEMICAL]
                "Ecological" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.ECOLOGICAL]
            }

            if (classification != null) {
                tableRow = newTableRow(rowIndex++)

                addTextView(tableRow, rating, 0.25, R.style.text_view_table_parent, Gravity.START)
                addTextView(tableRow, classification.value, 0.30)
                addTextView(tableRow, classification.certainty, 0.30)
                addTextView(tableRow, classification.year, 0.15)

                mDetailsTable.addView(tableRow)

                isDataLoaded[CDEPoint.REAL] = true
            } else {
                continue
            }
        }
    }

    /**
     * Called when classification data for a specific group of classifications is populated from the
     * API call. If all the data is loaded then mMoreInfoButton is set to be visible.
     *
     * @param group a group of classifications populated by the API
     */
    fun classificationPopulated(group: String) {
        isDataLoaded[group] = true
        if (isDataLoaded.all { entry: Map.Entry<String, Boolean> -> entry.value }) {
            mMoreInfoButton.visibility = View.VISIBLE
        }
    }
}