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
class MuscleGroupRecyclerAdapter(data: MutableList<MuscleGroupModel>): RecyclerView.Adapter<MuscleGroupRecyclerAdapter.MGItem>() {
    private var muscleGroups: MutableList<MuscleGroupModel>

    init {
        muscleGroups = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MGItem {
        return MGItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_muscle_group_item, parent, false))
    }

    override fun getItemCount(): Int {
        return muscleGroups.size
    }

    override fun onBindViewHolder(holder: MGItem, position: Int) {
        holder.bind(muscleGroups[position])
    }

    /** Returns the selected Muscle groups */
    fun getSelectedMuscleGroups(): MutableList<MuscleGroupModel> {
        return muscleGroups.filter { it.checked }.toMutableList()
    }

    /** Class to represent muscle group item view holder */
    class MGItem(view: View): RecyclerView.ViewHolder(view) {
        private var selected: CheckBox = itemView.findViewById(R.id.mg_selected_chkb)
        private var name: TextView = itemView.findViewById(R.id.mg_name_txt)

        fun bind(item: MuscleGroupModel) {
            selected.isChecked = item.checked

            selected.setOnClickListener {
                item.checked = selected.isChecked
            }

            name.text = item.name

            // Store the id as tag
            itemView.tag = item.id
        }
    }
}