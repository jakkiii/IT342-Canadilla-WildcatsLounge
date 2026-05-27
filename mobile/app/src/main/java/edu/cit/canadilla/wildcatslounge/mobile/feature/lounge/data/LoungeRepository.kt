package edu.cit.canadilla.wildcatslounge.mobile.feature.lounge.data

import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class LoungeRepository {
	suspend fun getStatus() = safeApiCall {
		RetrofitClient.loungeApiService.getStatus()
	}
}
