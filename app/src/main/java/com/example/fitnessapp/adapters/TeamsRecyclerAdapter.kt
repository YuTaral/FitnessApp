package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.utils.Utils


/** Recycler adapter to control and show the teams */
class TeamsRecyclerAdapter(data: List<TeamModel>, callback: () -> Unit): RecyclerView.Adapter<TeamsRecyclerAdapter.TeamItem>() {
    private var teams = data
    private var onClickCallback = callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamItem {
        return TeamItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_team_item, parent, false))
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    override fun onBindViewHolder(holder: TeamItem, position: Int) {
        holder.bind(teams[position], selectUnselectTeamCallback = { team ->
            changeSelectionState(team)
            onClickCallback()
        })
    }

    /** Change item selection state
     * @param team the team where the click occurred
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun changeSelectionState(team: TeamModel) {
        val updatedTeams = teams

        for (t: TeamModel in updatedTeams) {
            if (t.id == team.id) {
                // Mark the team as selected/unselected, depending on the current state
                t.selectedInPanel = !team.selectedInPanel

            } else {
                // Mark all other teams as unselected
                t.selectedInPanel = false

            }
        }

        teams = updatedTeams

        notifyDataSetChanged()
    }

    /** Return true if there is currently selected item, false otherwise*/
    fun isTeamSelected(): Boolean {
        return teams.any { it.selectedInPanel }
    }

    /** Update the teams list
     * @param newTeams the updated list
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateTeams(newTeams: List<TeamModel>) {
        teams = newTeams
        notifyDataSetChanged()
    }

    /** Class to represent team item view holder - each team */
    class TeamItem(view: View) : RecyclerView.ViewHolder(view) {
        private var image: ImageView = itemView.findViewById(R.id.image)
        private var name: TextView = itemView.findViewById(R.id.name)
        private var description: TextView = itemView.findViewById(R.id.description)
        private var expandSymbol: ImageView = itemView.findViewById(R.id.expand_collapse_symbol)
        private var membersContainer: LinearLayout = itemView.findViewById(R.id.members_container)

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

            if (true) { // Replace with teams.membersCount == 0
                expandSymbol.visibility = View.GONE

            } else {
                expandSymbol.visibility = View.VISIBLE

                // Add expand mechanism
                expandSymbol.setOnClickListener {
                    if (membersContainer.visibility == View.VISIBLE) {
                        Utils.collapseView(membersContainer)
                        expandSymbol.animate().rotation(180f).setDuration(300).start()
                    } else {
                        Utils.expandView(membersContainer)
                        expandSymbol.animate().rotation(360f).setDuration(300).start()
                    }
                }
            }
        }
    }
}