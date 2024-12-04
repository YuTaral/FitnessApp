package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the workouts data shown in the main panel */
class WorkoutRecyclerAdapter (data: List<WorkoutModel>, onClick: (WorkoutModel) -> Unit) : RecyclerView.Adapter<WorkoutRecyclerAdapter.WorkoutItem>() {
    private var workouts: MutableList<WorkoutModel> = mutableListOf()
    private var onClickCallback: (WorkoutModel) -> Unit

    init {
        workouts.addAll(data)
        onClickCallback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutItem {
        return WorkoutItem(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.inflatable_workout_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    override fun onBindViewHolder(holder: WorkoutItem, position: Int) {
        holder.bind(workouts[position], onClickCallback)
    }

    /** Remove the specified template and re-populates the recycler */
    @SuppressLint("NotifyDataSetChanged")
    fun removeTemplate(template: WorkoutModel) {
        workouts.remove(template)
        notifyDataSetChanged()
    }

    /** Class to represent workout item view holder - each workout */
    class WorkoutItem(view: View): RecyclerView.ViewHolder(view)  {
        private var name: TextView = itemView.findViewById(R.id.workout_name_txt)
        private var date: TextView = itemView.findViewById(R.id.workout_date_txt)
        private var exercisesLlb: TextView = itemView.findViewById(R.id.exercises_lbl)
        private var exercises: TextView = itemView.findViewById(R.id.workout_exercises_summary_txt)
        private var total: TextView = itemView.findViewById(R.id.workout_total_txt)

        @SuppressLint("SetTextI18n")
        fun bind(item: WorkoutModel, onClickCallback: (WorkoutModel) -> Unit) {
            var exercisesText = ""
            var totalWeight = 0.0
            var totalReps = 0
            var completedWeight = 0.0
            var completedReps = 0

            for (e: ExerciseModel in item.exercises) {
                exercisesText = exercisesText + e.name + ", "

                for (s : SetModel in e.sets) {
                    totalWeight += s.weight
                    totalReps += s.reps

                    if (s.completed) {
                        completedWeight += s.weight
                        completedReps += s.reps
                    }
                }
            }

            name.text = item.name
            date.text = Utils.defaultFormatDate(item.date)

            if (exercisesText.length > 2) {
                exercisesLlb.visibility = View.VISIBLE
                exercises.visibility = View.VISIBLE
                exercises.text = exercisesText.dropLast(2)
            } else {
                exercisesLlb.visibility = View.GONE
                exercises.visibility = View.GONE
            }

            total.text = String.format(Utils.getContext().getText(R.string.workout_summary_lbl).toString(),
                        Utils.formatDouble(completedWeight),  Utils.formatDouble(totalWeight),
                        StateEngine.user!!.defaultValues.weightUnit.text, completedReps, totalReps)

            // Add click listener to select the workout
            itemView.setOnClickListener {
                onClickCallback(item)
            }
        }
    }

}