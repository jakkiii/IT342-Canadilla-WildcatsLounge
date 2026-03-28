package edu.cit.canadilla.wildcatslounge.mobile.util

import android.util.Patterns

object InputValidators {

    private val studentIdRegex = Regex("^\\d{2}-\\d{4}-\\d{3}$")

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidStudentId(studentId: String): Boolean {
        return studentIdRegex.matches(studentId)
    }
}
