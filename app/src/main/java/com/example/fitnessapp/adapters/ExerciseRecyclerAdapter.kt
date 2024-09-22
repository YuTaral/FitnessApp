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
import com.example.fitnessapp.dialogs.EditExerciseDialog
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the data (exercises) shown for each workout */
class ExerciseRecyclerAdapter(data: List<ExerciseModel>, onSuccessChange: () -> Unit) : RecyclerView.Adapter<ExerciseRecyclerAdapter.ExerciseItem>() {
    private var exercises: MutableList<ExerciseModel> = mutableListOf()
    private var onSuccessChangeCallback: () -> Unit

    init {
        exercises.addAll(data)
        onSuccessChangeCallback = onSuccessChange
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseItem {
        return ExerciseItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_exercise_item, parent, false))
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ExerciseItem, position: Int) {
        holder.bind(exercises[position], onSuccessChangeCallback)
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
    class ExerciseItem(view: View) : RecyclerView.ViewHolder(view) {
        private var exerciseName: TextView = itemView.findViewById(R.id.exercise_name_txt)
        private var editBtn: ImageView = itemView.findViewById(R.id.exercise_edit_btn)
        private var expandSymbol: ImageView = itemView.findViewById(R.id.exercise_expand_collapse_symbol)
        private var setsContainer: ConstraintLayout = itemView.findViewById(R.id.sets_container)
        private var setsLinLayout: LinearLayout = itemView.findViewById(R.id.sets)

        /** Set the data for each exercise and adds click listener to expand / collapse the exercise
         * @param item the exercise
         * @param onSuccessChangeCallback the callback to execute when exercise has been modified successfully
         */
        fun bind(item: ExerciseModel, onSuccessChangeCallback: () -> Unit) {
            // Set the exercise data
            exerciseName.text = item.name

            // Add the sets
            bindSets(item)

            // Add Edit click listener
            editBtn.setOnClickListener {
                EditExerciseDialog.showDialog(item, onSuccessChange = { onSuccessChangeCallback() })
            }

            // Add expand mechanism
            expandSymbol.setOnClickListener {
                if (setsContainer.visibility == View.VISIBLE) {
                    setsContainer.visibility = View.GONE
                    expandSymbol.setBackgroundResource(R.drawable.expand_icon)
                } else {
                    setsContainer.visibility = View.VISIBLE
                    expandSymbol.setBackgroundResource(R.drawable.collapse_icon)
                }
            }
        }

        /** Set the data for each set
         * @param item the exercise
         */
        @SuppressLint("InflateParams")
        private fun bindSets(item: ExerciseModel) {
            val inflater = LayoutInflater.from(Utils.getActivity())
            var setCounter = 1

            // Clear the views from the previous bind
            setsLinLayout.removeAllViews()

            for (set: SetModel in item.sets) {
                val inflatableView: View = inflater.inflate(R.layout.inflatable_set_item, null)

                inflatableView.findViewById<TextView>(R.id.set_number_txt).text = setCounter.toString()

                if (set.reps > 0) {
                    inflatableView.findViewById<TextView>(R.id.reps_number_txt).text = set.reps.toString()
                }

                if (set.weight > 0) {
                    inflatableView.findViewById<TextView>(R.id.weight_lbl).text = String.format("%.3f", set.weight)
                }

                val completedCheckBox = inflatableView.findViewById<CheckBox>(R.id.completed)
                completedCheckBox.isChecked = set.completed
                completedCheckBox.isClickable = false

                setsLinLayout.addView(inflatableView)
                setCounter ++
            }
        }
    }
}