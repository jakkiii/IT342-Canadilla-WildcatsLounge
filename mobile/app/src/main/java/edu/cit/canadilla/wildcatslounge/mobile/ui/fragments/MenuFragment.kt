package edu.cit.canadilla.wildcatslounge.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.model.AddCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.MenuCategory
import edu.cit.canadilla.wildcatslounge.mobile.model.MenuItemData
import edu.cit.canadilla.wildcatslounge.mobile.model.ServingType
import edu.cit.canadilla.wildcatslounge.mobile.repository.CartRepository
import edu.cit.canadilla.wildcatslounge.mobile.repository.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.ui.adapter.MenuAdapter
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {
    private val menuRepository = MenuRepository()
    private val cartRepository = CartRepository()
    private lateinit var adapter: MenuAdapter
    private var selectedCategory: MenuCategory? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMenu)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = MenuAdapter(mutableListOf()) { item, serving ->
            if (userId <= 0) return@MenuAdapter
            viewLifecycleOwner.lifecycleScope.launch {
                cartRepository.addCartItem(
                    userId,
                    AddCartItemRequest(item.id, 1, serving, "")
                )
                loadCartSummary(view, userId)
            }
        }
        recycler.adapter = adapter

        setupFilters(view)

        viewLifecycleOwner.lifecycleScope.launch {
            loadMenu()
            if (userId > 0) {
                loadCartSummary(view, userId)
            }
        }
    }

    private fun setupFilters(view: View) {
        val filterAll = view.findViewById<Button>(R.id.filterAll)
        val filterCoffee = view.findViewById<Button>(R.id.filterCoffee)
        val filterLatte = view.findViewById<Button>(R.id.filterLatte)
        val filterMatcha = view.findViewById<Button>(R.id.filterMatcha)
        val filterBeverages = view.findViewById<Button>(R.id.filterBeverages)
        val filterAddOns = view.findViewById<Button>(R.id.filterAddOns)

        filterAll.setOnClickListener { selectedCategory = null; loadMenu() }
        filterCoffee.setOnClickListener { selectedCategory = MenuCategory.COFFEE; loadMenu() }
        filterLatte.setOnClickListener { selectedCategory = MenuCategory.FLAVORED_LATTE; loadMenu() }
        filterMatcha.setOnClickListener { selectedCategory = MenuCategory.MATCHA_SERIES; loadMenu() }
        filterBeverages.setOnClickListener { selectedCategory = MenuCategory.BEVERAGES; loadMenu() }
        filterAddOns.setOnClickListener { selectedCategory = MenuCategory.COFFEE_ADD_ON; loadMenu() }
    }

    private fun loadMenu() {
        viewLifecycleOwner.lifecycleScope.launch {
            val category = selectedCategory?.name
            val response = menuRepository.getMenuItems(category)
            if (response.success && response.data != null) {
                adapter.updateItems(response.data)
            }
        }
    }

    private fun loadCartSummary(view: View, userId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = cartRepository.getCart(userId)
            val sticky = view.findViewById<LinearLayout>(R.id.menuStickyCheckout)
            val tvSummary = view.findViewById<TextView>(R.id.tvCartSummary)
            val tvSubtotal = view.findViewById<TextView>(R.id.tvCartSubtotal)
            val btnCheck = view.findViewById<Button>(R.id.btnCheckOrder)

            if (response.success && response.data != null && response.data.items.isNotEmpty()) {
                val cart = response.data
                sticky.visibility = View.VISIBLE
                tvSummary.text = "${cart.itemCount} item(s)"
                tvSubtotal.text = "Subtotal P${cart.subtotal.toInt()}"
                btnCheck.setOnClickListener {
                    requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                        .selectedItemId = R.id.nav_cart
                }
            } else {
                sticky.visibility = View.GONE
            }
        }
    }
}
