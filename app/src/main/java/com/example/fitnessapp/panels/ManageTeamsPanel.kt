package com.example.fitnessapp.panels

import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamsRecAdapter
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Manage Teams Panel class to implement the logic for managing teams */
class ManageTeamsPanel: BasePanel(), ITemporaryPanel {
    override var id = Constants.PanelUniqueId.MANAGE_TEAMS.ordinal.toLong()
    override var panelIndex = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId = R.string.teams_lbl
    override var layoutId = R.layout.panel_manage_teams
    override val removePreviousTemporary = true

    private lateinit var teamsRecycler: RecyclerView
    private lateinit var addTeamBtn: Button
    private lateinit var editTeamBtn: Button

    private var refreshTeams = false
    private var autoSelectTeamId = 0L

    override fun findViews() {
        teamsRecycler = panel.findViewById(R.id.teams_recycler)
        addTeamBtn = panel.findViewById(R.id.add_team_btn)
        editTeamBtn = panel.findViewById(R.id.edit_team_btn)
    }

    override fun populatePanel() {
        TeamRepository().getMyTeams(onSuccess = { teams ->
            populateTeams(teams)
        })
    }

    override fun addClickListeners() {
        addTeamBtn.setOnClickListener {
            Utils.getPanelAdapter().displayTemporaryPanel(AddTeamPanel())
        }
        editTeamBtn.setOnClickListener {
            val selectedTeam = getSelectedTeam()

            if (selectedTeam != null) {
                Utils.getPanelAdapter().displayTemporaryPanel(EditTeamPanel(selectedTeam))
            } else {
                // Something went wrong, disable the edit button
                enableDisableEdit()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (refreshTeams) {
            populatePanel()
        }
    }

    /** Enable disable edit button when teams selection changes
     */
    private fun enableDisableEdit() {
        editTeamBtn.isEnabled = getSelectedTeam() != null

        if (Utils.getPanelAdapter().itemCount == Constants.PanelIndices.entries.size) {
            // If selection changes and we have active second temporary panel (add / edit team), remove it
            Utils.getPanelAdapter().removeTemporaryPanels(Constants.PanelIndices.ANOTHER_TEMPORARY.ordinal)
        }
    }

    /** Populate the teams by setting the adapter data
     * @param teams the teams
     */
    private fun populateTeams(teams: List<TeamModel>) {
        editTeamBtn.isEnabled = false

        if (teamsRecycler.adapter == null) {
            teamsRecycler.adapter = TeamsRecAdapter(teams, callback = { enableDisableEdit() })
        } else {
            (teamsRecycler.adapter as TeamsRecAdapter).updateTeams(teams)
        }

        // Check if we need to auto select team
        if (autoSelectTeamId > 0) {
            (teamsRecycler.adapter as TeamsRecAdapter).setSelectedTeam(autoSelectTeamId)
            editTeamBtn.isEnabled = true
            editTeamBtn.callOnClick()
            autoSelectTeamId = 0
        }

        refreshTeams = false
    }

    /** Return the currently selected team */
    private fun getSelectedTeam(): TeamModel? {
        return (teamsRecycler.adapter as TeamsRecAdapter).getSelectedTeam()
    }

    /** Setter for refresh teams property */
    fun setRefreshTeams(value: Boolean) {
        refreshTeams = value
    }

    /** Automatically set the variable to auto select team after populating teams
     * @param teamId the team id
     */
    fun setAutoSelectTeam(teamId: Long) {
        autoSelectTeamId = teamId
    }
}