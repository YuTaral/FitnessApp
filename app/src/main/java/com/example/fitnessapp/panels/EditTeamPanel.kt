package com.example.fitnessapp.panels

import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.ManageTeamMembersDialog
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Edit team temporary panel to implement the logic for edit team */
class EditTeamPanel(t: TeamModel): BaseTeamPanel(t) {
    override var id = Constants.PanelUniqueId.EDIT_TEAM.ordinal.toLong()
    override var titleId = R.string.edit_team_panel_title
    override var layoutId = R.layout.add_edit_team_panel

    override fun populatePanel() {
        teamImage.setImageBitmap(Utils.convertStringToBitmap(team!!.image))
        name.setText(team!!.name)
        description.setText(team!!.description)
        privateNote.setText(team!!.privateNote)
    }

    override fun addClickListeners() {
        super.addClickListeners()

        deleteBtn.setOnClickListener {
            delete()
        }
    }

    /** Executed on save button click. Send request to edit the team */
    override fun save() {
        if (name.text.isEmpty()) {
            saveBtn.isEnabled = true
            Utils.validationFailed(name, R.string.error_msg_team_name_mandatory)
            return
        }

        val updateTeam = TeamModel(team!!.id, Utils.encodeImageToString(teamImage), name.text.toString(),
            description.text.toString(), privateNote.text.toString())

        TeamRepository().updateTeam(updateTeam, onSuccess = {
            Utils.getPanelAdapter().refreshTeamsPanel()
        }, onError = {
            saveBtn.isEnabled = true
        })
    }

    override fun showInviteMembers() {
        ManageTeamMembersDialog(requireContext(), team!!).show()
    }

    /** Delete the selected team */
    private fun delete() {
        val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.DELETE_TEAM, team)

        dialog.setLeftButtonCallback {
            TeamRepository().deleteTeam(team!!.id, onSuccess = {
                dialog.dismiss()
                Utils.getPanelAdapter().refreshTeamsPanel()
            })
        }

        dialog.show()
    }
}