package com.bitbusters.android.speproject

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

/**
 * Created by mihajlo on 18/07/17.
 */
class InfoFragment: FragmentHelper() {
    private lateinit var mBackButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_info_view, container, false)

        mBackButton = view.findViewById(R.id.back_button_info_view) as ImageButton
        mBackButton.setOnClickListener { activity.onBackPressed() }

        val licence: TextView = view.bind(R.id.info_description)
        licence.movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}