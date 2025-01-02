package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** TeamRepository class, used to execute all requests related to teams */
class TeamRepository {

    /** Add new team
     * @param team the team data
     * @param onSuccess callback to execute if request is successful
     * @param onError callback to execute if request failed
     */
    fun addTeam(team: TeamModel, onSuccess: () -> Unit, onError: () -> Unit) {
        // Send a request to add the team
        val params = mapOf("team" to Utils.serializeObject(team))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().addTeam(params) },
            onSuccessCallback = { onSuccess() },
            onErrorCallback = { onError() }
        )
    }

    /** Update team
     * @param team the team data
     * @param onSuccess callback to execute if request is successful
     * @param onError callback to execute if request failed
     */
    fun updateTeam(team: TeamModel, onSuccess: () -> Unit, onError: () -> Unit) {
        val params = mapOf("team" to Utils.serializeObject(team))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().updateTeam(params) },
            onSuccessCallback = { onSuccess() },
            onErrorCallback = { onError() }
        )
    }

    /** Delete the team
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteTeam(teamId: Long, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteTeam(teamId) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /** Invite member to team
     * @param userId the user id to invite
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    fun inviteMember(userId: String, teamId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().inviteMember(userId, teamId) },
            onSuccessCallback = { response ->
                val members: MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Accept invite
     * @param userId the user id who accepted the invite
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    fun acceptInvite(userId: String, teamId: Long, onSuccess: (List<NotificationModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().acceptInvite(userId, teamId) },
            onSuccessCallback = { response -> onSuccess(response.data.map {NotificationModel(it)})},
        )
    }

    /** Remove member from team
     * @param recordId the record id to remove
     * @param onSuccess callback to execute if request is successful
     */
    fun removeMember(recordId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().removeMember(recordId) },
            onSuccessCallback = { response ->
                val members: MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Get the teams created by the user
     * @param onSuccess callback to execute if request is successful
     */
    fun getMyTeams(onSuccess: (List<TeamModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getMyTeams() },
            onSuccessCallback = { response -> onSuccess(response.data.map { TeamModel(it) } )},
        )
    }

    /** Perform search for users with the given name which are valid for team invitation
     * @param name the name to search
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    fun getUsersToInvite(name: String, teamId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getUsersToInvite(name, teamId) },
            onSuccessCallback = { response ->
                val members:MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }

    /** Get team members
     * @param teamId the team id
     * @param onSuccess callback to execute if request is successful
     */
    fun getTeamMembers(teamId: Long, onSuccess: (List<TeamMemberModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getTeamMembers(teamId) },
            onSuccessCallback = { response ->
                val members: MutableList<TeamMemberModel> = mutableListOf()

                if (response.data.isNotEmpty()) {
                    members.addAll(response.data.map { TeamMemberModel(it) })
                }

                onSuccess(members)
            },
        )
    }
}