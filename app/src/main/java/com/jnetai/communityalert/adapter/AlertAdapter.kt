package com.jnetai.communityalert.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.jnetai.communityalert.R
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import java.time.format.DateTimeFormatter

class AlertAdapter(
    private val onAcknowledge: (Long) -> Unit,
    private val onDismiss: (Long) -> Unit,
    private val onClick: (Alert) -> Unit
) : ListAdapter<Alert, AlertAdapter.AlertViewHolder>(AlertDiffCallback()) {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    inner class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView: CardView = view.findViewById(R.id.cardAlert)
        private val titleText: TextView = view.findViewById(R.id.textTitle)
        private val descriptionText: TextView = view.findViewById(R.id.textDescription)
        private val categoryText: TextView = view.findViewById(R.id.textCategory)
        private val severityText: TextView = view.findViewById(R.id.textSeverity)
        private val dateText: TextView = view.findViewById(R.id.textDate)
        private val statusText: TextView = view.findViewById(R.id.textStatus)
        private val categoryIcon: ImageView = view.findViewById(R.id.iconCategory)
        private val btnAcknowledge: View = view.findViewById(R.id.btnAcknowledge)
        private val btnDismiss: View = view.findViewById(R.id.btnDismiss)

        fun bind(alert: Alert) {
            titleText.text = alert.title
            descriptionText.text = alert.description
            categoryText.text = alert.category.displayName
            dateText.text = alert.createdAt.format(dateTimeFormatter)

            // Severity styling
            val (sevText, sevColor) = when (alert.severity) {
                Severity.CRITICAL -> Pair("CRITICAL", R.color.severity_critical)
                Severity.WARNING -> Pair("WARNING", R.color.severity_warning)
                Severity.INFO -> Pair("INFO", R.color.severity_info)
            }
            severityText.text = sevText
            severityText.setTextColor(ContextCompat.getColor(itemView.context, sevColor))

            // Category icon
            val iconRes = when (alert.category) {
                AlertCategory.WEATHER -> R.drawable.ic_weather
                AlertCategory.SAFETY -> R.drawable.ic_safety
                AlertCategory.TRAFFIC -> R.drawable.ic_traffic
                AlertCategory.COMMUNITY -> R.drawable.ic_community
            }
            categoryIcon.setImageResource(iconRes)

            // Status
            when {
                alert.dismissed -> {
                    statusText.text = "Dismissed"
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.severity_info))
                    btnAcknowledge.visibility = View.GONE
                    btnDismiss.visibility = View.GONE
                }
                alert.acknowledged -> {
                    statusText.text = "Acknowledged ✓"
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.severity_info))
                    btnAcknowledge.visibility = View.GONE
                    btnDismiss.visibility = View.VISIBLE
                }
                else -> {
                    statusText.text = "Active"
                    statusText.setTextColor(ContextCompat.getColor(itemView.context, R.color.severity_warning))
                    btnAcknowledge.visibility = View.VISIBLE
                    btnDismiss.visibility = View.VISIBLE
                }
            }

            btnAcknowledge.setOnClickListener { onAcknowledge(alert.id) }
            btnDismiss.setOnClickListener { onDismiss(alert.id) }
            cardView.setOnClickListener { onClick(alert) }

            // Card stroke for severity
            val strokeColor = when (alert.severity) {
                Severity.CRITICAL -> ContextCompat.getColor(itemView.context, R.color.severity_critical)
                Severity.WARNING -> ContextCompat.getColor(itemView.context, R.color.severity_warning)
                Severity.INFO -> ContextCompat.getColor(itemView.context, R.color.severity_info)
            }
            cardView.strokeColor = strokeColor
            cardView.strokeWidth = 2
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean =
            oldItem == newItem
    }
}