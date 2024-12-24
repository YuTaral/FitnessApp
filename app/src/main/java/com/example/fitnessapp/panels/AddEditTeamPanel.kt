package com.example.fitnessapp.panels

import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.IImagePicker
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.ImageUploadManager
import com.example.fitnessapp.utils.Utils

/** Add / Edit Team Panel class to implement the logic add / edit single team */
class AddEditTeamPanel: BasePanel(), IImagePicker, ITemporaryPanel {
    override var id = Constants.PanelUniqueId.ADD_EDIT_TEAM.ordinal.toLong()
    override var panelIndex = Constants.PanelIndices.ANOTHER_TEMPORARY.ordinal
    override var titleId = R.string.add_team_panel_title
    override var layoutId = R.layout.add_edit_team_panel
    override val removePreviousTemporary = false

    private lateinit var teamImage: ImageView
    private lateinit var removePictureBtn: Button
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var privateNote: EditText
    private lateinit var saveBtn: Button
    private lateinit var inviteMembersBtn: Button

    override fun findViews() {
        teamImage = panel.findViewById(R.id.team_image)
        removePictureBtn = panel.findViewById(R.id.remove_pic_btn)
        name = panel.findViewById(R.id.team_name)
        description = panel.findViewById(R.id.team_description)
        privateNote = panel.findViewById(R.id.team_private_note)
        saveBtn = panel.findViewById(R.id.save_btn)
        inviteMembersBtn = panel.findViewById(R.id.invite_btn)
    }

    override fun populatePanel() {}

    override fun addClickListeners() {
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
            saveBtn.isEnabled = true
            Utils.validationFailed(name, R.string.error_msg_team_name_mandatory)
            return
        }

        val team = TeamModel(0, Utils.encodeImageToString(teamImage), name.text.toString(),
            description.text.toString(), privateNote.text.toString())

        TeamRepository().addTeam(team, onSuccess = {
            Utils.getPanelAdapter().getTeamsPanel()!!.setRefreshTeams(true)
            Utils.getPanelAdapter().displayTemporaryPanel(ManageTeamsPanel())
        }, onError = {
            saveBtn.isEnabled = true
        })
    }
}