package edu.cit.canadilla.wildcatslounge.mobile.feature.events.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.EventData
import retrofit2.http.GET

interface EventApiService {
	@GET("auth/events")
	suspend fun getEvents(): ApiResponse<List<EventData>>

	@GET("auth/events/today")
	suspend fun getTodayEvents(): ApiResponse<List<EventData>>
}
