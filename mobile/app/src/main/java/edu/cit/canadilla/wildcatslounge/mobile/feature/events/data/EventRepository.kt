package edu.cit.canadilla.wildcatslounge.mobile.feature.events.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class EventRepository {
	suspend fun getEvents() = safeApiCall {
		RetrofitClient.eventApiService.getEvents()
	}

	suspend fun getTodayEvents() = safeApiCall {
		RetrofitClient.eventApiService.getTodayEvents()
	}
}
