package edu.cit.canadilla.wildcatslounge.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.repository.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
	private val authRepository = AuthRepository()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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
			val password = etPassword.text.toString().trim()

			if (identifier.isBlank() || password.isBlank()) {
				tvError.text = "All fields are required."
				tvError.visibility = View.VISIBLE
				return@setOnClickListener
			}

			lifecycleScope.launch {
				val response = authRepository.login(LoginRequest(identifier, password))
				if (response.success && response.data != null) {
					val sessionManager = SessionManager(this@LoginActivity)
					sessionManager.saveSession(
						response.data.user,
						response.data.accessToken,
						response.data.refreshToken
					)
					startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
					finish()
				} else {
					tvError.text = response.error ?: "Login failed."
					tvError.visibility = View.VISIBLE
				}
			}
		}
	}
}
