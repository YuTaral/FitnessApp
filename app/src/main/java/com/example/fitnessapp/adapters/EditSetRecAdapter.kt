package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the data (sets) when editing exercise from workout */
class EditSetRecAdapter(data: MutableList<SetModel>) : RecyclerView.Adapter<EditSetRecAdapter.ViewHolder>() {
    private var sets = data
    private val viewHolders = mutableListOf<ViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.inflatable_edit_set, parent, false))
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sets[position], onRemove = { v -> removeViewHolder(v) })

        if (!viewHolders.contains(holder)) {
            viewHolders.add(holder)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        viewHolders.remove(holder)
    }

    /** Remove view holder
     * @param holder the view holder to remove
     */
    private fun removeViewHolder(holder: ViewHolder) {
        val index = viewHolders.indexOf(holder)

        sets.removeAt(index)
        notifyItemRemoved(index)
    }

    /** Add new set and binds the data
     * @param model the new set
     */
    fun addSet(model: SetModel) {
        sets.add(model)
        notifyItemInserted(sets.size - 1)
    }

    /** Mark all sets as completed / not completed
     * @param value true to complete all, false to mark all as not completed
     */
    @SuppressLint("NotifyDataSetChanged")
    fun changeCompletedState(value: Boolean) {
        sets.map { it.completed = value }
        notifyDataSetChanged()
    }

    /** Return list of sets based on the populated data */
    fun getSetsData(): MutableList<SetModel> {
        val sets: MutableList<SetModel> = mutableListOf()

        for (viewHolder in viewHolders) {
            sets.add(viewHolder.getSetData())
        }

        return sets
    }

    /** Mark all sets as deletable/not deletable, used to allow the user to delete set
     * @param value true if the sets should be marked as deletable, false if not
     */
    @SuppressLint("NotifyDataSetChanged")
    fun markAllDeletable(value: Boolean) {
        // Mark all models as deletable/not deletable and update the border a
        sets.map { it.deletable = value }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var removeSet: ImageView = itemView.findViewById(R.id.remove_set)
        private var reps: EditText = itemView.findViewById(R.id.reps_number_txt)
        private var weight: EditText = itemView.findViewById(R.id.weight_txt)
        private var rest: EditText = itemView.findViewById(R.id.rest_txt)
        private var completed: CheckBox = itemView.findViewById(R.id.completed)

        /** Populate the data for each set and click listeners
         * @param item the set model
         * */
        fun bind(item: SetModel, onRemove: (ViewHolder) -> Unit) {
            completed.isChecked = item.completed

            if (item.reps > 0) {
                reps.setText(item.reps.toString())
            }

            if (item.weight > 0) {
                weight.setText(Utils.formatDouble(item.weight))
            }

            if (item.rest > 0) {
                rest.setText(item.rest.toString())
            }

            // Save the id as tag
            itemView.tag = item.id

            if (item.deletable) {
                completed.visibility = View.INVISIBLE
                removeSet.visibility = View.VISIBLE
                removeSet.setOnClickListener { onRemove(this) }
            } else {
                completed.visibility = View.VISIBLE
                removeSet.visibility = View.GONE
                removeSet.setOnClickListener(null)
            }
        }

        /** Create set model based on the data at the specified position */
        fun getSetData(): SetModel {
            val id = itemView.tag.toString().toLong()
            val completed = completed.isChecked
            val repsVal = reps.text.toString()
            val restVal = rest.text.toString()
            val weightVal = weight.text.toString()
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

            return SetModel(id, reps, weight, rest, completed)
        }
    }
}