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
class TeamsRecyclerAdapter(data: List<TeamModel>): RecyclerView.Adapter<TeamsRecyclerAdapter.TeamItem>() {
    private var teams = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamItem {
        return TeamItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_team_item, parent, false))
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    override fun onBindViewHolder(holder: TeamItem, position: Int) {
        holder.bind(teams[position])
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
        private var noMembersLbl: TextView = itemView.findViewById(R.id.no_members_lbl)
        private var membersContainer: LinearLayout = itemView.findViewById(R.id.members_container)

        /** Set the data for each team and adds click listener to expand / collapse the exercise
         * @param team the team */
        fun bind(team: TeamModel) {
            image.setImageBitmap(Utils.convertStringToBitmap(team.image))
            name.text = team.name
            description.text = team.description

            // Add expand mechanism
            expandSymbol.setOnClickListener {
                if (true) { // Replace with teams.membersCount == 0
                    if (noMembersLbl.visibility == View.VISIBLE) {
                        Utils.collapseView(noMembersLbl)
                        expandSymbol.animate().rotation(180f).setDuration(300).start()
                    } else {
                        Utils.expandView(noMembersLbl)
                        expandSymbol.animate().rotation(360f).setDuration(300).start()
                    }
                } else {
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