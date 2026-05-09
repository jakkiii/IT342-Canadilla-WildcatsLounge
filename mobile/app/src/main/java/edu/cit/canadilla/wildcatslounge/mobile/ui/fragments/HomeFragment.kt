package edu.cit.canadilla.wildcatslounge.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.repository.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.repository.OrderRepository
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val menuRepository = MenuRepository()
    private val orderRepository = OrderRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        val tvOrderStatus = view.findViewById<TextView>(R.id.tvOrderStatus)
        val tvFeaturedMenu = view.findViewById<TextView>(R.id.tvFeaturedMenu)
        val tvFeaturedMenuDesc = view.findViewById<TextView>(R.id.tvFeaturedMenuDesc)
        val btnStartOrder = view.findViewById<Button>(R.id.btnStartOrder)

        btnStartOrder.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                .selectedItemId = R.id.nav_menu
        }

        if (userId <= 0) {
            tvOrderStatus.text = "Login required"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val orders = orderRepository.getOrders(userId)
            if (orders.success && !orders.data.isNullOrEmpty()) {
                val latest = orders.data.first()
                tvOrderStatus.text = "${latest.orderNumber} · ${latest.status}"
            } else {
                tvOrderStatus.text = "No order yet. Start your first pickup order."
            }

            val menu = menuRepository.getMenuItems()
            if (menu.success && !menu.data.isNullOrEmpty()) {
                val featured = menu.data.first()
                tvFeaturedMenu.text = featured.name
                tvFeaturedMenuDesc.text = featured.description
            } else {
                tvFeaturedMenu.text = "Menu highlights coming soon"
                tvFeaturedMenuDesc.text = ""
            }
        }
    }
}
