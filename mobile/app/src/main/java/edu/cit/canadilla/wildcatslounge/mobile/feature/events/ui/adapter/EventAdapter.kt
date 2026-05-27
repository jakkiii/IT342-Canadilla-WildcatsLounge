package edu.cit.canadilla.wildcatslounge.mobile.feature.events.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.EventData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class EventAdapter(
	private val events: MutableList<EventData>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

	fun updateEvents(newEvents: List<EventData>) {
		events.clear()
		events.addAll(newEvents)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
		return EventViewHolder(view)
	}

	override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
		holder.bind(events[position])
	}

	override fun getItemCount(): Int = events.size

	inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val tvMonth: TextView = itemView.findViewById(R.id.tvEventMonth)
		private val tvDay: TextView = itemView.findViewById(R.id.tvEventDay)
		private val tvTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
		private val tvDescription: TextView = itemView.findViewById(R.id.tvEventDescription)
		private val tvTime: TextView = itemView.findViewById(R.id.tvEventTime)
		private val tvLink: TextView = itemView.findViewById(R.id.tvEventLink)

		fun bind(event: EventData) {
			val badge = formatDateBadge(event.startDatetime)
			tvMonth.text = badge.month
			tvDay.text = badge.day
			tvTitle.text = event.title

			if (!event.description.isNullOrBlank()) {
				tvDescription.text = event.description
				tvDescription.isVisible = true
			} else {
				tvDescription.isVisible = false
			}

			tvTime.text = formatEventTime(event.startDatetime, event.endDatetime)

			if (!event.postLink.isNullOrBlank()) {
				tvLink.text = "View post for more details"
				tvLink.isVisible = true
				tvLink.setOnClickListener {
					val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.postLink))
					itemView.context.startActivity(intent)
				}
			} else {
				tvLink.isVisible = false
				tvLink.setOnClickListener(null)
			}
		}

		private fun formatDateBadge(raw: String): DateBadge {
			return runCatching {
				val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
					timeZone = TimeZone.getDefault()
				}
				val date = parser.parse(raw.take(19)) ?: return DateBadge("--", "--")
				val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase(Locale.getDefault())
				val day = SimpleDateFormat("d", Locale.getDefault()).format(date)
				DateBadge(month, day)
			}.getOrDefault(DateBadge("--", "--"))
		}

		private fun formatEventTime(start: String, end: String): String {
			return runCatching {
				val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
					timeZone = TimeZone.getDefault()
				}
				val timeFmt = SimpleDateFormat("EEE · h:mm a", Locale.getDefault())
				val startDate = parser.parse(start.take(19)) ?: return formatFallback(start, end)
				val endDate = parser.parse(end.take(19))
				val startLabel = timeFmt.format(startDate)
				val endTime = SimpleDateFormat("h:mm a", Locale.getDefault())
				if (endDate != null) {
					"$startLabel – ${endTime.format(endDate)}"
				} else {
					startLabel
				}
			}.getOrDefault(formatFallback(start, end))
		}

		private fun formatFallback(start: String, end: String): String {
			val startLabel = start.replace('T', ' ').take(16)
			val endLabel = end.replace('T', ' ').take(16)
			return "$startLabel – $endLabel"
		}
	}

	private data class DateBadge(val month: String, val day: String)
}
