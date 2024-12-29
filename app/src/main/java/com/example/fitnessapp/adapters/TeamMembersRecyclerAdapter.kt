package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to control the data (team members)
 * shown when searching for members and displaying them in the Invite Members Dialog
 */
class TeamMembersRecyclerAdapter(data: List<TeamMemberModel>,
                                 adapterType: AdapterType,
                                 callback: (member: TeamMemberModel) -> Unit):
    RecyclerView.Adapter<TeamMembersRecyclerAdapter.TeamMemberItem>() {


    /** Enum containing the adapter type, the adapter is used on many places in the app */
    enum class AdapterType {
        INVITE,
        REMOVE,
        DISPLAY
    }

    private var members = data.toMutableList()
    private var type = adapterType
    private var onClickCallback = callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberItem {
        return TeamMemberItem(LayoutInflater.from(parent.context)
                                .inflate(R.layout.inflatable_team_member_item, parent, false))
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: TeamMemberItem, position: Int) {
        holder.bind(members[position], type, onClickCallback)
    }

    /** Update the data in the adapter
     * @param newMembers the new members
     */
    @SuppressLint("NotifyDataSetChanged")
    fun update(newMembers: List<TeamMemberModel>) {
        members = newMembers.toMutableList()
        notifyDataSetChanged()
    }

    /** Change the member state to invited / removed and refresh the data
     * @param add true if the member is being invited, false if removed
     */
    fun addRemoveMember(member: TeamMemberModel, add: Boolean) {
        if (add) {
            members.add(member)
            notifyItemInserted(members.size - 1)

        } else {
            val index = members.indexOf(member)
            members.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    /** Class to represent team member item view holder - each team member */
    class TeamMemberItem(view: View): RecyclerView.ViewHolder(view) {

        /** User -> Team state - whether the user has been invited, removed and etc. */
        enum class MemberTeamState(private var stringId: Int, private var colorId: Int) {
            NOT_INVITED(0,0),
            INVITED(R.string.invited_lbl, R.color.orange),
            ACCEPTED(R.string.in_team_lbl, R.color.green);

            fun getColorId(): Int {
                return colorId
            }

            fun getText(): String {
                return Utils.getActivity().getString(stringId)
            }
        }

        private var image = view.findViewById<ImageView>(R.id.image)
        private var name = view.findViewById<TextView>(R.id.name)
        private var memberState = view.findViewById<TextView>(R.id.member_state)
        private var inviteRemoveSymbol = view.findViewById<ImageView>(R.id.invite_remove_symbol)

        /** Populate the data in the item
         * @param member the member to add
         * @param adapterType the adapter type
         * @param onClickCallback callback to execute on click
         */
        fun bind(member: TeamMemberModel, adapterType: AdapterType, onClickCallback: (member: TeamMemberModel) -> Unit) {
            image.setImageBitmap(Utils.convertStringToBitmap(member.image))
            name.text = member.fullName
            
            when (adapterType) {
                AdapterType.INVITE -> {
                    memberState.visibility = View.GONE
                    inviteRemoveSymbol.setBackgroundResource(R.drawable.icon_invite_member)

                    inviteRemoveSymbol.setOnClickListener {
                        // Execute the callback to move the team into the members section
                        onClickCallback(member)
                    }
                }
                AdapterType.REMOVE -> {
                    setState(member)
                    inviteRemoveSymbol.setBackgroundResource(R.drawable.icon_remove_member)

                    inviteRemoveSymbol.setOnClickListener {
                        // Execute the callback to remove the mark the item as removed
                        onClickCallback(member)
                    }
                }
                else -> {
                    setState(member)
                    inviteRemoveSymbol.visibility = View.GONE
                }
            }
        }

        /** Set the text and color of th state view depending on the current member.teamState value*/
        private fun setState(member: TeamMemberModel) {
            val state = MemberTeamState.valueOf(member.teamState)

            if (state == MemberTeamState.NOT_INVITED) {
                memberState.visibility = View.GONE
            } else {
                memberState.visibility = View.VISIBLE
                memberState.text = state.getText()
                memberState.setTextColor(Utils.getActivity().getColor(state.getColorId()))
            }
        }
    }
}