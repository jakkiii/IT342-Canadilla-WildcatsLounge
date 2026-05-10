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
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.core.util.InputValidators
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
	private val authRepository = AuthRepository()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_register)

		val etFirstName = findViewById<EditText>(R.id.etFirstName)
		val etLastName = findViewById<EditText>(R.id.etLastName)
		val etEmail = findViewById<EditText>(R.id.etEmail)
		val etStudentId = findViewById<EditText>(R.id.etStudentId)
		val etPassword = findViewById<EditText>(R.id.etPassword)
		val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
		val tvStatus = findViewById<TextView>(R.id.tvStatus)
		val btnRegister = findViewById<Button>(R.id.btnRegister)
		val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)

		tvGoLogin.setOnClickListener {
			startActivity(Intent(this, LoginActivity::class.java))
			finish()
		}

		btnRegister.setOnClickListener {
			tvStatus.visibility = View.GONE

			val firstName = etFirstName.text.toString().trim()
			val lastName = etLastName.text.toString().trim()
			val email = etEmail.text.toString().trim()
			val studentId = etStudentId.text.toString().trim()
			val password = etPassword.text.toString().trim()
			val confirmPassword = etConfirmPassword.text.toString().trim()

			if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
				tvStatus.text = "All required fields must be filled."
				tvStatus.visibility = View.VISIBLE
				return@setOnClickListener
			}

			if (!InputValidators.isValidEmail(email)) {
				tvStatus.text = "Enter a valid email."
				tvStatus.visibility = View.VISIBLE
				return@setOnClickListener
			}

			if (studentId.isNotEmpty() && !InputValidators.isValidStudentId(studentId)) {
				tvStatus.text = "Student ID must be in format ##-####-###."
				tvStatus.visibility = View.VISIBLE
				return@setOnClickListener
			}

			if (password.length < 6) {
				tvStatus.text = "Password must be at least 6 characters."
				tvStatus.visibility = View.VISIBLE
				return@setOnClickListener
			}

			if (password != confirmPassword) {
				tvStatus.text = "Passwords do not match."
				tvStatus.visibility = View.VISIBLE
				return@setOnClickListener
			}

			lifecycleScope.launch {
				val response = authRepository.register(
					RegisterRequest(email, password, firstName, lastName, studentId.ifBlank { null })
				)

				if (response.success) {
					tvStatus.text = "Registration successful. Redirecting to login..."
					tvStatus.visibility = View.VISIBLE
					tvStatus.setTextColor(getColor(R.color.wildcats_success))
					tvStatus.postDelayed({
						startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
						finish()
					}, 1500)
				} else {
					tvStatus.text = response.error ?: "Registration failed."
					tvStatus.visibility = View.VISIBLE
					tvStatus.setTextColor(getColor(R.color.wildcats_error))
				}
			}
		}
	}
}

