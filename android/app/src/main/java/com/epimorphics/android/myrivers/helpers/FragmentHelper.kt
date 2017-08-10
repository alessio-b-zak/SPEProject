package com.epimorphics.android.myrivers.helpers

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
import com.epimorphics.android.myrivers.R


/**
 * Helper class providing base functions used by most fragments
 *
 */
abstract class FragmentHelper : Fragment() {

    /**
     * Used to initialise an element from a resource
     *
     * @param T a type of the element
     * @param res a resource Id
     *
     * @return an element of type T
     */
    fun <T : View> View.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById<T>(res)
    }

    /**
     * Creates a new table row
     *
     * @param position row index
     * @param isDoubleRow set to true if two rows act as one like in WIMSDataFragment
     * @param offset set to 1 if table contains double rows but the first row is a header row
     *
     * @return TableRow new table row
     */
    fun newTableRow(position: Int, isDoubleRow: Boolean = false, offset: Int = 0): TableRow {
        val tableRow: TableRow = TableRow(context)
        val lp = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)

        tableRow.layoutParams = lp
        tableRow.gravity = Gravity.CENTER
        tableRow.setPadding(16, 8, 16, 8)

        // If isDoubleRow then set condition to alternate colors every 2 rows
        val condition: Int = if (isDoubleRow) {
            val inputAsDouble = (position + offset).toDouble()
            (Math.floor(inputAsDouble / 2)).toInt() % 2
        } else {
            position % 2
        }

        // Set table row background
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

    /**
     * Adds a TextView to a TableRow
     *
     * @param tableRow a TableRow to which to add a TextView
     * @param value a String to be added to the TextView
     * @param weight a weighting of the TextView in the given TableRow
     * @param style a resource id of a TextView style
     * @param gravity a gravity of a TextView inside a TableRow
     * @param startPadding a paddingStart property of a TextView
     * @param popupText a popup window text
     *
     */
    fun addTextView(tableRow: TableRow, value: String?, weight: Double = 1.0,
                    style: Int = R.style.text_view_table_child, gravity: Int = Gravity.CENTER,
                    startPadding: Int = 0, popupText: String = "") {

        val textView: TextView = TextView(context)
        val params = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight.toFloat())

        textView.layoutParams = params
        textView.text = value
        textView.gravity = gravity

        // basePadding is set to a different value for mobile and tabled devices
        val basePadding = context.resources.getDimensionPixelSize(R.dimen.table_base_padding)
        textView.setPaddingRelative(basePadding + startPadding, 0, basePadding, 0)

        // set the style of the TextView
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, style)
        } else {
            textView.setTextAppearance(style)
        }

        // if popupText provided then initiate a popup window
        if (popupText != "") {
            textView.setOnClickListener { view: View -> displayPopupWindow(view, popupText) }
        }

        tableRow.addView(textView)
    }

    /**
     * Attaches a popup window to a given view
     *
     * @param view a view to which a popup window is to be attached
     * @param inputString a string to be shown inside a popup window
     */
    fun displayPopupWindow(view: View, inputString: String) {
        val layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(R.layout.popup_content, null)

        // Initiate a popup window
        val popup = PopupWindow(context)
        popup.contentView = layout

        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT

        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        // Set Text
        val textView: TextView = popup.contentView.findViewById<TextView>(R.id.pop_up_window)
        textView.text = inputString

        // Show anchored to button
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
    }

    /**
     * Converts a date from ISO format to Short Date format
     *
     * @param date date in ISO format
     * @return String date in Short Date format
     */
    fun simplifyDate(date: String): String {
        val year = date.substring(2, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8, 10)

        return "$day/$month/$year"
    }
}