package com.example.fitnessapp.panels

import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.IImagePicker
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.ImageUploadManager

/** Base Team Panel class to implement the logic add / edit single team */
abstract class BaseTeamPanel: BasePanel(), IImagePicker, ITemporaryPanel {
    override var panelIndex = Constants.PanelIndices.ANOTHER_TEMPORARY.ordinal
    override val removePreviousTemporary = false

    private lateinit var removePictureBtn: Button

    protected lateinit var teamImage: ImageView
    protected lateinit var name: EditText
    protected lateinit var description: EditText
    protected lateinit var privateNote: EditText
    protected lateinit var saveBtn: Button
    protected lateinit var deleteBtn: Button
    protected lateinit var inviteMembersBtn: Button

    override fun findViews() {
        teamImage = panel.findViewById(R.id.team_image)
        removePictureBtn = panel.findViewById(R.id.remove_pic_btn)
        name = panel.findViewById(R.id.team_name)
        description = panel.findViewById(R.id.team_description)
        privateNote = panel.findViewById(R.id.team_private_note)
        deleteBtn = panel.findViewById(R.id.delete_btn)
        saveBtn = panel.findViewById(R.id.save_btn)
        inviteMembersBtn = panel.findViewById(R.id.invite_btn)
    }

    override fun addClickListeners() {
        teamImage.setOnClickListener {
            ImageUploadManager.showImagePicker()
        }
        saveBtn.setOnClickListener {
            saveBtn.isEnabled = false
            save()
        }
        removePictureBtn.setOnClickListener {
            removePicture()
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

    /** The callback to execute on save button click */
    abstract fun save()
}