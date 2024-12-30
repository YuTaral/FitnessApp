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

    private lateinit var originalName: TextView
    private lateinit var description: TextView

    override fun findViews() {
        super.findViews()

        originalName = dialog.findViewById(R.id.exercise_original_name)
        description = dialog.findViewById(R.id.mg_exercise_description)
    }

    override fun populateDialog() {
        title.text = exerciseName

        originalName.text = String.format(Utils.getActivity().getString(R.string.original_name_lbl),
                                            model.name)

        if (model.description.isEmpty()) {
            description.text = Utils.getActivity().getString(R.string.no_description_for_ex_lbl)
        } else {
            description.text = model.description
        }
    }
}