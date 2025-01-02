package com.example.fitnessapp.panels

import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.NotificationsRecAdapter
import com.example.fitnessapp.dialogs.JoinTeamNotificationDialog
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.network.repositories.NotificationRepository
import com.example.fitnessapp.utils.Constants

/** Notifications Panel class to implement the logic for managing notifications */
class NotificationsPanel: BasePanel(), ITemporaryPanel {
    override var id: Long = Constants.PanelUniqueId.NOTIFICATIONS.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_notifications
    override var panelIndex: Int = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId: Int = R.string.notifications_lbl
    override val removePreviousTemporary = true

    private lateinit var notificationsRecycler: RecyclerView

    override fun findViews() {
        notificationsRecycler = panel.findViewById(R.id.notifications_recycler)
    }

    override fun populatePanel() {
        NotificationRepository().getNotifications(onSuccess = { notifications ->
            notificationsRecycler.adapter = NotificationsRecAdapter(notifications, callback = { notification ->
                onNotificationClick(notification)
            })
        })
    }

    override fun addClickListeners() {}


    /** Execute the callback on notification click and display the dialog for join team confirmation
     * @param notification the notification
     */
    private fun onNotificationClick(notification: NotificationModel) {
        if (notification.type == Constants.NotificationType.INVITED_TO_TEAM.toString()) {
            NotificationRepository().getNotificationDetails(notification.id, onSuccess = { details ->
                JoinTeamNotificationDialog(requireContext(), details).show()
            })
        }
    }

    /** Populate the notifications
     * @param notifications list of notifications
     */
    fun populateNotifications(notifications: List<NotificationModel>) {
        if (notificationsRecycler.adapter == null) {
            notificationsRecycler.adapter = NotificationsRecAdapter(notifications, callback = { notification ->
                onNotificationClick(notification)
            })
        } else {
            (notificationsRecycler.adapter as NotificationsRecAdapter).updateData(notifications)
        }
    }
}