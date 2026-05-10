package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.AuthResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
	@POST("auth/register")
	suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

	@POST("auth/login")
	suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

	@GET("auth/health")
	suspend fun health(): ApiResponse<String>
}

