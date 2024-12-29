package com.example.fitnessapp.dialogs

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamMembersRecyclerAdapter
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.Utils

/** Manage members dialog to handle invite / remove members to team */
class ManageTeamMembersDialog(ctx: Context, team: TeamModel, teamMembers: List<TeamMemberModel>): BaseDialog(ctx) {
    override var layoutId = R.layout.manage_team_members_dialog
    override var dialogTitleId = R.string.invite_members_dialog_title

    private var selectedTeam = team
    private var members = teamMembers

    private lateinit var teamNameLbl: TextView
    private lateinit var search: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var searchResultLbl: TextView
    private lateinit var searchResultRecycler: RecyclerView
    private lateinit var membersRecycler: RecyclerView

    override fun findViews() {
        super.findViews()

        teamNameLbl = dialog.findViewById(R.id.team_name_lbl)
        search = dialog.findViewById(R.id.search)
        searchIcon = dialog.findViewById(R.id.search_icon)
        searchResultLbl = dialog.findViewById(R.id.results_lbl)
        searchResultRecycler = dialog.findViewById(R.id.members_search_recycler)
        membersRecycler = dialog.findViewById(R.id.invited_members_recycler)
    }

    override fun populateDialog() {
        teamNameLbl.text = selectedTeam.name

        membersRecycler.adapter = TeamMembersRecyclerAdapter(members, TeamMembersRecyclerAdapter.AdapterType.REMOVE, callback = {
                member -> onRemove(member)
        })
    }

    override fun addClickListeners() {
        super.addClickListeners()

        searchIcon.setOnClickListener {
            performSearch()
        }

        search.setOnEditorActionListener { v, actionId, event ->
            // Check if the action is the "Done" action
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                        && event.action == KeyEvent.ACTION_DOWN)) {

                Utils.closeKeyboard(v)
                performSearch()
                true

            } else {
                false
            }
        }
    }

    override fun dismiss() {
        super.dismiss()

        // Re-populate the panel, if any changes to members were done they must be visible
        Utils.getPanelAdapter().getTeamPanel()!!.populatePanel()
    }

    /** Search for users with the given search term and display them in the section */
    private fun performSearch() {
        if (search.text.isEmpty()) {
            Utils.validationFailed(search, R.string.enter_user_email_or_name_lbl)
            return
        }

        TeamRepository().getUsersToInvite(search.text.toString(), selectedTeam.id, onSuccess = { members ->
            if (members.isEmpty()) {
                searchResultLbl.text = String.format(Utils.getActivity().getString(R.string.no_users_found), search.text)
                searchResultRecycler.visibility = View.GONE
            } else {
                searchResultLbl.text = Utils.getActivity().getString(R.string.search_results_lbl)
                searchResultRecycler.visibility = View.VISIBLE

                if (searchResultRecycler.adapter == null) {
                    searchResultRecycler.adapter = TeamMembersRecyclerAdapter(members, TeamMembersRecyclerAdapter.AdapterType.INVITE, callback = {
                        member -> onInvite(member)
                    })
                } else {
                    (searchResultRecycler.adapter as TeamMembersRecyclerAdapter).update(members)
                }
            }
        })
    }

    /** Executed on invite button click to move the invited member into the team members section
     * and send invite request
     * @param member the member to invite
     */
    private fun onInvite(member: TeamMemberModel) {
        // Remove the member from th search result
        getSearchAdapter().addRemoveMember(member, false)

        // Send invite request, on success updated list will be returned
        TeamRepository().inviteMember(member.userId, selectedTeam.id, onSuccess = { members ->
            getMembersAdapter().update(members)
        })
    }

    /** Executed on remove button click to move the mark the member as removed
     * @param member the member to remove
     */
    private fun onRemove(member: TeamMemberModel) {
        // Send remove request, on success updated list will be returned
        TeamRepository().removeMember(member.id, onSuccess = { members ->
            getMembersAdapter().update(members)
        })
    }

    /** Return the adapter of the search recycler */
    private fun getSearchAdapter(): TeamMembersRecyclerAdapter {
        return  searchResultRecycler.adapter as TeamMembersRecyclerAdapter
    }

    /** Return the adapter of the members recycler */
    private fun getMembersAdapter(): TeamMembersRecyclerAdapter {
        return  membersRecycler.adapter as TeamMembersRecyclerAdapter
    }
}