package com.bitbusters.android.speproject

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.view.ViewGroup



/**
 * Created by mihajlo on 20/07/17.
 */

abstract class FragmentHelper() : Fragment() {

    fun <T : View> View.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }

    fun newTableRow(position: Int) : TableRow {
        val tableRow: TableRow = TableRow(context)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)

        tableRow.layoutParams = lp
        tableRow.gravity = Gravity.CENTER
        tableRow.setPadding(16, 8, 16, 0)

        when(position % 2) {
            0 -> {
                if (Build.VERSION.SDK_INT < 21) {
                    tableRow.background = resources.getDrawable(R.color.tablePrimary)
                } else {
                    tableRow.background = resources.getDrawable(R.color.tablePrimary, null)
                }
            }
            1 -> {
                if (Build.VERSION.SDK_INT < 21) {
                    tableRow.background = resources.getDrawable(R.color.tableSecondary)
                } else {
                    tableRow.background = resources.getDrawable(R.color.tableSecondary, null)
                }
            }
        }

        return tableRow
    }

    fun addTextView(tableRow: TableRow, value: String?, weight: Double = 1.0,
                    style: Int = R.style.text_view_table_child, gravity: Int = Gravity.CENTER,
                    leftPadding: Int = 0) {
        val textView : TextView = TextView(context)
        val params = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight.toFloat())

        textView.layoutParams = params
        textView.text = value
        textView.gravity = gravity
        textView.setPadding(leftPadding, 0, 0, 0)

        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, style)
        } else {
            textView.setTextAppearance(style)
        }

        tableRow.addView(textView)
    }

    fun simplifyDate(date: String) : String {
        val year = date.substring(2, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        return "$day/$month/$year"
    }
}