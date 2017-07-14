package com.bitbusters.android.speproject

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
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
    private lateinit var isDataLoaded: HashMap<String,Boolean>

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

        CDEPointRatingsAPI(this).execute(cdePoint, CDEPoint.REAL)
        CDEPointRatingsAPI(this).execute(cdePoint, CDEPoint.PREDICTED)
        CDEPointRatingsAPI(this).execute(cdePoint, CDEPoint.OBJECTIVE)

        if(cdePoint.rnagList.isEmpty()) {
            CDERnagAPI(this).execute(cdePoint)
        }

        val cdePointLabel : TextView = view.bind(R.id.cde_label)
        val label = cdePoint.label
        cdePointLabel.text = label

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

        Log.i("CREATE", "I AM CREATED")

        return view
    }

    override fun onStart() {
        Log.i("START", "I AM WORKING")
        Log.i("START", isDataLoaded.entries.toString())
        super.onStart()
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

            year.text = classification!!.year
            year.gravity = Gravity.CENTER
            value.text = classification.value
            value.gravity = Gravity.CENTER
            certainty.text = classification.certainty
            certainty.gravity = Gravity.CENTER

            isDataLoaded[CDEPoint.REAL] = true
        }
    }

    fun classificationPopulated(classification: String) {
        isDataLoaded[classification] = true
        if (isDataLoaded.all { entry: Map.Entry<String, Boolean> ->  entry.value }) {
            mMoreInfoButton.visibility = View.VISIBLE
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