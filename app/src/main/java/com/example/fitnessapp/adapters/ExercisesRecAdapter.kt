package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.EditExerciseFromWorkoutDialog
import com.example.fitnessapp.dialogs.TimerDialog
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the data (exercises) shown for each workout */
class ExercisesRecAdapter(data: List<ExerciseModel>) : RecyclerView.Adapter<ExercisesRecAdapter.ViewHolder>() {
    private var exercises: MutableList<ExerciseModel> = mutableListOf()

    init {
        exercises.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_exercise_item, parent, false))
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    /** Updates the data
     * @param data the new exercises
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<ExerciseModel>) {
        exercises.clear()
        exercises.addAll(data)
        notifyDataSetChanged()
    }

    /** Class to represent exercise item view holder - each exercise */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var exerciseName: TextView = itemView.findViewById(R.id.exercise_name_txt)
        private var weightLbl: TextView = itemView.findViewById(R.id.weight_lbl)
        private var editBtn: ImageView = itemView.findViewById(R.id.exercise_edit_btn)
        private var expandSymbol: ImageView = itemView.findViewById(R.id.exercise_expand_collapse_symbol)
        private var targetMuscleGroup: TextView = itemView.findViewById(R.id.target_muscle_group)
        private var setsContainer: ConstraintLayout = itemView.findViewById(R.id.sets_container)
        private var setsLinLayout: LinearLayout = itemView.findViewById(R.id.sets)

        /** Set the data for each exercise and adds click listener to expand / collapse the exercise
         * @param item the exercise
         */
        fun bind(item: ExerciseModel) {
            // Set the exercise data
            exerciseName.text = item.name

            weightLbl.text = String.format(Utils.getActivity().getString(R.string.weight_in_unit_lbl),
                AppStateManager.user!!.defaultValues.weightUnit.text)

            targetMuscleGroup.text = String.format(Utils.getActivity().getString(R.string.target_lbl), item.muscleGroup.name)

            // Add the sets
            bindSets(item.sets)

            // Add Edit click listener
            editBtn.setOnClickListener {
                Utils.addEditExerciseClick(AskQuestionDialog.Question.WORKOUT_ALREADY_FINISHED_WHEN_EDIT_EXERCISE, callback = {
                    // Display the edit exercise dialog
                    EditExerciseFromWorkoutDialog(Utils.getActivity(), item).show()
                })
            }

            // Add expand mechanism
            expandSymbol.setOnClickListener {
                if (setsContainer.visibility == View.VISIBLE) {
                    Utils.collapseView(setsContainer)
                    expandSymbol.animate().rotation(180f).setDuration(300).start()
                } else {
                    Utils.expandView(setsContainer)
                    expandSymbol.animate().rotation(360f).setDuration(300).start()
                }
            }
        }

        /** Set the data for each set
         * @param sets the sets
         */
        @SuppressLint("InflateParams")
        private fun bindSets(sets: MutableList<SetModel>) {
            val inflater = LayoutInflater.from(Utils.getActivity())
            var setCounter = 1

            // Clear the views from the previous bind
            setsLinLayout.removeAllViews()

            for (set: SetModel in sets) {
                val inflatableView: View = inflater.inflate(R.layout.inflatable_set_item, null)
                val completedCheckBox = inflatableView.findViewById<CheckBox>(R.id.completed)
                val rest = inflatableView.findViewById<TextView>(R.id.rest)

                inflatableView.findViewById<TextView>(R.id.set_number_txt).text = setCounter.toString()

                if (set.reps > 0) {
                    inflatableView.findViewById<TextView>(R.id.reps_number_txt).text = set.reps.toString()
                }

                if (set.weight > 0) {
                    inflatableView.findViewById<TextView>(R.id.weight_lbl).text = Utils.formatDouble(set.weight)
                }

                completedCheckBox.isClickable = false

                if (set.completed) {
                    rest.setOnClickListener(null)
                    rest.visibility = View.GONE

                } else {
                    completedCheckBox.visibility = View.GONE
                    rest.visibility = View.VISIBLE

                    rest.text = set.rest.toString()

                    rest.setOnClickListener { TimerDialog(Utils.getActivity(), set.rest, onFinish = {
                        ExerciseRepository().completeSet(set.id, AppStateManager.workout!!.id, onSuccess =  { workout ->
                            // Mark the set as completed on finish and refresh the workout
                            Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
                        })
                    }).show() }
                }

                setsLinLayout.addView(inflatableView)
                setCounter ++
            }
        }
    }
}