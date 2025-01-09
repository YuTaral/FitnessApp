package com.example.fitnessapp.panels

import android.view.View
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamMembersRecAdapter
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.ManageTeamMembersDialog
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Edit team temporary panel to implement the logic for edit team */
class EditTeamPanel(t: TeamModel): BaseTeamPanel() {
    override var id = Constants.PanelUniqueId.EDIT_TEAM.ordinal.toLong()
    override var titleId = R.string.edit_team_panel_title
    override var layoutId = R.layout.panel_add_edit_team

    private var team = t

    private lateinit var members: List<TeamMemberModel>

    override fun populatePanel() {
        teamImage.setImageBitmap(Utils.convertStringToBitmap(team.image))
        name.setText(team.name)
        description.setText(team.description)
        privateNote.setText(team.privateNote)

        membersRecycler.visibility = View.VISIBLE

        TeamRepository().getTeamMembers(team.id, onSuccess = { teamMembers ->
            members = teamMembers
            membersRecycler.adapter = TeamMembersRecAdapter(members, TeamMembersRecAdapter.AdapterType.DISPLAY, callback = {})
        })
    }

    override fun addClickListeners() {
        super.addClickListeners()

        deleteBtn.setOnClickListener {
            delete()
        }

        inviteMembersBtn.setOnClickListener {
            ManageTeamMembersDialog(requireContext(), team, members).show()
        }
    }

    /** Executed on save button click. Send request to edit the team */
    override fun save() {
        if (name.text.isEmpty()) {
            saveBtn.isEnabled = true
            Utils.validationFailed(name, R.string.error_msg_team_name_mandatory)
            return
        }

        val updateTeam = TeamModel(team.id, Utils.encodeImageToString(teamImage), name.text.toString(),
            description.text.toString(), privateNote.text.toString())

        TeamRepository().updateTeam(updateTeam, onSuccess = {
            Utils.getPanelAdapter().refreshTeamsPanel()
        }, onError = {
            saveBtn.isEnabled = true
        })
    }

    /** Delete the selected team */
    private fun delete() {
        val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.DELETE_TEAM, team)

        dialog.setConfirmButtonCallback {
            TeamRepository().deleteTeam(team.id, onSuccess = {
                dialog.dismiss()
                Utils.getPanelAdapter().refreshTeamsPanel()
            })
        }

        dialog.show()
    }
}