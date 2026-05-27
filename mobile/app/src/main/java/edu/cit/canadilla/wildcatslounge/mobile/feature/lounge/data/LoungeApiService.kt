package edu.cit.canadilla.wildcatslounge.mobile.feature.lounge.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoungeStatusData
import retrofit2.http.GET

interface LoungeApiService {
	@GET("auth/lounge/status")
	suspend fun getStatus(): ApiResponse<LoungeStatusData>
}
