package edu.cit.canadilla.wildcatslounge.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.util.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)
        val authData = sessionManager.getAuthData()

        if (authData == null) {
            redirectToLogin()
            return
        }

        findViewById<TextView>(R.id.tvName).text = "Name: ${authData.user.firstname} ${authData.user.lastname}"
        findViewById<TextView>(R.id.tvEmail).text = "Email: ${authData.user.email}"

        val studentIdText = authData.user.studentId?.takeIf { it.isNotBlank() } ?: "Not provided"
        findViewById<TextView>(R.id.tvStudentId).text = "Student ID: $studentIdText"

        val roleText = authData.user.role?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown"
        findViewById<TextView>(R.id.tvRole).text = "Role: $roleText"

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sessionManager.clearSession()
            openLogoutSuccessScreen()
        }
    }

    private fun openLogoutSuccessScreen() {
        startActivity(Intent(this, LogoutSuccessActivity::class.java))
        finishAffinity()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
