package edu.cit.canadilla.wildcatslounge.mobile.util

import android.content.Context
import com.google.gson.Gson
import edu.cit.canadilla.wildcatslounge.mobile.model.AuthData

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(authData: AuthData) {
        prefs.edit()
            .putString(KEY_AUTH_DATA, Gson().toJson(authData))
            .putString(KEY_ACCESS_TOKEN, authData.accessToken)
            .putString(KEY_REFRESH_TOKEN, authData.refreshToken)
            .apply()
    }

    fun getAuthData(): AuthData? {
        val json = prefs.getString(KEY_AUTH_DATA, null) ?: return null
        return runCatching { Gson().fromJson(json, AuthData::class.java) }.getOrNull()
    }

    fun isLoggedIn(): Boolean {
        return !prefs.getString(KEY_ACCESS_TOKEN, null).isNullOrBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "wildcats_lounge_session"
        private const val KEY_AUTH_DATA = "auth_data"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
