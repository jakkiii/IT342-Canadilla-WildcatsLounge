package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterVerifyRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient
import edu.cit.canadilla.wildcatslounge.mobile.core.network.safeApiCall

class AuthRepository {
	suspend fun login(request: LoginRequest) = safeApiCall {
		RetrofitClient.authApiService.login(request)
	}

	suspend fun sendRegisterCode(request: RegisterRequest) = safeApiCall {
		RetrofitClient.authApiService.sendRegisterCode(request)
	}

	suspend fun verifyRegister(request: RegisterVerifyRequest) = safeApiCall {
		RetrofitClient.authApiService.verifyRegister(request)
	}

	suspend fun health() = safeApiCall {
		RetrofitClient.authApiService.health()
	}
}
