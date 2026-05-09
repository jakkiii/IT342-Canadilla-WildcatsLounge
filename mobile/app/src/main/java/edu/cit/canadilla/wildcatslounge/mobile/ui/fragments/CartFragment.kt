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
import edu.cit.canadilla.wildcatslounge.mobile.model.UpdateCartItemRequest
import edu.cit.canadilla.wildcatslounge.mobile.repository.CartRepository
import edu.cit.canadilla.wildcatslounge.mobile.repository.OrderRepository
import edu.cit.canadilla.wildcatslounge.mobile.ui.adapter.CartAdapter
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private val cartRepository = CartRepository()
    private val orderRepository = OrderRepository()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCart)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartAdapter(mutableListOf(), { item, quantity ->
            if (userId <= 0) return@CartAdapter
            viewLifecycleOwner.lifecycleScope.launch {
                if (quantity <= 0) {
                    cartRepository.removeCartItem(userId, item.id)
                } else {
                    cartRepository.updateCartItem(
                        userId,
                        item.id,
                        UpdateCartItemRequest(quantity, item.servingType, item.customizationNotes ?: "")
                    )
                }
                loadCart(view, userId)
            }
        }, { item ->
            if (userId <= 0) return@CartAdapter
            viewLifecycleOwner.lifecycleScope.launch {
                cartRepository.removeCartItem(userId, item.id)
                loadCart(view, userId)
            }
        })
        recycler.adapter = adapter

        view.findViewById<Button>(R.id.btnBrowseMenu).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                .selectedItemId = R.id.nav_menu
        }

        view.findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            if (userId <= 0) return@setOnClickListener
            viewLifecycleOwner.lifecycleScope.launch {
                orderRepository.checkoutCart(userId)
                loadCart(view, userId)
            }
        }

        if (userId > 0) {
            loadCart(view, userId)
        }
    }

    private fun loadCart(view: View, userId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = cartRepository.getCart(userId)
            val emptyState = view.findViewById<LinearLayout>(R.id.cartEmptyState)
            val tvSubtotal = view.findViewById<TextView>(R.id.tvSubtotal)

            if (response.success && response.data != null) {
                val cart = response.data
                adapter.updateItems(cart.items)
                tvSubtotal.text = "P${cart.subtotal.toInt()}"
                emptyState.visibility = if (cart.items.isEmpty()) View.VISIBLE else View.GONE
            } else {
                emptyState.visibility = View.VISIBLE
            }
        }
    }
}
