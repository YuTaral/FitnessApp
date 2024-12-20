package com.example.fitnessapp.panels

import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamsRecyclerAdapter
import com.example.fitnessapp.dialogs.AddTeamDialog
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants

/** Manage Teams Panel class to implement the logic for managing teams */
class ManageTeamsPanel: BasePanel() {
    override var id = Constants.PanelUniqueId.MANAGE_TEAMS.ordinal.toLong()
    override var panelIndex = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId = R.string.teams_lbl
    override var layoutId = R.layout.manage_teams_panel

    private lateinit var teamsRecycler: RecyclerView
    private lateinit var addTeamBtn: Button

    override fun findViews() {
        teamsRecycler = panel.findViewById(R.id.teams_recycler)
        addTeamBtn = panel.findViewById(R.id.add_team_btn)
    }

    override fun populatePanel() {
        TeamRepository().getMyTeams(onSuccess = { teams ->
            populateTeams(teams)
        })
    }

    override fun addClickListeners() {
        addTeamBtn.setOnClickListener {
            AddTeamDialog(requireContext()).show()
        }
    }

    /** Populate the teams by setting the adapter data
     * @param teams the teams
     */
    fun populateTeams(teams: List<TeamModel>) {
        if (teamsRecycler.adapter == null) {
            teamsRecycler.adapter = TeamsRecyclerAdapter(teams)
        } else {
            (teamsRecycler.adapter as TeamsRecyclerAdapter).updateTeams(teams)
        }
    }
}