package edu.cit.canadilla.wildcatslounge.mobile.feature.home.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyOutlinedStyle
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyPrimaryStyle
import edu.cit.canadilla.wildcatslounge.mobile.feature.events.data.EventRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.lounge.data.LoungeRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.data.OrderRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.ui.OrdersActivity
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {
	private val menuRepository = MenuRepository()
	private val orderRepository = OrderRepository()
	private val eventRepository = EventRepository()
	private val loungeRepository = LoungeRepository()
	private val refreshHandler = Handler(Looper.getMainLooper())

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.fragment_home, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val sessionManager = SessionManager(requireContext())
		val tvGreetingLine = view.findViewById<TextView>(R.id.tvGreetingLine)
		val tvHomeTitle = view.findViewById<TextView>(R.id.tvHomeTitle)
		val tvHomeSubtitle = view.findViewById<TextView>(R.id.tvHomeSubtitle)
		val cardLounge = view.findViewById<LinearLayout>(R.id.cardLounge)
		val tvLoungeStatus = view.findViewById<TextView>(R.id.tvLoungeStatus)
		val cardActiveOrder = view.findViewById<LinearLayout>(R.id.cardActiveOrder)
		val tvOrderNumber = view.findViewById<TextView>(R.id.tvOrderNumber)
		val tvOrderStatus = view.findViewById<TextView>(R.id.tvOrderStatus)
		val tvOrderBadge = view.findViewById<TextView>(R.id.tvOrderBadge)
		val tvFeaturedMenu = view.findViewById<TextView>(R.id.tvFeaturedMenu)
		val tvFeaturedMenuDesc = view.findViewById<TextView>(R.id.tvFeaturedMenuDesc)
		val tvFeaturedEvent = view.findViewById<TextView>(R.id.tvFeaturedEvent)
		val tvFeaturedEventMeta = view.findViewById<TextView>(R.id.tvFeaturedEventMeta)
		val btnStartOrder = view.findViewById<MaterialButton>(R.id.btnStartOrder)
		val btnViewOrders = view.findViewById<MaterialButton>(R.id.btnViewOrders)
		val btnRefreshLounge = view.findViewById<ImageButton>(R.id.btnRefreshLounge)

		btnStartOrder.applyPrimaryStyle()
		btnViewOrders.applyOutlinedStyle()

		val firstName = sessionManager.getUser()?.firstname ?: "there"
		tvGreetingLine.text = timeGreeting()
		tvHomeTitle.text = "Hello, $firstName!"

		btnStartOrder.setOnClickListener {
			requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
				.selectedItemId = R.id.nav_menu
		}

		btnViewOrders.setOnClickListener {
			startActivity(Intent(requireContext(), OrdersActivity::class.java))
		}

		btnRefreshLounge.setOnClickListener {
			loadDashboard(
				cardLounge, tvLoungeStatus, cardActiveOrder,
				tvOrderNumber, tvOrderStatus, tvOrderBadge,
				tvFeaturedMenu, tvFeaturedMenuDesc, tvFeaturedEvent, tvFeaturedEventMeta,
				sessionManager
			)
		}

		val refreshRunnable = object : Runnable {
			override fun run() {
				loadDashboard(
					cardLounge, tvLoungeStatus, cardActiveOrder,
					tvOrderNumber, tvOrderStatus, tvOrderBadge,
					tvFeaturedMenu, tvFeaturedMenuDesc, tvFeaturedEvent, tvFeaturedEventMeta,
					sessionManager
				)
				refreshHandler.postDelayed(this, 10_000)
			}
		}

		loadDashboard(
			cardLounge, tvLoungeStatus, cardActiveOrder,
			tvOrderNumber, tvOrderStatus, tvOrderBadge,
			tvFeaturedMenu, tvFeaturedMenuDesc, tvFeaturedEvent, tvFeaturedEventMeta,
			sessionManager
		)
		refreshHandler.postDelayed(refreshRunnable, 10_000)
	}

	override fun onDestroyView() {
		refreshHandler.removeCallbacksAndMessages(null)
		super.onDestroyView()
	}

	private fun timeGreeting(): String {
		return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
			in 0..11 -> "Good morning"
			in 12..16 -> "Good afternoon"
			else -> "Good evening"
		}
	}

	private fun loadDashboard(
		cardLounge: LinearLayout,
		tvLoungeStatus: TextView,
		cardActiveOrder: LinearLayout,
		tvOrderNumber: TextView,
		tvOrderStatus: TextView,
		tvOrderBadge: TextView,
		tvFeaturedMenu: TextView,
		tvFeaturedMenuDesc: TextView,
		tvFeaturedEvent: TextView,
		tvFeaturedEventMeta: TextView,
		sessionManager: SessionManager
	) {
		viewLifecycleOwner.lifecycleScope.launch {
			val lounge = loungeRepository.getStatus()
			if (lounge.success && lounge.data != null) {
				tvLoungeStatus.text = lounge.data.displayLabel
				applyLoungeStyle(cardLounge, tvLoungeStatus, lounge.data.color)
			} else {
				tvLoungeStatus.text = "Available"
				applyLoungeStyle(cardLounge, tvLoungeStatus, "green")
			}

			val menu = menuRepository.getMenuItems()
			if (menu.success && !menu.data.isNullOrEmpty()) {
				val featured = menu.data.firstOrNull { it.isAvailable } ?: menu.data.first()
				tvFeaturedMenu.text = featured.name
				tvFeaturedMenuDesc.text = featured.description?.takeIf { it.isNotBlank() }
					?: "₱${"%.2f".format(featured.price)} · Tap Start Order to customize."
			} else {
				tvFeaturedMenu.text = "Menu highlights coming soon"
				tvFeaturedMenuDesc.text = "Check back shortly for fresh picks."
			}

			val todayEvents = eventRepository.getTodayEvents()
			val event = when {
				todayEvents.success && !todayEvents.data.isNullOrEmpty() -> todayEvents.data.first()
				else -> {
					val events = eventRepository.getEvents()
					if (events.success && !events.data.isNullOrEmpty()) events.data.first() else null
				}
			}
			if (event != null) {
				tvFeaturedEvent.text = event.title
				tvFeaturedEventMeta.text = formatEventRange(event.startDatetime, event.endDatetime)
			} else {
				tvFeaturedEvent.text = "No events today"
				tvFeaturedEventMeta.text = "Visit the Events tab for the full schedule."
			}

			if (sessionManager.getUser() == null) {
				cardActiveOrder.isVisible = false
				return@launch
			}

			val orders = orderRepository.getOrders()
			val active = orders.data?.firstOrNull {
				it.status in listOf("pending", "preparing", "ready")
			}
			if (orders.success && active != null) {
				cardActiveOrder.isVisible = true
				tvOrderNumber.text = active.orderNumber
				tvOrderStatus.text = "₱${"%.2f".format(active.totalAmount)}"
				val label = active.status.replaceFirstChar { it.uppercase() }
				tvOrderBadge.text = label
				tvOrderBadge.setTextColor(statusColor(active.status))
			} else {
				cardActiveOrder.isVisible = false
			}
		}
	}

	private fun applyLoungeStyle(card: LinearLayout, label: TextView, colorKey: String) {
		val bg = when (colorKey.lowercase()) {
			"yellow" -> R.drawable.bg_lounge_yellow
			"red" -> R.drawable.bg_lounge_red
			else -> R.drawable.bg_lounge_green
		}
		val textColor = when (colorKey.lowercase()) {
			"yellow" -> R.color.wildcats_warning
			"red" -> R.color.wildcats_error
			else -> R.color.wildcats_success
		}
		card.setBackgroundResource(bg)
		label.setTextColor(ContextCompat.getColor(requireContext(), textColor))
	}

	private fun statusColor(status: String): Int {
		val res = when (status.lowercase()) {
			"preparing" -> R.color.wildcats_warning
			"ready" -> R.color.wildcats_success
			"completed" -> R.color.text_muted
			else -> R.color.text_secondary
		}
		return ContextCompat.getColor(requireContext(), res)
	}

	private fun formatEventRange(start: String, end: String): String {
		val startLabel = start.replace('T', ' ').take(16)
		val endLabel = end.replace('T', ' ').take(16)
		return "$startLabel – $endLabel"
	}
}
