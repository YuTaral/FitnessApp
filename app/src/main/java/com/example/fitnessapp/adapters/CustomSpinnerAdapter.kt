package com.example.fitnessapp.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fitnessapp.R

/** Custom Spinner adapter to override ArrayAdapter used in Spinners
 * @param context the context
 * @param selectOptionExists true if the 1st item in the dropdown is not valid selection and should
 * not be bolded (e.g first option is called 'Select option')
 * @param data the items in the adapter
 */
class CustomSpinnerAdapter(context: Context, selectOptionExists: Boolean, data: List<String>) :
    ArrayAdapter<String>(context, R.layout.custom_spinner_style, R.id.item_text, data) {

    /** The items in the dropdown */
    private var items: List<String> = data

    /** True if the first item should not be bolded, false otherwise */
    private val removeBoldFirst: Boolean = selectOptionExists

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_style, parent, false)
        val textView = view.findViewById<TextView>(R.id.item_text)

        // Set the selected item
        textView.text = items[position]

        if (removeBoldFirst && position == 0) {
            // Remove the bold of the first item
            textView.typeface = null
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_style, parent, false)
        val textView = view.findViewById<TextView>(R.id.item_text)
        val icon = view.findViewById<ImageView>(R.id.expand_collapse_symbol)

        textView.text = items[position]

        if (position == 0) {
            // Start rotation effect
            icon.animate().rotation(180f).setDuration(350).start()

            if (removeBoldFirst) {
                textView.typeface = null
            }

        } else {
            // Remove the icon if it's not the first element
            icon.visibility = View.GONE
        }

        return view
    }
}
