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

    /** Class to represent muscle group item view holder */
    class MGItem(view: View): RecyclerView.ViewHolder(view) {
        private var image: ImageView = itemView.findViewById(R.id.mg_image)
        private var name: TextView = itemView.findViewById(R.id.mg_name_txt)
        private var imageNext: ImageView = itemView.findViewById(R.id.image_next)

        /** Binds the view
         * @param item the muscle group
         */
        @SuppressLint("DiscouragedApi")
        fun bind(item: MuscleGroupModel) {
            val ctx = Utils.getContext()
            val resourceId = ctx.resources.getIdentifier(item.imageName, "drawable", ctx.packageName)

            if (resourceId != 0) { // Check if resource exists
                image.setBackgroundResource(resourceId)
            }

            name.text = item.name

            // Store the id as tag
            itemView.tag = item.id
        }
    }
}