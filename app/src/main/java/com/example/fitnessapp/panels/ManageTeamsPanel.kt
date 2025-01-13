package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamsRecAdapter
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import com.example.fitnessapp.views.CustomSwitchView

/** Manage Teams Panel class to implement the logic for managing teams */
class ManageTeamsPanel: BasePanel(), ITemporaryPanel {
    override var id = Constants.PanelUniqueId.MANAGE_TEAMS.ordinal.toLong()
    override var panelIndex = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId = R.string.teams_lbl
    override var iconId = R.drawable.icon_tab_manage_teams
    override var layoutId = R.layout.panel_manage_teams
    override val removePreviousTemporary = true

    private lateinit var switch: CustomSwitchView
    private lateinit var noTeamsLbl: TextView
    private lateinit var teamsRecycler: RecyclerView
    private lateinit var addTeamBtn: Button
    private lateinit var editTeamBtn: Button

    private lateinit var _teamType: Constants.ViewTeamAs
    private var teamType: Constants.ViewTeamAs
            get() = _teamType
            set(value) {
                _teamType = value
                updatePanel()
            }

    private var refreshTeams = false
    private var autoSelectTeamId = 0L

    override fun findViews() {
        switch = panel.findViewById(R.id.coach_member_selector)
        noTeamsLbl = panel.findViewById(R.id.no_teams_lbl)
        teamsRecycler = panel.findViewById(R.id.teams_recycler)
        addTeamBtn = panel.findViewById(R.id.add_team_btn)
        editTeamBtn = panel.findViewById(R.id.edit_team_btn)
    }

    override fun populatePanel() {
        // Set the initially selected team type (usually COACH)
        teamType = if (switch.getSelected() == CustomSwitchView.Selected.LEFT) {
            Constants.ViewTeamAs.COACH
        } else {
            Constants.ViewTeamAs.MEMBER
        }

        TeamRepository().getMyTeams(teamType.toString(), onSuccess = { teams ->
            populateTeams(teams)
        })
    }

    override fun addClickListeners() {
        switch.setLeftClickListener {
            teamType = Constants.ViewTeamAs.COACH

            TeamRepository().getMyTeams(teamType.toString(), onSuccess = { teams ->
                populateTeams(teams)
            })
        }

        switch.setRightClickListener {
            teamType = Constants.ViewTeamAs.MEMBER

            TeamRepository().getMyTeams(teamType.toString(), onSuccess = { teams ->
                populateTeams(teams)
            })
        }

        addTeamBtn.setOnClickListener {
            Utils.getPanelAdapter().displayTemporaryPanel(AddTeamPanel())
        }
        editTeamBtn.setOnClickListener {
            val selectedTeam = getAdapter()!!.getSelectedTeam()

            if (selectedTeam != null) {
                if (selectedTeam.viewTeamAs == Constants.ViewTeamAs.COACH.toString()) {
                    Utils.getPanelAdapter().displayTemporaryPanel(EditTeamPanel(selectedTeam))
                } else {
                    Utils.getPanelAdapter().displayTemporaryPanel(TeamDetailsPanel(selectedTeam))
                }

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
        editTeamBtn.isEnabled = getAdapter()!!.getSelectedTeam() != null
        removeFourthPanel()
    }

    /** Check whether there is fourth active panel and remove it if there is. Executed when
     * team selection changes
     */
    private fun removeFourthPanel() {
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

        if (teams.isEmpty()) {
            teamsRecycler.visibility = View.GONE

            if (teamType == Constants.ViewTeamAs.COACH) {
                noTeamsLbl.text = requireContext().getString(R.string.no_my_teams_lbl)
            } else {
                noTeamsLbl.text = requireContext().getString(R.string.no_joined_teams_lbl)
            }

            noTeamsLbl.visibility = View.VISIBLE

        } else {
            noTeamsLbl.visibility = View.GONE

            teamsRecycler.visibility = View.VISIBLE

            if (teamsRecycler.adapter == null) {
                teamsRecycler.adapter = TeamsRecAdapter(teams, callback = { enableDisableEdit() })
            } else {
                (teamsRecycler.adapter as TeamsRecAdapter).updateTeams(teams)
            }

            // Check if we need to auto select team
            if (autoSelectTeamId > 0) {
                (teamsRecycler.adapter as TeamsRecAdapter).changeSelectedTeam(autoSelectTeamId)
                editTeamBtn.isEnabled = true
                editTeamBtn.callOnClick()
                autoSelectTeamId = 0
            }
        }

        refreshTeams = false
    }

    /** Return the teams adapter or null if not initialized */
    private fun getAdapter(): TeamsRecAdapter? {
        val adapter = teamsRecycler.adapter ?: return null

        return adapter as TeamsRecAdapter
    }

    /** Update the views in the panel based on the selected team type */
    private fun updatePanel() {
        // Clear the selected team
        val adapter = getAdapter() ?: return
        adapter.changeSelectedTeam(0)

        // Remove fourth panel (add / edit / details team) if there is
        removeFourthPanel()

        if (teamType == Constants.ViewTeamAs.COACH) {
            editTeamBtn.text = requireContext().getString(R.string.edit_btn_lbl)
        } else {
            editTeamBtn.text = requireContext().getString(R.string.details_btn)
        }
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