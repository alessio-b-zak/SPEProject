package com.bitbusters.android.speproject.helpers

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TableRow
import android.widget.TextView
import com.bitbusters.android.speproject.R


/**
 * Created by mihajlo on 20/07/17.
 */

abstract class FragmentHelper() : Fragment() {

    fun <T : View> View.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }

    fun newTableRow(position: Int, isDoubleRow: Boolean = false, offset: Int = 0): TableRow {
        val tableRow: TableRow = TableRow(context)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)

        tableRow.layoutParams = lp
        tableRow.gravity = Gravity.CENTER
        tableRow.setPadding(16, 8, 16, 8)

        val condition: Int = if (isDoubleRow) {
            val inputAsDouble = (position + offset).toDouble()
            (Math.floor(inputAsDouble / 2)).toInt() % 2
        } else {
            position % 2
        }

        when (condition) {
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
                    startPadding: Int = 0, popupText: String = "") {
        val textView: TextView = TextView(context)
        val params = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight.toFloat())

        textView.layoutParams = params
        textView.text = value
        textView.gravity = gravity
        val basePadding = context.resources.getDimensionPixelSize(R.dimen.table_base_padding)
        textView.setPaddingRelative(basePadding + startPadding, 0, basePadding, 0)

        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, style)
        } else {
            textView.setTextAppearance(style)
        }

        if (popupText != "") {
            textView.setOnClickListener { view: View -> displayPopupWindow(view, popupText) }
        }

        tableRow.addView(textView)
    }

    fun displayPopupWindow(view: View, inputString: String) {
        val popup = PopupWindow(context)
        val layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(R.layout.popup_content, null)
        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Set Text
        val textView: TextView = popup.contentView.findViewById(R.id.pop_up_window) as TextView
        textView.text = inputString
        // Show anchored to button
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
    }

    fun simplifyDate(date: String): String {
        val year = date.substring(2, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        return "$day/$month/$year"
    }
}