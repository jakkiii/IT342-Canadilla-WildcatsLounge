package edu.cit.canadilla.wildcatslounge.mobile.feature.order.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.util.OrderDisplay
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.data.OrderRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.ui.adapter.OrdersAdapter
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {
	private val orderRepository = OrderRepository()
	private val menuRepository = MenuRepository()
	private lateinit var adapter: OrdersAdapter
	private var addonNames: Set<String> = emptySet()
	private val refreshHandler = Handler(Looper.getMainLooper())
	private val refreshRunnable = object : Runnable {
		override fun run() {
			loadOrders()
			refreshHandler.postDelayed(this, 5_000)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (SessionManager(this).getUser() == null) {
			finish()
			return
		}

		setContentView(R.layout.activity_orders)

		val recycler = findViewById<RecyclerView>(R.id.recyclerOrders)
		recycler.layoutManager = LinearLayoutManager(this)
		adapter = OrdersAdapter(mutableListOf(), addonNames)
		recycler.adapter = adapter

		findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
		findViewById<ImageButton>(R.id.btnRefresh).setOnClickListener { loadOrders() }

		lifecycleScope.launch {
			val menuRes = menuRepository.getMenuItems()
			if (menuRes.success && menuRes.data != null) {
				addonNames = OrderDisplay.createAddonNameSet(menuRes.data)
			}
			adapter = OrdersAdapter(mutableListOf(), addonNames)
			recycler.adapter = adapter
			loadOrders()
		}
	}

	override fun onResume() {
		super.onResume()
		refreshHandler.post(refreshRunnable)
	}

	override fun onPause() {
		refreshHandler.removeCallbacks(refreshRunnable)
		super.onPause()
	}

	private fun loadOrders() {
		lifecycleScope.launch {
			val response = orderRepository.getOrders()
			val emptyState = findViewById<LinearLayout>(R.id.ordersEmptyState)
			val recycler = findViewById<RecyclerView>(R.id.recyclerOrders)
			if (response.success && !response.data.isNullOrEmpty()) {
				adapter.updateOrders(response.data)
				emptyState.visibility = View.GONE
				recycler.visibility = View.VISIBLE
			} else {
				adapter.updateOrders(emptyList())
				emptyState.visibility = View.VISIBLE
				recycler.visibility = View.GONE
			}
		}
	}
}
