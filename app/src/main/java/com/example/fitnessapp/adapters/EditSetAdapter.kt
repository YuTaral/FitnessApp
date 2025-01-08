package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.fitnessapp.R
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.utils.Utils

/** Adapter to control the data (sets) when editing exercise from workout. Usually in the app
 * recycler adapter is used, but here we need a different approach as the data in the rows
 * is changeable and if using notifyDataSetChanged() there is inconsistency as the sets
 * list would hold the initial data. Store the data in linear layout, it's unlikely
 * that each exercise will have large amounts of sets, so it's fine not using recycler adapter.
 */
class EditSetAdapter(container: LinearLayout, data: MutableList<SetModel>) {
    private var sets = data
    private var parent = container

    init {
        sets.map { addRow(it) }
    }

    /** Add row to the container
     * @param set the set model
     */
    @SuppressLint("InflateParams")
    private fun addRow(set: SetModel) {
        val row = LayoutInflater.from(Utils.getActivity()).inflate(R.layout.inflatable_edit_set, null)
        val removeSet: ImageView = row.findViewById(R.id.remove_set)
        val reps: EditText = row.findViewById(R.id.reps_number_txt)
        val weight: EditText = row.findViewById(R.id.weight_txt)
        val rest: EditText = row.findViewById(R.id.rest_txt)
        val completed: CheckBox = row.findViewById(R.id.completed)

        completed.isChecked = set.completed

        if (set.reps > 0) {
            reps.setText(set.reps.toString())
        }

        if (set.weight > 0) {
            weight.setText(Utils.formatDouble(set.weight))
        }

        if (set.rest > 0) {
            rest.setText(set.rest.toString())
        }

        setDeletableIndicator(set.deletable, completed, removeSet, row)

        // Store the id as tag
        row.tag = set.id

        parent.addView(row)
    }

    /** Set the views indicating whether the row is deletable at the moment
     * @param value true if deletable, false otherwise
     * @param completed the completed checkbox view
     * @param removeSet the remove set image view
     * @param row the row
     */
    private fun setDeletableIndicator(value: Boolean, completed: CheckBox, removeSet: ImageView, row: View) {
        if (value) {
            completed.visibility = View.INVISIBLE
            removeSet.visibility = View.VISIBLE
            removeSet.setOnClickListener {
                parent.removeView(row)
            }
        } else {
            completed.visibility = View.VISIBLE
            removeSet.visibility = View.GONE
            removeSet.setOnClickListener(null)
        }
    }

    /** Return list of sets based on the populated data */
    fun getSetsData(): MutableList<SetModel> {
        val sets: MutableList<SetModel> = mutableListOf()

        for (row: View in parent.children) {
            val id = (row.tag as Long)
            val completed = row.findViewById<CheckBox>(R.id.completed).isChecked
            val repsVal = row.findViewById<EditText>(R.id.reps_number_txt).text.toString()
            val restVal = row.findViewById<EditText>(R.id.rest_txt).text.toString()
            val weightVal = row.findViewById<EditText>(R.id.weight_txt).text.toString()
            var reps = 0
            var weight = 0.0
            var rest = 0

            if (repsVal.isNotEmpty()) {
                reps = repsVal.toInt()
            }

            if (weightVal.isNotEmpty()) {
                weight = weightVal.toDouble()
            }

            if (restVal.isNotEmpty()) {
                rest = restVal.toInt()
            }

            sets.add(SetModel(id, reps, weight, rest, completed, false))
        }

        return sets
    }

    /** Add new set and binds the data
     * @param set the new set
     */
    fun addSet(set: SetModel) {
        sets.add(set)
        addRow(set)
    }

    /** Mark all sets as deletable/not deletable, used to allow the user to delete set
     * @param value true if the sets should be marked as deletable, false if not
     */
    fun markAllDeletable(value: Boolean) {
        // Mark all models as deletable/not deletable and update the border
        sets.map { it.deletable = value }

        for (row: View in parent.children) {
            setDeletableIndicator(value, row.findViewById(R.id.completed), row.findViewById(R.id.remove_set), row)
        }
    }
}