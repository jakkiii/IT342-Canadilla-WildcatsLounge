package edu.cit.canadilla.wildcatslounge.mobile.repository

import edu.cit.canadilla.wildcatslounge.mobile.model.AuthResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.network.RetrofitClient

class AuthRepository {
	suspend fun login(request: LoginRequest) = RetrofitClient.authApiService.login(request)

	suspend fun register(request: RegisterRequest) = RetrofitClient.authApiService.register(request)

	suspend fun health() = RetrofitClient.authApiService.health()
}
