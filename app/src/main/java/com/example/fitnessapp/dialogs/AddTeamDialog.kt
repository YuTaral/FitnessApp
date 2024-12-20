package com.example.fitnessapp.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.IImagePickerDialog
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.ImageUploadManager
import com.example.fitnessapp.utils.Utils

/** Add team dialog used to create teams */
class AddTeamDialog(ctx: Context): BaseDialog(ctx), IImagePickerDialog {
    override var layoutId = R.layout.add_team_dialog
    override var dialogTitleId = R.string.add_team_dialog_title

    private lateinit var teamImage: ImageView
    private lateinit var removePictureBtn: Button
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var privateNote: EditText
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        teamImage = dialog.findViewById(R.id.team_image)
        removePictureBtn = dialog.findViewById(R.id.remove_pic_btn)
        name = dialog.findViewById(R.id.team_name)
        description = dialog.findViewById(R.id.team_description)
        privateNote = dialog.findViewById(R.id.team_private_note)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }
    override fun populateDialog() {}

    override fun addClickListeners() {
        super.addClickListeners()

        teamImage.setOnClickListener {
            ImageUploadManager.showImagePicker()
        }
        removePictureBtn.setOnClickListener {
            removePicture()
        }
        saveBtn.setOnClickListener {
            saveBtn.isEnabled = false
            save()
        }
    }

    override fun onImageUploadSuccess(bitmap: Bitmap) {
        teamImage.setImageBitmap(bitmap)
    }

    /** Remove picture button handler to clear the team picture and return to the default one */
    private fun removePicture() {
        teamImage.setImageBitmap(null)
        teamImage.setBackgroundResource(R.drawable.icon_team_default_picture)
    }

    /** Executed on save button click. Send request to add the team */
    private fun save() {
        if (name.text.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_team_name_mandatory)
            return
        }

        val team = TeamModel(0, Utils.encodeImageToString(teamImage), name.text.toString(),
                                    description.text.toString(), privateNote.text.toString())

        TeamRepository().addTeam(team, onSuccess = { teams ->
            dismiss()
            Utils.getPanelAdapter().getTeamsPanel()!!.populateTeams(teams)
        }, onError = {
            saveBtn.isEnabled = true
        })
    }
}