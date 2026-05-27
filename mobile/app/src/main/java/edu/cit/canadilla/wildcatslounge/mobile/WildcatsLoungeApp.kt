package edu.cit.canadilla.wildcatslounge.mobile

import android.app.Application
import edu.cit.canadilla.wildcatslounge.mobile.core.network.AuthTokenProvider
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager

class WildcatsLoungeApp : Application() {
	override fun onCreate() {
		super.onCreate()
		AuthTokenProvider.install {
			SessionManager(this).getAccessToken()
		}
	}
}
