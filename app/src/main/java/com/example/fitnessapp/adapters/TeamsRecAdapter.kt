package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control and show the teams */
class TeamsRecAdapter(data: List<TeamModel>, callback: (TeamModel) -> Unit) : RecyclerView.Adapter<TeamsRecAdapter.ViewHolder>() {
    private var teams = data.toMutableList()
    private var onClickCallback = callback
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.inflatable_team_item, parent, false))
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teams[position], selectUnselectTeamCallback = { team ->
            changeSelectionState(position)
            onClickCallback(team)
        })
    }

    /** Change item selection state
     * @param position the position of the team where the click occurred
     */
    private fun changeSelectionState(position: Int) {
        if (position == selectedPosition) {
            // Deselect the currently selected item
            teams[position].selectedInPanel = false
            notifyItemChanged(position)
            selectedPosition = RecyclerView.NO_POSITION

        } else {
            // Deselect the previously selected item
            val previousSelectedPosition = selectedPosition
            selectedPosition = position

            if (previousSelectedPosition != RecyclerView.NO_POSITION) {
                teams[previousSelectedPosition].selectedInPanel = false
                notifyItemChanged(previousSelectedPosition)
            }

            // Select the new team
            teams[position].selectedInPanel = true
            notifyItemChanged(position)
        }
    }

    /** Return true if there is currently selected item, false otherwise */
    fun getSelectedTeam(): TeamModel? {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return null
        }
        return teams[selectedPosition]
    }

    /** Programmatically set the selected team, return true on success, false otherwise
     * @param teamId the team id if selecting team, 0 if removing the selected team
     */
    @SuppressLint("NotifyDataSetChanged")
    fun changeSelectedTeam(teamId: Long): Boolean {
        val result: Boolean

        if (teamId == 0L) {
            selectedPosition = RecyclerView.NO_POSITION
            notifyDataSetChanged()
            result = true
        } else {
            val position = teams.indexOfFirst { it.id == teamId }

            result = if (position > -1) {
                changeSelectionState(position)
                true
            } else {
                false
            }
        }

        return result
    }

    /** Update the teams list
     * @param newTeams the updated list
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateTeams(newTeams: List<TeamModel>) {
        teams = newTeams.toMutableList()
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    /** Class to represent team item view holder - each team */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image: ImageView = itemView.findViewById(R.id.image)
        private var name: TextView = itemView.findViewById(R.id.name)
        private var description: TextView = itemView.findViewById(R.id.description)

        /** Set the data for each team and adds click listener to expand / collapse the exercise
         * @param team the team
         * @param selectUnselectTeamCallback the callback to execute on click
         */
        fun bind(team: TeamModel, selectUnselectTeamCallback: (TeamModel) -> Unit) {
            image.setImageBitmap(Utils.convertStringToBitmap(team.image))
            name.text = team.name
            description.text = team.description

            // Mark the team as selected / unselected
            if (team.selectedInPanel) {
                itemView.setBackgroundResource(R.drawable.background_selected_team)
            } else {
                itemView.setBackgroundResource(R.drawable.background_transparent_accent_border)
            }

            itemView.setOnClickListener {
                selectUnselectTeamCallback(team)
            }
        }
    }
}