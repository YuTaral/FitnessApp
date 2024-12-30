package com.example.fitnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to display the notifications */
class NotificationsRecAdapter(data: List<NotificationModel>): RecyclerView.Adapter<NotificationsRecAdapter.ViewHolder>() {
    private var notifications = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_notification_item, parent,false))
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    /** Class to represent notification item view holder */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var text: TextView = itemView.findViewById(R.id.text)
        private var date: TextView = itemView.findViewById(R.id.date)

        /** Bind the view
         * @param item the notification model
         */
        fun bind(item: NotificationModel) {
            text.text = item.notificationText
            date.text = Utils.defaultFormatDateTime(item.dateTime)

            if (item.isActive) {
                itemView.setBackgroundResource(R.drawable.background_selected_team)
            } else {
                itemView.setBackgroundResource(R.drawable.background_transparent_accent_border)
            }
        }
    }
}