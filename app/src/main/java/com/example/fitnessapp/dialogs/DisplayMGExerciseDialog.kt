package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.utils.Utils

/** Dialog to display muscle group exercise information */
class DisplayMGExerciseDialog(ctx: Context, exName: String, data: MGExerciseModel): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_display_mg_exercise
    override var dialogTitleId = 0

    private var exerciseName = exName
    private var model = data

    private lateinit var description: TextView

    override fun findViews() {
        super.findViews()

        description = dialog.findViewById(R.id.mg_exercise_description)
    }

    override fun populateDialog() {
        title.text = exerciseName

        if (model.description.isEmpty()) {
            description.text = Utils.getActivity().getString(R.string.no_description_for_ex_lbl)
        } else {
            description.text = model.description
        }
    }
}