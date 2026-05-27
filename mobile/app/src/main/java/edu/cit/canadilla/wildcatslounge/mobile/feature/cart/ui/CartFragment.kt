package edu.cit.canadilla.wildcatslounge.mobile.feature.cart.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.GroupedCartItem
import edu.cit.canadilla.wildcatslounge.mobile.core.util.OrderDisplay
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyPrimaryStyle
import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data.CartRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.ui.adapter.CartAdapter
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.data.OrderRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.order.ui.OrdersActivity
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
	private val cartRepository = CartRepository()
	private val orderRepository = OrderRepository()
	private val menuRepository = MenuRepository()
	private lateinit var adapter: CartAdapter
	private lateinit var sessionManager: SessionManager
	private var addonNames: Set<String> = emptySet()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.fragment_cart, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		sessionManager = SessionManager(requireContext())

		view.findViewById<TextView>(R.id.tvPageTitle).text = "Cart"
		view.findViewById<TextView>(R.id.tvPageSubtitle).text =
			"Review items before placing your order"

		val recycler = view.findViewById<RecyclerView>(R.id.recyclerCart)
		recycler.layoutManager = LinearLayoutManager(requireContext())
		adapter = CartAdapter(mutableListOf(), { group, quantity ->
			viewLifecycleOwner.lifecycleScope.launch {
				if (quantity <= 0) {
					cartRepository.removeCartItem(group.main.id)
				} else {
					cartRepository.updateCartItem(group.main.id, quantity)
				}
				loadCart(view)
			}
		}, { group ->
			viewLifecycleOwner.lifecycleScope.launch {
				cartRepository.removeCartItem(group.main.id)
				loadCart(view)
			}
		})
		recycler.adapter = adapter

		view.findViewById<MaterialButton>(R.id.btnBrowseMenu).setOnClickListener {
			requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
				.selectedItemId = R.id.nav_menu
		}

		val btnPlaceOrder = view.findViewById<MaterialButton>(R.id.btnPlaceOrder)
		btnPlaceOrder.applyPrimaryStyle()
		btnPlaceOrder.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				val response = orderRepository.placeOrder()
				if (response.success) {
					Toast.makeText(
						requireContext(),
						"Order placed successfully.",
						Toast.LENGTH_LONG
					).show()
					startActivity(Intent(requireContext(), OrdersActivity::class.java))
					loadCart(view)
				} else {
					Toast.makeText(
						requireContext(),
						response.error ?: "Could not place order.",
						Toast.LENGTH_LONG
					).show()
				}
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			val menuRes = menuRepository.getMenuItems()
			if (menuRes.success && menuRes.data != null) {
				addonNames = OrderDisplay.createAddonNameSet(menuRes.data)
			}
			if (sessionManager.getUserId() > 0) {
				loadCart(view)
			}
		}
	}

	private fun loadCart(view: View) {
		viewLifecycleOwner.lifecycleScope.launch {
			val emptyState = view.findViewById<LinearLayout>(R.id.cartEmptyState)
			val recycler = view.findViewById<RecyclerView>(R.id.recyclerCart)
			val tvSubtotal = view.findViewById<TextView>(R.id.tvSubtotal)

			if (sessionManager.getUserId() <= 0) {
				emptyState.visibility = View.VISIBLE
				recycler.visibility = View.GONE
				adapter.updateItems(emptyList())
				tvSubtotal.text = "₱0.00"
				return@launch
			}

			val response = cartRepository.getCart()
			if (response.success && response.data != null) {
				val cart = response.data
				val grouped: List<GroupedCartItem> =
					OrderDisplay.groupCartItems(cart.items, addonNames)
				adapter.updateItems(grouped)
				tvSubtotal.text = "₱${"%.2f".format(cart.subtotal)}"
				val isEmpty = grouped.isEmpty()
				emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
				recycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
			} else {
				adapter.updateItems(emptyList())
				tvSubtotal.text = "₱0.00"
				emptyState.visibility = View.VISIBLE
				recycler.visibility = View.GONE
			}
		}
	}
}
