package edu.cit.canadilla.wildcatslounge.mobile.core.util

import android.content.Context
import edu.cit.canadilla.wildcatslounge.mobile.core.model.UserData

class SessionManager(context: Context) {
	private val prefs = context.getSharedPreferences("wildcats_session", Context.MODE_PRIVATE)

	fun saveSession(user: UserData, accessToken: String, refreshToken: String) {
		prefs.edit()
			.putLong("user_id", user.id)
			.putString("user_email", user.email)
			.putString("user_firstname", user.firstname)
			.putString("user_lastname", user.lastname)
			.putString("user_student_id", user.studentId)
			.putString("user_role", user.role)
			.putString("access_token", accessToken)
			.putString("refresh_token", refreshToken)
			.apply()
	}

	fun clearSession() {
		prefs.edit().clear().apply()
	}

	fun getUserId(): Long = prefs.getLong("user_id", -1)

	fun getUser(): UserData? {
		val userId = prefs.getLong("user_id", -1)
		if (userId <= 0) return null
		return UserData(
			id = userId,
			email = prefs.getString("user_email", "") ?: "",
			firstname = prefs.getString("user_firstname", "") ?: "",
			lastname = prefs.getString("user_lastname", "") ?: "",
			studentId = prefs.getString("user_student_id", null),
			role = prefs.getString("user_role", null)
		)
	}
}

