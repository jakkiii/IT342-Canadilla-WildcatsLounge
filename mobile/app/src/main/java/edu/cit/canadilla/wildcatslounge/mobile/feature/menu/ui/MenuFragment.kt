package edu.cit.canadilla.wildcatslounge.mobile.feature.menu.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import edu.cit.canadilla.wildcatslounge.mobile.core.util.applyOutlinedStyle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.core.util.MenuHelpers
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.data.CartRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.ui.adapter.MenuAdapter
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {
	private val menuRepository = MenuRepository()
	private val cartRepository = CartRepository()
	private lateinit var adapter: MenuAdapter
	private lateinit var sessionManager: SessionManager
	private var selectedCategory: String = MenuHelpers.CATEGORY_ALL
	private var allItems: List<MenuItemData> = emptyList()
	private var addonItems: List<MenuItemData> = emptyList()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.fragment_menu, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		sessionManager = SessionManager(requireContext())

		view.findViewById<TextView>(R.id.tvPageTitle).text = "Menu"
		view.findViewById<TextView>(R.id.tvPageSubtitle).text =
			"Customize your drink and add it to your cart."

		val recycler = view.findViewById<RecyclerView>(R.id.recyclerMenu)
		recycler.layoutManager = LinearLayoutManager(requireContext())
		adapter = MenuAdapter(mutableListOf()) { item ->
			MenuCustomizationBottomSheet.newInstance(item, addonItems) {
				Toast.makeText(requireContext(), "${item.name} added to cart.", Toast.LENGTH_SHORT).show()
				loadCartSummary(view)
			}.show(parentFragmentManager, "add_to_cart")
		}
		recycler.adapter = adapter

		setupFilters(view)

		viewLifecycleOwner.lifecycleScope.launch {
			loadMenu()
			if (sessionManager.getUserId() > 0) {
				loadCartSummary(view)
			}
		}
	}

	private fun setupFilters(view: View) {
		val filterAll = view.findViewById<Button>(R.id.filterAll)
		val filterCoffee = view.findViewById<Button>(R.id.filterCoffee)
		val filterDrinks = view.findViewById<Button>(R.id.filterLatte)
		val filterTreats = view.findViewById<Button>(R.id.filterMatcha)
		view.findViewById<Button>(R.id.filterBeverages).visibility = View.GONE
		view.findViewById<Button>(R.id.filterAddOns).visibility = View.GONE

		filterCoffee.text = MenuHelpers.tabLabel(MenuHelpers.CATEGORY_COFFEE)
		filterDrinks.text = MenuHelpers.tabLabel(MenuHelpers.CATEGORY_DRINKS)
		filterTreats.text = MenuHelpers.tabLabel(MenuHelpers.CATEGORY_TREAT)
		filterTreats.visibility = View.VISIBLE

		val filters = listOf(
			filterAll to MenuHelpers.CATEGORY_ALL,
			filterCoffee to MenuHelpers.CATEGORY_COFFEE,
			filterDrinks to MenuHelpers.CATEGORY_DRINKS,
			filterTreats to MenuHelpers.CATEGORY_TREAT
		)

		filters.forEach { (button, category) ->
			button.setOnClickListener {
				selectedCategory = category
				updateFilterStyles(view, button)
				applyMenuFilter()
			}
		}
		updateFilterStyles(view, filterAll)
	}

	private fun updateFilterStyles(view: View, selected: Button) {
		val group = view.findViewById<ViewGroup>(R.id.menuFilterGroup)
		val white = ContextCompat.getColor(requireContext(), R.color.white)
		val blue = ContextCompat.getColor(requireContext(), R.color.wildcats_blue)

		for (i in 0 until group.childCount) {
			val child = group.getChildAt(i) as? Button ?: continue
			if (child.visibility != View.VISIBLE) continue

			child.backgroundTintList = null
			if (child == selected) {
				child.setBackgroundResource(R.drawable.bg_filter_tab_selected)
				child.setTextColor(white)
			} else {
				child.setBackgroundResource(R.drawable.bg_filter_tab_unselected)
				child.setTextColor(blue)
			}
		}
	}

	private fun loadMenu() {
		viewLifecycleOwner.lifecycleScope.launch {
			val response = menuRepository.getMenuItems()
			if (response.success && response.data != null) {
				allItems = response.data
				addonItems = MenuHelpers.getAddonItems(allItems)
				applyMenuFilter()
			} else {
				allItems = emptyList()
				adapter.updateItems(emptyList())
				Toast.makeText(
					requireContext(),
					response.error ?: "Could not load menu.",
					Toast.LENGTH_LONG
				).show()
			}
		}
	}

	private fun applyMenuFilter() {
		val filtered = MenuHelpers.filterMenuByCategory(allItems, selectedCategory)
		adapter.updateItems(filtered)
	}

	private fun loadCartSummary(view: View) {
		viewLifecycleOwner.lifecycleScope.launch {
			val sticky = view.findViewById<LinearLayout>(R.id.menuStickyCheckout)
			val recycler = view.findViewById<RecyclerView>(R.id.recyclerMenu)
			val tvSummary = view.findViewById<TextView>(R.id.tvCartSummary)
			val tvSubtotal = view.findViewById<TextView>(R.id.tvCartSubtotal)
			val btnCheck = view.findViewById<MaterialButton>(R.id.btnCheckOrder)
			btnCheck.applyOutlinedStyle()

			if (sessionManager.getUserId() <= 0) {
				sticky.visibility = View.GONE
				updateMenuListPadding(recycler, sticky)
				return@launch
			}

			val response = cartRepository.getCart()
			if (response.success && response.data != null && response.data.items.isNotEmpty()) {
				val cart = response.data
				sticky.visibility = View.VISIBLE
				val itemCount = cart.items.sumOf { it.quantity }
				tvSummary.text = "$itemCount item(s)"
				tvSubtotal.text = "Subtotal ₱${"%.2f".format(cart.subtotal)}"
				btnCheck.setOnClickListener {
					requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
						.selectedItemId = R.id.nav_cart
				}
			} else {
				sticky.visibility = View.GONE
			}
			sticky.post { updateMenuListPadding(recycler, sticky) }
		}
	}

	private fun updateMenuListPadding(recycler: RecyclerView, sticky: View) {
		val density = resources.displayMetrics.density
		val basePad = (16 * density).toInt()
		val stickyPad = if (sticky.visibility == View.VISIBLE) {
			val measured = sticky.height
			if (measured > 0) measured else (72 * density).toInt()
		} else {
			0
		}
		recycler.setPadding(
			recycler.paddingLeft,
			recycler.paddingTop,
			recycler.paddingRight,
			basePad + stickyPad
		)
	}
}
