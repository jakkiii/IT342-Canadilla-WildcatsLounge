package edu.cit.canadilla.wildcatslounge.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.repository.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.util.InputValidators
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvStatus: TextView
    private lateinit var btnRegister: Button

    private val authRepository = AuthRepository(RetrofitClient.authApi)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etStudentId = findViewById(R.id.etStudentId)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvStatus = findViewById(R.id.tvStatus)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            submitRegistration()
        }
    }

    private fun submitRegistration() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val studentIdRaw = etStudentId.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        when {
            firstName.length < 2 -> showStatus("First name must be at least 2 characters", isError = true)
            lastName.length < 2 -> showStatus("Last name must be at least 2 characters", isError = true)
            !InputValidators.isValidEmail(email) -> showStatus("Please enter a valid email", isError = true)
            studentIdRaw.isNotBlank() && !InputValidators.isValidStudentId(studentIdRaw) -> {
                showStatus("Student ID format must be ##-####-###", isError = true)
            }
            !InputValidators.isValidPassword(password) -> showStatus("Password must be at least 6 characters", isError = true)
            password != confirmPassword -> showStatus("Passwords do not match", isError = true)
            else -> {
                showStatus(null, isError = false)
                setLoading(true)

                val studentId = studentIdRaw.ifBlank { null }

                lifecycleScope.launch {
                    val result = authRepository.register(
                        RegisterRequest(
                            email = email,
                            password = password,
                            firstname = firstName,
                            lastname = lastName,
                            studentId = studentId
                        )
                    )

                    setLoading(false)

                    if (result.isSuccess) {
                        showStatus("Registration successful! Redirecting to login...", isError = false)
                        etFirstName.text?.clear()
                        etLastName.text?.clear()
                        etEmail.text?.clear()
                        etStudentId.text?.clear()
                        etPassword.text?.clear()
                        etConfirmPassword.text?.clear()

                        tvStatus.postDelayed({
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }, 1200)
                    } else {
                        showStatus(result.exceptionOrNull()?.message ?: "Registration failed", isError = true)
                    }
                }
            }
        }
    }

    private fun showStatus(message: String?, isError: Boolean) {
        if (message.isNullOrBlank()) {
            tvStatus.visibility = View.GONE
            return
        }

        tvStatus.visibility = View.VISIBLE
        tvStatus.text = message
        if (isError) {
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.wildcats_error))
            tvStatus.setBackgroundResource(R.drawable.bg_status_error)
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.wildcats_success))
            tvStatus.setBackgroundResource(R.drawable.bg_status_success)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text = if (isLoading) "Creating account..." else "Create Account"
        etFirstName.isEnabled = !isLoading
        etLastName.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etStudentId.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etConfirmPassword.isEnabled = !isLoading
    }
}
