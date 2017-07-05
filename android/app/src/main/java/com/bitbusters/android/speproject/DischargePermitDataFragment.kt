package com.bitbusters.android.speproject

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

/**
 * Created by mihajlo on 05/07/17.
 */
open class DischargePermitDataFragment : android.support.v4.app.Fragment() {

    private lateinit var mToolbar : Toolbar
    private lateinit var mBackButton : ImageButton
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDischargePermitDataView: View
    private lateinit var mDataViewActivity: DataViewActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_permit_data_view, container, false)
        mDischargePermitDataView = view

        // Initialise Recycler View and hide it
        mRecyclerView = view.findViewById(R.id.permit_grid_view) as RecyclerView
        mRecyclerView.visibility = View.INVISIBLE

        val permitPoint = mDataViewActivity.selectedPermitPoint

        val permitHolderNameView = view.findViewById(R.id.permit_holder_name) as TextView
        permitHolderNameView.text = permitPoint.holderName

        mToolbar = view.findViewById(R.id.permit_toolbar) as Toolbar

        mBackButton = view.findViewById(R.id.back_button_permit_data_view) as ImageButton
        mBackButton.setOnClickListener { activity.onBackPressed() }

        val siteTypeView = mDischargePermitDataView.findViewById(R.id.permit_table_type) as TextView
        val effectiveDateView = mDischargePermitDataView.findViewById(R.id.permit_table_date) as TextView

        siteTypeView.text = permitPoint.siteType
        effectiveDateView.text = permitPoint.effectiveDate

        return view
    }

}