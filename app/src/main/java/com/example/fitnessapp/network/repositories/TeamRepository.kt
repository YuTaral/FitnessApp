package com.example.fitnessapp.network.repositories

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

    /** Get the teams created by the user
     * @param onSuccess callback to execute if request is successful
     */
    fun getMyTeams(onSuccess: (List<TeamModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getMyTeams() },
            onSuccessCallback = { response -> onSuccess(response.data.map { TeamModel(it) } )},
        )
    }
}