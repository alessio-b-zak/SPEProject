package com.bitbusters.android.speproject

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

/**
 * Created by mihajlo on 18/07/17.
 */
class MyAreaFragment: Fragment() {
    private lateinit var mBackButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_my_area, container, false)

        mBackButton = view.findViewById(R.id.back_button_my_area_view) as ImageButton
        mBackButton.setOnClickListener { activity.onBackPressed() }

        return view
    }
}