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
import edu.cit.canadilla.wildcatslounge.mobile.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.repository.AuthRepository
import edu.cit.canadilla.wildcatslounge.mobile.util.InputValidators
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etIdentifier: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button

    private lateinit var sessionManager: SessionManager
    private val authRepository = AuthRepository(RetrofitClient.authApi)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        etIdentifier = findViewById(R.id.etIdentifier)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            submitLogin()
        }
    }

    private fun submitLogin() {
        val identifier = etIdentifier.text.toString().trim()
        val password = etPassword.text.toString().trim()

        when {
            identifier.isBlank() -> showError("Email or Student ID is required")
            identifier.contains("@") && !InputValidators.isValidEmail(identifier) -> showError("Please enter a valid email")
            !InputValidators.isValidPassword(password) -> showError("Password must be at least 6 characters")
            else -> {
                showError(null)
                setLoading(true)
                lifecycleScope.launch {
                    val result = authRepository.login(LoginRequest(identifier, password))
                    setLoading(false)

                    if (result.isSuccess) {
                        val authData = result.getOrNull() ?: return@launch
                        sessionManager.saveSession(authData)
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        showError(result.exceptionOrNull()?.message ?: "Login failed")
                    }
                }
            }
        }
    }

    private fun showError(message: String?) {
        if (message.isNullOrBlank()) {
            tvError.visibility = View.GONE
        } else {
            tvError.text = message
            tvError.visibility = View.VISIBLE
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Signing in..." else "Login"
        etIdentifier.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
    }
}
