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
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView

/**
 * Created by mihajlo on 07/07/17.
 */

open class CDEDataFragment : Fragment() {
    private lateinit var mToolbar: Toolbar  // The toolbar.
    private lateinit var mBackButton: ImageButton
    private lateinit var mCDEDataView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCDEDataFragment : CDEDataFragment
    private lateinit var mDataViewActivity: DataViewActivity

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

        val cdePoint = mDataViewActivity.selectedCDEPoint
        CDEPointRatingsAPI(this).execute(cdePoint, CDEPoint.OVERALL)

        val cdePointLabel : TextView = view.bind(R.id.cd_label)
        val label = cdePoint.label
        cdePointLabel.text = label

        mToolbar = view.bind(R.id.cde_toolbar)

        mBackButton = view.bind(R.id.back_button_cde_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        //        setClassificationText(cdePoint);

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
                "overall" -> classification = cdePoint.classificationHashMap[CDEPoint.OVERALL]
                "chemical" -> classification = cdePoint.classificationHashMap[CDEPoint.CHEMICAL]
                "ecological" -> classification = cdePoint.classificationHashMap[CDEPoint.ECOLOGICAL]
                else -> {
                    Log.i("FAIL","FAILED TO CONVERT RATING")
                }
            }

            year.text      = classification!!.year
            value.text     = classification.value
            certainty.text = classification.certainty
        }

        val ecologicalRow : TableRow = mCDEDataView.bind(R.id.cde_table_ecological_row)
        ecologicalRow.setOnClickListener { view ->
            view.setBackgroundColor(Color.YELLOW)
            CDEPointRatingsAPI(mCDEDataFragment).execute(cdePoint, CDEPoint.ECOLOGICAL)
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

    fun getResId(resName: String, c: Class<*>): Int {
        try {
            val idField = c.getDeclaredField(resName)
            return idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

    }

    fun setSubClassificationText(cdePoint: CDEPoint) {
        // Set table rows for subset
    }
}