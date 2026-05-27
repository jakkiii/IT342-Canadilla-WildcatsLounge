package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.feature.dashboard.ui.DashboardActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
	private val authRepository = AuthRepository()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val sessionManager = SessionManager(this)
		if (sessionManager.getUser() != null) {
			goToDashboard()
			return
		}

		setContentView(R.layout.activity_login)

		val etIdentifier = findViewById<EditText>(R.id.etIdentifier)
		val etPassword = findViewById<EditText>(R.id.etPassword)
		val tvError = findViewById<TextView>(R.id.tvError)
		val btnLogin = findViewById<Button>(R.id.btnLogin)
		val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

		tvGoRegister.setOnClickListener {
			startActivity(Intent(this, RegisterActivity::class.java))
		}

		btnLogin.setOnClickListener {
			tvError.visibility = View.GONE
			val identifier = etIdentifier.text.toString().trim()
			val password = etPassword.text.toString()

			if (identifier.isBlank() || password.isBlank()) {
				tvError.text = "Email or Student ID and password are required."
				tvError.visibility = View.VISIBLE
				return@setOnClickListener
			}

			btnLogin.isEnabled = false
			lifecycleScope.launch {
				val response = authRepository.login(LoginRequest(identifier, password))
				btnLogin.isEnabled = true
				if (response.success && response.data != null) {
					if (response.data.user.role.equals("staff", ignoreCase = true)) {
						tvError.text = "Staff accounts should sign in on the web admin portal."
						tvError.visibility = View.VISIBLE
						return@launch
					}
					sessionManager.saveSession(
						response.data.user,
						response.data.accessToken,
						response.data.refreshToken
					)
					goToDashboard()
				} else {
					tvError.text = response.error ?: "Login failed."
					tvError.visibility = View.VISIBLE
				}
			}
		}
	}

	private fun goToDashboard() {
		startActivity(Intent(this, DashboardActivity::class.java))
		finish()
	}
}
