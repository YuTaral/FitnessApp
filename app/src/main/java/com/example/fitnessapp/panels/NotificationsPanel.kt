package com.example.fitnessapp.panels

import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.NotificationsRecAdapter
import com.example.fitnessapp.interfaces.ITemporaryPanel
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
            notificationsRecycler.adapter = NotificationsRecAdapter(notifications)
        })
    }

    override fun addClickListeners() {}
}