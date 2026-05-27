package edu.cit.canadilla.wildcatslounge.mobile.feature.events.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.feature.events.data.EventRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.events.ui.adapter.EventAdapter
import kotlinx.coroutines.launch

class EventsFragment : Fragment() {
	private val eventRepository = EventRepository()
	private lateinit var adapter: EventAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.fragment_events, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		view.findViewById<TextView>(R.id.tvPageTitle).text = "Events"
		view.findViewById<TextView>(R.id.tvPageSubtitle).text =
			"Campus happenings at Wildcats Lounge"

		val tvEventsSummary = view.findViewById<TextView>(R.id.tvEventsSummary)
		val tvEventsEmpty = view.findViewById<TextView>(R.id.tvEventsEmpty)
		val recycler = view.findViewById<RecyclerView>(R.id.recyclerEvents)

		recycler.layoutManager = LinearLayoutManager(requireContext())
		adapter = EventAdapter(mutableListOf())
		recycler.adapter = adapter

		viewLifecycleOwner.lifecycleScope.launch {
			val response = eventRepository.getEvents()
			if (response.success && !response.data.isNullOrEmpty()) {
				val events = response.data
				tvEventsSummary.text =
					"${events.size} upcoming event${if (events.size == 1) "" else "s"} — tap a card for details."
				adapter.updateEvents(events)
				recycler.visibility = View.VISIBLE
				tvEventsEmpty.visibility = View.GONE
			} else {
				tvEventsSummary.text = "Stay tuned for lounge events and campus specials."
				adapter.updateEvents(emptyList())
				recycler.visibility = View.GONE
				tvEventsEmpty.visibility = View.VISIBLE
			}
		}
	}
}
