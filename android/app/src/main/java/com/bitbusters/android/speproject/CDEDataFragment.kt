package com.bitbusters.android.speproject

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


/**
 * Created by mihajlo on 07/07/17.
 */

open class CDEDataFragment : Fragment() {
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mMoreInfoButton: Button
    private lateinit var mCDEDataView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCDEDataFragment : CDEDataFragment
    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mTableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_cde_data_view, container, false)
        mCDEDataView = view
        mCDEDataFragment = this

        // Initialise Recycler View and hide it
        mRecyclerView = view.bind(R.id.cde_grid_view)
        mRecyclerView.visibility = View.INVISIBLE

        mTableLayout = view.bind(R.id.cde_table)

        val cdePoint = mDataViewActivity.selectedCDEPoint
        CDEPointRatingsAPI(mDataViewActivity).execute(cdePoint, CDEPoint.REAL)

        val cdePointLabel : TextView = view.bind(R.id.cde_label)
        val label = cdePoint.label
        cdePointLabel.text = label

        mToolbar = view.bind(R.id.cde_toolbar)

        mBackButton = view.bind(R.id.back_button_cde_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        mMoreInfoButton = view.bind(R.id.info_button_cde_data_view)
        mMoreInfoButton.setOnClickListener { mDataViewActivity.openCDEDetailsFragment() }

        return view
    }

    fun setClassificationText(cdePoint: CDEPoint) {
        val ratingList = arrayListOf<String>("overall", "ecological", "chemical");

        for(rating in ratingList) {
            val year      : TextView = mCDEDataView.bind(resources.getIdentifier(
                    "cde_table_" + rating + "_year", "id",
                    mDataViewActivity.packageName))
            val value     : TextView = mCDEDataView.bind(resources.getIdentifier(
                    "cde_table_" + rating + "_value", "id",
                    mDataViewActivity.packageName))
            val certainty : TextView = mCDEDataView.bind(resources.getIdentifier(
                    "cde_table_" + rating + "_certainty",
                    "id", mDataViewActivity.packageName))

            var classification : Classification? = null

            when(rating){
                "overall" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.OVERALL]
                "chemical" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.CHEMICAL]
                "ecological" -> classification = cdePoint.getClassificationHashMap(CDEPoint.REAL)[CDEPoint.ECOLOGICAL]
                else -> {
                    Log.i("FAIL","FAILED TO CONVERT RATING")
                }
            }

            year.text      = classification!!.year
            value.text     = classification.value
            certainty.text = classification.certainty
        }
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