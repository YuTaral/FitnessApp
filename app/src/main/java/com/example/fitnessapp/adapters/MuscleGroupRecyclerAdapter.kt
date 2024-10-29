package com.example.fitnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MuscleGroupModel

/** Recycler adapter to control the muscle groups shown when adding new workout */
class MuscleGroupRecyclerAdapter(data: MutableList<MuscleGroupModel>, enableAll: Boolean): RecyclerView.Adapter<MuscleGroupRecyclerAdapter.MGItem>() {
    private var muscleGroups: MutableList<MuscleGroupModel>
    private var enabled: Boolean

    init {
        muscleGroups = data
        enabled = enableAll
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MGItem {
        return MGItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_muscle_group_item, parent, false))
    }

    override fun getItemCount(): Int {
        return muscleGroups.size
    }

    override fun onBindViewHolder(holder: MGItem, position: Int) {
        holder.bind(muscleGroups[position], enabled)
    }

    /** Returns the selected Muscle groups */
    fun getSelectedMuscleGroups(): MutableList<MuscleGroupModel> {
        return muscleGroups.filter { it.checked }.toMutableList()
    }

    /** Class to represent muscle group item view holder */
    class MGItem(view: View): RecyclerView.ViewHolder(view) {
        private var selected: CheckBox = itemView.findViewById(R.id.mg_selected_chkb)
        private var name: TextView = itemView.findViewById(R.id.mg_name_txt)

        /** Binds the view
         * @param item the muscle group
         * @param enabled whether the selected checkbox should be enabled
         * */
        fun bind(item: MuscleGroupModel, enabled: Boolean) {
            selected.isChecked = item.checked

            selected.setOnClickListener {
                item.checked = selected.isChecked
            }

            // Set the enabled state
           selected.isEnabled = enabled

            name.text = item.name

            // Store the id as tag
            itemView.tag = item.id
        }
    }
}