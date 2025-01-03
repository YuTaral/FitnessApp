package com.example.fitnessapp.panels

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.NotificationsRecAdapter
import com.example.fitnessapp.dialogs.JoinTeamNotificationDialog
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.network.repositories.NotificationRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Notifications Panel class to implement the logic for managing notifications */
class NotificationsPanel: BasePanel(), ITemporaryPanel {
    override var id: Long = Constants.PanelUniqueId.NOTIFICATIONS.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_notifications
    override var panelIndex: Int = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId: Int = R.string.notifications_lbl
    override val removePreviousTemporary = true

    private lateinit var noNotificationsLbl: TextView
    private lateinit var notificationsRecycler: RecyclerView

    override fun findViews() {
        noNotificationsLbl = panel.findViewById(R.id.no_notifications)
        notificationsRecycler = panel.findViewById(R.id.notifications_recycler)
    }

    override fun populatePanel() {
        NotificationRepository().getNotifications(onSuccess = { notifications ->
            populateNotifications(notifications)
        })
    }

    override fun addClickListeners() {}

    /** Execute the callback on notification click and display the dialog for join team confirmation
     * @param notification the notification
     */
    private fun onNotificationClick(notification: NotificationModel) {
        if (notification.type == Constants.NotificationType.INVITED_TO_TEAM.toString()) {
            // Open dialog to accept / decline team invitation
            NotificationRepository().getNotificationDetails(notification.id, onSuccess = { details ->
                JoinTeamNotificationDialog(requireContext(), details).show()
            })

        } else if (notification.type == Constants.NotificationType.JOINED_TEAM.toString() ||
                   notification.type == Constants.NotificationType.DECLINED_TEAM_INVITATION.toString()) {

            // Mark the notification as inactive if it's still active
            if (notification.isActive) {
                NotificationRepository().notificationReviewed(notification.id, onSuccess = {
                    redirectToEditTeam(notification.teamId!!)
                })

            } else {
                redirectToEditTeam(notification.teamId!!)
            }
        }
    }

    /** Automatically select team after clicking on JOINED_TEAM / DECLINED_TEAM_INVITATION notification
     * @param teamId the team id to select
     */
    private fun redirectToEditTeam(teamId: Long) {
        // First create the Manage teams panel
        Utils.getPanelAdapter().displayTemporaryPanel(ManageTeamsPanel())

        // After that preselect the team and redirect to edit team
        Utils.getPanelAdapter().getManageTeamsPanel()!!.setAutoSelectTeam(teamId)
    }

    /** Send request to delete notification
     * @param notification the notification to remove
     */
    private fun deleteNotification(notification: NotificationModel) {
        NotificationRepository().deleteNotification(notification, onSuccess = { notifications ->
            // Update the list on success
            populateNotifications(notifications)
        })
    }

    /** Populate the notifications
     * @param notifications list of notifications
     */
    fun populateNotifications(notifications: List<NotificationModel>) {
        if (notifications.isEmpty()) {
            notificationsRecycler.visibility = View.GONE
            noNotificationsLbl.visibility = View.VISIBLE

        } else {
            noNotificationsLbl.visibility = View.GONE
            notificationsRecycler.visibility = View.VISIBLE

            if (notificationsRecycler.adapter == null) {
                notificationsRecycler.adapter = NotificationsRecAdapter(notifications,
                    callback = { notification -> onNotificationClick(notification) },
                    removeCallback = { notification -> deleteNotification(notification) })
            } else {
                (notificationsRecycler.adapter as NotificationsRecAdapter).updateData(notifications)
            }
        }
    }
}