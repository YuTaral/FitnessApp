package com.example.fitnessapp.panels

import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.IImagePicker
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.managers.ImageUploadManager
import com.example.fitnessapp.utils.Constants
import com.google.android.material.textfield.TextInputLayout

/** Base Team Panel class to implement the logic add / edit single team */
abstract class BaseTeamPanel: BasePanel(), IImagePicker, ITemporaryPanel {
    override var panelIndex = Constants.PanelIndices.SECOND_TEMPORARY.ordinal
    override val removePreviousTemporary = false

    private lateinit var removePictureBtn: Button

    protected lateinit var teamImage: ImageView
    protected lateinit var name: EditText
    protected lateinit var description: EditText
    protected lateinit var saveBtn: Button
    protected lateinit var deleteBtn: Button
    protected lateinit var membersSectionContainer: ConstraintLayout
    protected lateinit var inviteMembersBtn: Button
    protected lateinit var membersRecycler: RecyclerView

    override fun findViews() {
        teamImage = panel.findViewById(R.id.team_image)
        removePictureBtn = panel.findViewById(R.id.remove_pic_btn)
        name = panel.findViewById<TextInputLayout>(R.id.team_name).editText!!
        description = panel.findViewById<TextInputLayout>(R.id.team_description).editText!!
        deleteBtn = panel.findViewById(R.id.delete_btn)
        saveBtn = panel.findViewById(R.id.save_btn)
        membersSectionContainer = panel.findViewById(R.id.members_section_container)
        inviteMembersBtn = panel.findViewById(R.id.invite_btn)
        membersRecycler = panel.findViewById(R.id.members_recycler)
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