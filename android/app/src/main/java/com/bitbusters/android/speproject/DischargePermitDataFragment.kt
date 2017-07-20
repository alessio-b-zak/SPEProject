package com.bitbusters.android.speproject

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
open class DischargePermitDataFragment : FragmentHelper() {

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

        mToolbar = view.bind(R.id.permit_toolbar)

        mBackButton = view.bind(R.id.back_button_permit_data_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        // Initialise Recycler View and hide it
        mRecyclerView = view.bind(R.id.permit_grid_view)
        mRecyclerView.visibility = View.INVISIBLE

        val permitPoint = mDataViewActivity.selectedPermitPoint

        val permitHolderNameView: TextView = view.bind(R.id.permit_holder_name)
        val effluentTypeView: TextView = view.bind(R.id.permit_table_effluent_type)
        val siteTypeView: TextView = view.bind(R.id.permit_table_site_type)
        val effectiveDateView: TextView = view.bind(R.id.permit_table_effective_date)

        permitHolderNameView.text = permitPoint.holder
        effluentTypeView.text = permitPoint.effluentType
        siteTypeView.text = permitPoint.siteType
        effectiveDateView.text = simplifyDate(permitPoint.effectiveDate)

        return view
    }

}