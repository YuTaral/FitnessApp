package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the muscle groups shown when adding new workout */
class MuscleGroupsRecAdapter(data: MutableList<MuscleGroupModel>, callback: (muscleGroup: MuscleGroupModel) -> Unit):
    RecyclerView.Adapter<MuscleGroupsRecAdapter.ViewHolder>() {

        private var muscleGroups: MutableList<MuscleGroupModel>
        private var filteredMuscleGroups: MutableList<MuscleGroupModel>
        private var onSelectCallback: (muscleGroup: MuscleGroupModel) -> Unit = callback

    init {
        muscleGroups = data
        filteredMuscleGroups = muscleGroups
        onSelectCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_muscle_group_item, parent, false))
    }

    override fun getItemCount(): Int {
        return filteredMuscleGroups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredMuscleGroups[position], onSelectCallback)
    }

    /** Filter the muscle groups by the provided text */
    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        filteredMuscleGroups = if (text.isEmpty()) {
            muscleGroups
        } else {
            muscleGroups.filter { it.name.lowercase().contains(text) }.toMutableList()
        }

        notifyDataSetChanged()
    }

    /** Class to represent muscle group item view holder */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var image: ImageView = itemView.findViewById(R.id.mg_image)
        private var name: TextView = itemView.findViewById(R.id.mg_name_txt)

        /** Binds the view
         * @param item the muscle group
         */
        @SuppressLint("DiscouragedApi")
        fun bind(item: MuscleGroupModel, onSelectCallback: (muscleGroup: MuscleGroupModel) -> Unit) {
            val ctx = Utils.getActivity()
            val resourceId = ctx.resources.getIdentifier(item.imageName, "drawable", ctx.packageName)

            if (resourceId != 0) { // Check if resource exists
                image.setBackgroundResource(resourceId)
            } else {
                image.setBackgroundResource(R.drawable.icon_mg_not_found)
            }

            name.text = item.name

            // Set the on click listener
            itemView.setOnClickListener { onSelectCallback(item) }
        }
    }
}