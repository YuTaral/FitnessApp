package com.example.fitnessapp.panels

import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamMembersRecAdapter
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.TeamCoachModel
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import com.google.android.material.imageview.ShapeableImageView


/** Team details temporary panel to implement the logic to allow the user to view teams in which
 * participates as member
 */
class TeamDetailsPanel(t: TeamModel): BasePanel(), ITemporaryPanel {
    override var id = Constants.PanelUniqueId.TEAM_DETAILS.ordinal.toLong()
    override var panelIndex = Constants.PanelIndices.ANOTHER_TEMPORARY.ordinal
    override var titleId = R.string.team_details_lbl
    override var iconId = R.drawable.icon_tab_team_details
    override var layoutId = R.layout.panel_team_details
    override val removePreviousTemporary = false

    private var team = t

    private lateinit var teamImage: ShapeableImageView
    private lateinit var teamName: TextView
    private lateinit var coachImage: ShapeableImageView
    private lateinit var coachName: TextView
    private lateinit var teamDescription: TextView
    private lateinit var teamMembersLbl: TextView
    private lateinit var membersRecycler: RecyclerView
    private lateinit var leaveBtn: Button

    override fun findViews() {
        teamImage = panel.findViewById(R.id.team_image)
        teamName = panel.findViewById(R.id.team_name)
        coachImage = panel.findViewById(R.id.coach_image)
        coachName = panel.findViewById(R.id.coach_name)
        teamDescription = panel.findViewById(R.id.team_description)
        teamMembersLbl = panel.findViewById(R.id.other_members_lbl)
        membersRecycler = panel.findViewById(R.id.members_recycler)
        leaveBtn = panel.findViewById(R.id.leave_btn)
    }

    override fun populatePanel() {
        TeamRepository().getJoinedTeamMembers(team.id, onSuccess = { data ->
            val coach = TeamCoachModel(data[0])
            val teamMembers: MutableList<TeamMemberModel> = mutableListOf()

            for (i in 1..<data.size) {
                teamMembers.add(TeamMemberModel(data[i]))
            }

            membersRecycler.adapter = TeamMembersRecAdapter(teamMembers, TeamMembersRecAdapter.AdapterType.DISPLAY, callback = {})

            if (team.image.isNotEmpty()) {
                teamImage.setImageBitmap(Utils.convertStringToBitmap(team.image))
            }

            if (coach.image.isNotEmpty()) {
                coachImage.setImageBitmap(Utils.convertStringToBitmap(coach.image))
            }

            teamName.text = team.name
            coachName.text = String.format(requireContext().getString(R.string.coach_s_lbl), coach.fullName)
            teamDescription.text = team.description

            teamMembersLbl.text = String.format(requireContext().getString(R.string.other_members_lbl), teamMembers.size)
        })
    }

    override fun addClickListeners() {
    }
}