package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the data (exercises) shown for each muscle group */
class MGExercisesRecyclerAdapter(data: List<MGExerciseModel>, callback: (MGExerciseModel) -> Unit): RecyclerView.Adapter<MGExercisesRecyclerAdapter.ExerciseItem>() {
    private var exercises: MutableList<MGExerciseModel> = mutableListOf()
    private var onClickCallback: (MGExerciseModel) -> Unit

    init {
        exercises.addAll(data)
        onClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseItem {
        return ExerciseItem(LayoutInflater.from(parent.context).inflate(R.layout.inflatable_mg_exercise_item, parent, false))
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ExerciseItem, position: Int) {
        holder.bind(exercises[position], onClickCallback)
    }

    /** Updates the exercises with the provided data
     * @param data the new exercises
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<MGExerciseModel>) {
        exercises.clear()
        exercises.addAll(data)
        notifyDataSetChanged()
    }

    /** Class to represent exercise item view holder - each exercise */
    class ExerciseItem(view: View): RecyclerView.ViewHolder(view) {
        private var name: TextView = itemView.findViewById(R.id.exercise_name_txt)
        private var description: TextView = itemView.findViewById(R.id.exercise_description_txt)
        private var expandCollapse: ImageView = itemView.findViewById(R.id.exercise_expand_collapse_symbol)

        /** Set the data for each exercise
         * @param item the exercise
         * @param onClickCallback callback to execute on click
         */
        fun bind(item: MGExerciseModel, onClickCallback: (MGExerciseModel) -> Unit) {
            name.text = item.name

            if (item.description.isEmpty()) {
                description.text = Utils.getContext().getString(R.string.no_description_for_ex_lbl)
                description.setTypeface(null, Typeface.ITALIC)
            } else {
                description.text = item.description
                description.typeface = null
            }

            name.setOnClickListener { onClickCallback(item) }
            description.setOnClickListener { onClickCallback(item) }

            // Add expand mechanism
            expandCollapse.setOnClickListener {
                if (description.visibility == View.VISIBLE) {
                    Utils.collapseView(description)
                    expandCollapse.animate().rotation(180f).setDuration(350).start()
                } else {
                    Utils.expandView(description)
                    expandCollapse.animate().rotation(360f).setDuration(350).start()
                }
            }
        }
    }
}