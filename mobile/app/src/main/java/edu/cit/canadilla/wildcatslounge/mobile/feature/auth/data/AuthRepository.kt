package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.AuthResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.network.RetrofitClient

class AuthRepository {
	suspend fun login(request: LoginRequest) = RetrofitClient.authApiService.login(request)

	suspend fun register(request: RegisterRequest) = RetrofitClient.authApiService.register(request)

	suspend fun health() = RetrofitClient.authApiService.health()
}

