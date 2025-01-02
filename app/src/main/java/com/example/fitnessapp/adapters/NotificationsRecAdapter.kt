package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.utils.Utils

/** Recycler adapter to display the notifications */
class NotificationsRecAdapter(data: List<NotificationModel>, callback: (NotificationModel) -> Unit): RecyclerView.Adapter<NotificationsRecAdapter.ViewHolder>() {
    private var notifications = data
    private var onClickCallback = callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_notification_item, parent,false))
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position], onClickCallback)
    }

    /** Update the notifications
     * @param data the new notifications
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<NotificationModel>) {
        notifications = data
        notifyDataSetChanged()
    }

    /** Class to represent notification item view holder */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var image: ImageView = itemView.findViewById(R.id.image)
        private var text: TextView = itemView.findViewById(R.id.text)
        private var date: TextView = itemView.findViewById(R.id.date)

        /** Bind the view
         * @param item the notification model
         * @param onClickCallback callback to execute on row click
         */
        fun bind(item: NotificationModel, onClickCallback: (NotificationModel) -> Unit) {
            if (item.image.isNotEmpty()) {
                image.setImageBitmap(Utils.convertStringToBitmap(item.image))
            } else {
                image.setBackgroundResource(R.drawable.icon_profile)
            }

            text.text = item.notificationText
            date.text = Utils.defaultFormatDateTime(item.dateTime)

            if (item.isActive) {
                itemView.setBackgroundResource(R.drawable.background_selected_team)
            } else {
                itemView.setBackgroundResource(R.drawable.background_transparent_accent_border)
            }

            itemView.setOnClickListener {
                onClickCallback(item)
            }
        }
    }
}