package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.JoinTeamNotificationModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Dialog to ask the user to join team or warn the team team owner when user accepts/decline invite */
class JoinTeamNotificationDialog(ctx: Context, model: JoinTeamNotificationModel): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_join_team
    override var dialogTitleId = 0

    private var notification = model

    private lateinit var image: ImageView
    private lateinit var description: TextView
    private lateinit var yesBtn: Button
    private lateinit var noBtn: Button

    override fun findViews() {
        super.findViews()

        image = dialog.findViewById(R.id.image)
        description = dialog.findViewById(R.id.description)
        yesBtn = dialog.findViewById(R.id.yes_btn)
        noBtn = dialog.findViewById(R.id.no_btn)
    }

    override fun populateDialog() {
        if (notification.notificationType == Constants.NotificationType.INVITED_TO_TEAM.toString()) {
            title.text = String.format(Utils.getActivity().getString(R.string.join_team_lbl), notification.teamName)
        }

        if (notification.teamImage.isNotEmpty()) {
            image.setImageBitmap(Utils.convertStringToBitmap(notification.teamImage))
        }

        description.text = notification.description
    }

    override fun addClickListeners() {
        super.addClickListeners()

        if (notification.notificationType == Constants.NotificationType.INVITED_TO_TEAM.toString()) {
            yesBtn.setOnClickListener {
                acceptInvite()
            }
            noBtn.setOnClickListener {
                declineInvite()
            }
        }
    }

    /** Send request to accept team invitation */
    private fun acceptInvite() {
        TeamRepository().acceptInvite(AppStateManager.user!!.id, notification.teamId, onSuccess = { notifications ->
            dismiss()
            Utils.getPanelAdapter().getNotificationsPanel()!!.populateNotifications(notifications)
        })
    }

    /** Send request to decline team invitation */
    private fun declineInvite() {
        TeamRepository().declineInvite(AppStateManager.user!!.id, notification.teamId, onSuccess = { notifications ->
            dismiss()
            Utils.getPanelAdapter().getNotificationsPanel()!!.populateNotifications(notifications)
        })
    }
}