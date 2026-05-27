package edu.cit.canadilla.wildcatslounge.mobile.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.graphics.drawable.TransitionDrawable
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.ui.LoginActivity

class ProfileFragment : Fragment() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.fragment_profile, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val sessionManager = SessionManager(requireContext())
		val user = sessionManager.getUser() ?: return

		view.findViewById<TextView>(R.id.tvPageTitle).text = "Profile"
		view.findViewById<TextView>(R.id.tvPageSubtitle).text =
			"Your Wildcats Lounge student account"

		val initials = "${user.firstname.firstOrNull() ?: ""}${user.lastname.firstOrNull() ?: ""}"
			.uppercase()
		view.findViewById<TextView>(R.id.tvInitials).text = initials.ifBlank { "?" }
		view.findViewById<TextView>(R.id.tvFullName).text = "${user.firstname} ${user.lastname}"
		view.findViewById<TextView>(R.id.tvEmail).text = user.email

		bindProfileRow(
			view.findViewById(R.id.rowStudentId),
			"Student ID",
			user.studentId ?: "—",
			R.drawable.ic_badge
		)
		bindProfileRow(
			view.findViewById(R.id.rowRole),
			"Account role",
			user.role?.replaceFirstChar { it.uppercase() } ?: "Student",
			R.drawable.ic_activity
		)

		val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)
		btnLogout.setBackgroundResource(R.drawable.bg_logout_normal)

		btnLogout.setOnClickListener {
			btnLogout.isEnabled = false

			val normal = ContextCompat.getDrawable(requireContext(), R.drawable.bg_logout_normal) ?: return@setOnClickListener
			val pressed = ContextCompat.getDrawable(requireContext(), R.drawable.bg_logout_pressed) ?: return@setOnClickListener
			val transition = TransitionDrawable(arrayOf(normal, pressed)).apply {
				isCrossFadeEnabled = true
				// Let the fill shift quickly, but still feel smooth.
				startTransition(180)
			}

			btnLogout.background = transition
			btnLogout.animate().scaleX(0.98f).scaleY(0.98f).setDuration(120).start()

			Handler(requireContext().mainLooper).postDelayed({
				sessionManager.clearSession()
				val intent = Intent(requireContext(), LoginActivity::class.java)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
				startActivity(intent)
			}, 180)
		}
	}

	private fun bindProfileRow(root: View, label: String, value: String, iconRes: Int) {
		root.findViewById<TextView>(R.id.tvRowLabel).text = label
		root.findViewById<TextView>(R.id.tvRowValue).text = value
		root.findViewById<ImageView>(R.id.ivRowIcon).setImageResource(iconRes)
	}
}
