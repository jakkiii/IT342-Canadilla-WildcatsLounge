package edu.cit.canadilla.wildcatslounge.mobile.feature.dashboard.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.feature.cart.ui.CartFragment
import edu.cit.canadilla.wildcatslounge.mobile.feature.events.ui.EventsFragment
import edu.cit.canadilla.wildcatslounge.mobile.feature.home.ui.HomeFragment
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.ui.MenuFragment
import edu.cit.canadilla.wildcatslounge.mobile.feature.profile.ui.ProfileFragment
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager

class DashboardActivity : AppCompatActivity() {
	private lateinit var sessionManager: SessionManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_dashboard)

		sessionManager = SessionManager(this)
		val user = sessionManager.getUser()

		val greetingView = findViewById<TextView>(R.id.tvGreeting)
		greetingView.text = "Good day, ${user?.firstname ?: "Guest"}"

		val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
		bottomNav.setOnItemSelectedListener { item ->
			when (item.itemId) {
				R.id.nav_home -> switchFragment(HomeFragment())
				R.id.nav_menu -> switchFragment(MenuFragment())
				R.id.nav_cart -> switchFragment(CartFragment())
				R.id.nav_events -> switchFragment(EventsFragment())
				R.id.nav_profile -> switchFragment(ProfileFragment())
			}
			true
		}

		if (savedInstanceState == null) {
			bottomNav.selectedItemId = R.id.nav_home
		}
	}

	private fun switchFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragmentContainer, fragment)
			.commit()
	}
}

