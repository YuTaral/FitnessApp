package com.example.fitnessapp.panels

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.ManageTeamMembersDialog
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Add team temporary panel to implement the logic for add team */
class AddTeamPanel: BaseTeamPanel(null) {
    override var id = Constants.PanelUniqueId.ADD_TEAM.ordinal.toLong()
    override var titleId = R.string.add_team_panel_title
    override var layoutId = R.layout.add_edit_team_panel

    override fun populatePanel() {
        changeButtons()
    }

    /** Executed on save button click. Send request to add the team */
    override fun save() {
        if (name.text.isEmpty()) {
            saveBtn.isEnabled = true
            Utils.validationFailed(name, R.string.error_msg_team_name_mandatory)
            return
        }

        val team = TeamModel(0, Utils.encodeImageToString(teamImage), name.text.toString(),
            description.text.toString(), privateNote.text.toString())

        TeamRepository().addTeam(team, onSuccess = {
            Utils.getPanelAdapter().refreshTeamsPanel()
        }, onError = {
            saveBtn.isEnabled = true
        })
    }

    override fun showInviteMembers() {
        // Create empty team model when invite members is being displayed from Add Team Dialog
        team = TeamModel(0, "", name.text.toString(), "", "")

        ManageTeamMembersDialog(requireContext(), team!!).show()
    }

    /** Hide the delete button and center the save button when adding new team */
    private fun changeButtons() {
        deleteBtn.visibility = View.GONE

        val layoutParams = saveBtn.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        saveBtn.layoutParams = layoutParams
    }
}