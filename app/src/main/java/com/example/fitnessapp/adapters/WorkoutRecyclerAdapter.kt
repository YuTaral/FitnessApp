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
import com.example.fitnessapp.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale

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

    /** Class to represent workout item view holder - each workout */
    class WorkoutItem(view: View): RecyclerView.ViewHolder(view)  {
        private var name: TextView = itemView.findViewById(R.id.workout_name_txt)
        private var date: TextView = itemView.findViewById(R.id.workout_date_txt)
        private var summary: TextView = itemView.findViewById(R.id.workout_exercises_summary_txt)
        private var total: TextView = itemView.findViewById(R.id.workout_total_txt)

        @SuppressLint("SetTextI18n")
        fun bind(item: WorkoutModel, onClickCallback: (WorkoutModel) -> Unit) {
            var textSummary = ""
            var totalKg = 0.0
            var totalReps = 0

            for (e: ExerciseModel in item.exercises) {
                textSummary = textSummary + e.name + ", "

                for (s : SetModel in e.sets) {
                    totalKg += s.weight
                    totalReps += s.reps
                }
            }

            name.text = item.name
            date.text = SimpleDateFormat("dd/MMM/yyyy", Locale.US).format(item.date)
            summary.text = textSummary.dropLast(2)
            total.text = String.format(Utils.getContext().getText(R.string.workout_summary_lbl).toString(),
                         totalKg,
                         totalReps)

            // Add click listener to select the workout
            itemView.setOnClickListener {
                onClickCallback(item)
            }
        }
    }

}