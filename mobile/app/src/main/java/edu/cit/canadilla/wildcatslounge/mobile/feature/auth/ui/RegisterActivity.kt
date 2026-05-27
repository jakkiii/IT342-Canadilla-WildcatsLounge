package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterVerifyRequest
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.core.util.InputValidators
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import edu.cit.canadilla.wildcatslounge.mobile.feature.dashboard.ui.DashboardActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
	private val authRepository = AuthRepository()
	private var codeSent = false
	private var cooldownTimer: CountDownTimer? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_register)

		val etFirstName = findViewById<EditText>(R.id.etFirstName)
		val etLastName = findViewById<EditText>(R.id.etLastName)
		val etEmail = findViewById<EditText>(R.id.etEmail)
		val etStudentId = findViewById<EditText>(R.id.etStudentId)
		val etPassword = findViewById<EditText>(R.id.etPassword)
		val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
		val etCode1 = findViewById<EditText>(R.id.etCode1)
		val etCode2 = findViewById<EditText>(R.id.etCode2)
		val etCode3 = findViewById<EditText>(R.id.etCode3)
		val etCode4 = findViewById<EditText>(R.id.etCode4)
		val etCode5 = findViewById<EditText>(R.id.etCode5)
		val etCode6 = findViewById<EditText>(R.id.etCode6)
		val codeInputs = listOf(etCode1, etCode2, etCode3, etCode4, etCode5, etCode6)

		val tvStatus = findViewById<TextView>(R.id.tvStatus)
		val verificationSection = findViewById<LinearLayout>(R.id.verificationSection)
		val btnSendCode = findViewById<Button>(R.id.btnSendCode)
		val btnVerify = findViewById<Button>(R.id.btnVerify)
		val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)

		fun resetCodeState() {
			codeSent = false
			cooldownTimer?.cancel()
			btnSendCode.isEnabled = true
			btnSendCode.text = "Send code"
			verificationSection.visibility = View.GONE
			codeInputs.forEach { it.text?.clear() }
		}

		fun showStatus(message: String, isError: Boolean) {
			tvStatus.text = message
			tvStatus.visibility = View.VISIBLE
			tvStatus.setTextColor(getColor(if (isError) R.color.wildcats_error else R.color.wildcats_success))
			tvStatus.setBackgroundResource(if (isError) R.drawable.bg_status_error else R.drawable.bg_status_success)
		}

		listOf(etFirstName, etLastName, etEmail, etStudentId, etPassword, etConfirmPassword).forEach { field ->
			field.doAfterTextChanged {
				if (codeSent) resetCodeState()
				tvStatus.visibility = View.GONE
			}
		}

		setupCodeInputFocus(codeInputs)

		tvGoLogin.setOnClickListener {
			startActivity(Intent(this, LoginActivity::class.java))
			finish()
		}

		btnSendCode.setOnClickListener {
			tvStatus.visibility = View.GONE
			val validation = validateForm(
				etFirstName.text.toString().trim(),
				etLastName.text.toString().trim(),
				etEmail.text.toString().trim(),
				etStudentId.text.toString().trim(),
				etPassword.text.toString(),
				etConfirmPassword.text.toString(),
				requireCode = false
			)
			if (validation != null) {
				showStatus(validation, true)
				return@setOnClickListener
			}

			btnSendCode.isEnabled = false
			lifecycleScope.launch {
				val request = buildRegisterRequest(
					etFirstName, etLastName, etEmail, etStudentId, etPassword
				)
				val response = authRepository.sendRegisterCode(request)
				if (response.success) {
					codeSent = true
					verificationSection.visibility = View.VISIBLE
					showStatus("Verification code sent. Check your email.", false)
					startResendCooldown(btnSendCode)
					etCode1.requestFocus()
				} else {
					btnSendCode.isEnabled = true
					showStatus(response.error ?: "Could not send verification code.", true)
				}
			}
		}

		btnVerify.setOnClickListener {
			tvStatus.visibility = View.GONE
			if (!codeSent) {
				showStatus("Send a verification code first.", true)
				return@setOnClickListener
			}

			val code = codeInputs.joinToString("") { it.text.toString().trim() }
			val validation = validateForm(
				etFirstName.text.toString().trim(),
				etLastName.text.toString().trim(),
				etEmail.text.toString().trim(),
				etStudentId.text.toString().trim(),
				etPassword.text.toString(),
				etConfirmPassword.text.toString(),
				requireCode = true,
				verificationCode = code
			)
			if (validation != null) {
				showStatus(validation, true)
				return@setOnClickListener
			}

			btnVerify.isEnabled = false
			lifecycleScope.launch {
				val request = RegisterVerifyRequest(
					email = etEmail.text.toString().trim(),
					password = etPassword.text.toString(),
					firstname = etFirstName.text.toString().trim(),
					lastname = etLastName.text.toString().trim(),
					studentId = etStudentId.text.toString().trim(),
					verificationCode = code
				)
				val response = authRepository.verifyRegister(request)
				btnVerify.isEnabled = true
				if (response.success && response.data != null) {
					if (response.data.user.role.equals("staff", ignoreCase = true)) {
						showStatus("Staff accounts should use the web admin portal.", true)
						return@launch
					}
					SessionManager(this@RegisterActivity).saveSession(
						response.data.user,
						response.data.accessToken,
						response.data.refreshToken
					)
					showStatus("Registration successful! Redirecting...", false)
					findViewById<View>(android.R.id.content).postDelayed({
						startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
						finish()
					}, 1200)
				} else {
					showStatus(response.error ?: "Registration failed.", true)
				}
			}
		}
	}

	override fun onDestroy() {
		cooldownTimer?.cancel()
		super.onDestroy()
	}

	private fun startResendCooldown(button: Button) {
		cooldownTimer?.cancel()
		cooldownTimer = object : CountDownTimer(60_000, 1_000) {
			override fun onTick(millisUntilFinished: Long) {
				val seconds = (millisUntilFinished / 1000).toInt()
				button.text = "Resend in ${seconds}s"
			}

			override fun onFinish() {
				button.isEnabled = true
				button.text = if (codeSent) "Resend code" else "Send code"
			}
		}.start()
	}

	private fun setupCodeInputFocus(inputs: List<EditText>) {
		inputs.forEachIndexed { index, field ->
			field.doAfterTextChanged { text ->
				if ((text?.length ?: 0) == 1 && index < inputs.lastIndex) {
					inputs[index + 1].requestFocus()
				}
			}
		}
	}

	private fun buildRegisterRequest(
		etFirstName: EditText,
		etLastName: EditText,
		etEmail: EditText,
		etStudentId: EditText,
		etPassword: EditText
	) = RegisterRequest(
		email = etEmail.text.toString().trim(),
		password = etPassword.text.toString(),
		firstname = etFirstName.text.toString().trim(),
		lastname = etLastName.text.toString().trim(),
		studentId = etStudentId.text.toString().trim()
	)

	private fun validateForm(
		firstName: String,
		lastName: String,
		email: String,
		studentId: String,
		password: String,
		confirmPassword: String,
		requireCode: Boolean,
		verificationCode: String = ""
	): String? {
		if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
			studentId.isBlank() || password.isBlank()
		) {
			return "First name, last name, email, student ID and password are required."
		}
		if (!InputValidators.isValidEmail(email)) {
			return "Enter a valid email."
		}
		if (!InputValidators.isValidStudentId(studentId)) {
			return "Student ID must be in format ##-####-###."
		}
		if (password.length < 6) {
			return "Password must be at least 6 characters."
		}
		if (password != confirmPassword) {
			return "Passwords do not match."
		}
		if (requireCode && !Regex("^\\d{6}$").matches(verificationCode)) {
			return "Enter the 6-digit verification code sent to your email."
		}
		return null
	}
}
