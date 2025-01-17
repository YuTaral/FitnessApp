package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the workouts data shown in the main panel */
class WorkoutsRecAdapter (data: List<WorkoutModel>, onClick: (WorkoutModel) -> Unit) : RecyclerView.Adapter<WorkoutsRecAdapter.ViewHolder>() {
    private var workouts: MutableList<WorkoutModel> = mutableListOf()
    private var filteredWorkouts: MutableList<WorkoutModel>
    private var onClickCallback: (WorkoutModel) -> Unit

    init {
        workouts.addAll(data)
        filteredWorkouts = workouts
        onClickCallback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.inflatable_workout_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return filteredWorkouts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = filteredWorkouts[position]

        if (workout.template) {
            holder.bindTemplate(workout, onClickCallback)
        } else {
            holder.bindWorkout(workout, onClickCallback)
        }
    }

    /** Filter the workouts by the provided text */
    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        filteredWorkouts = if (text.isEmpty()) {
            workouts
        } else {
            filteredWorkouts.filter { it.name.lowercase().contains(text) }.toMutableList()
        }

        notifyDataSetChanged()
    }

    /** Class to represent workout item view holder - each workout */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view)  {
        private var workoutContainer: ConstraintLayout = itemView.findViewById(R.id.workout_container)
        private var templateContainer: ConstraintLayout = itemView.findViewById(R.id.template_container)
        private var templateExercisesContainer: LinearLayout = itemView.findViewById(R.id.template_exercises_container)
        private var name: TextView = itemView.findViewById(R.id.workout_name_txt)
        private var startedTime: TextView = itemView.findViewById(R.id.workout_date_txt)
        private var finishedTime: TextView = itemView.findViewById(R.id.workout_finished_date_txt)
        private var statusValue: TextView = itemView.findViewById(R.id.current_workout_status_value_lbl)
        private var exercisesLlb: TextView = itemView.findViewById(R.id.exercises_lbl)
        private var exercises: TextView = itemView.findViewById(R.id.workout_exercises_summary_txt)
        private var total: TextView = itemView.findViewById(R.id.workout_total_txt)

        /** Bind the workout item to the view
         * @param item the workout
         * @param onClickCallback callback to execute on click
         */
        @SuppressLint("SetTextI18n")
        fun bindWorkout(item: WorkoutModel, onClickCallback: (WorkoutModel) -> Unit) {
            val statusStringId: Int
            val statusColorId: Int
            var exercisesText = ""
            var totalWeight = 0.0
            var totalReps = 0
            var completedWeight = 0.0
            var completedReps = 0

            templateContainer.visibility = View.GONE
            workoutContainer.visibility = View.VISIBLE

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

            if (item.finishDateTime == null) {
                statusStringId = R.string.in_progress_lbl
                statusColorId = R.color.orange
            } else {
                statusStringId = R.string.finished_lbl
                statusColorId = R.color.green
            }

            statusValue.text = Utils.getActivity().getString(statusStringId)
            statusValue.setTextColor(Utils.getActivity().getColor(statusColorId))

            startedTime.text = Utils.defaultFormatDateTime(item.startDateTime!!)

            if (item.finishDateTime != null) {
                finishedTime.visibility = View.VISIBLE
                finishedTime.text = Utils.defaultFormatDateTime(item.finishDateTime!!)
            } else {
                finishedTime.visibility = View.INVISIBLE
            }

            if (exercisesText.length > 2) {
                exercisesLlb.visibility = View.VISIBLE
                exercises.visibility = View.VISIBLE
                exercises.text = exercisesText.dropLast(2)
            } else {
                exercisesLlb.visibility = View.GONE
                exercises.visibility = View.GONE
            }

            total.text = String.format(Utils.getActivity().getText(R.string.workout_summary_lbl).toString(),
                        Utils.formatDouble(completedWeight),  Utils.formatDouble(totalWeight),
                        AppStateManager.user!!.defaultValues.weightUnit.text, completedReps, totalReps)

            // Add click listener to select the workout
            itemView.setOnClickListener {
                onClickCallback(item)
            }
        }

        /** Bind the template item to the view
         * @param item the template
         * @param onClickCallback callback to execute on click
         */
        @SuppressLint("SetTextI18n", "InflateParams")
        fun bindTemplate(item: WorkoutModel, onClickCallback: (WorkoutModel) -> Unit) {
            workoutContainer.visibility = View.GONE
            templateContainer.visibility = View.VISIBLE
            templateExercisesContainer.removeAllViews()

            name.text = item.name

            for (e: ExerciseModel in item.exercises) {
                // Inflate the view
                val inflatableView: View = LayoutInflater.from(Utils.getActivity())
                    .inflate(R.layout.inflatable_template_exercise_item, null)

                // Populate the text
                inflatableView.findViewById<TextView>(R.id.exercise_summary).text =
                    String.format(Utils.getActivity().getString(R.string.template_exercise_summary),
                        e.name, e.sets.count())

                templateExercisesContainer.addView(inflatableView)
            }

            // Add click listener to select the workout
            itemView.setOnClickListener {
                onClickCallback(item)
            }
        }
    }
}