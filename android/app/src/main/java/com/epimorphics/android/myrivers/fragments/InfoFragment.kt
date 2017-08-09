package com.epimorphics.android.myrivers.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.helpers.FragmentHelper

/**
 * A fragment occupying full screen showcasing information about what the app is and how to use it.
 * It is initiated by clicking a "How it Works" button in the drawer
 *
 * @see <a href="https://github.com/alessio-b-zak/myRivers/blob/master/graphic%20assets/screenshots/info_view.png">Screenshot</a>
 */
class InfoFragment : FragmentHelper() {
    private lateinit var mBackButton: ImageButton

    /**
     * Called when a fragment is created.
     *
     * @param savedInstanceState Saved state of the fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_info_view, container, false)

        mBackButton = view.bind(R.id.back_button_info_view)
        mBackButton.setOnClickListener { activity.onBackPressed() }

        // Creates a movementMethod for a link to the Government Licence
        val licence: TextView = view.bind(R.id.info_description)
        licence.movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}