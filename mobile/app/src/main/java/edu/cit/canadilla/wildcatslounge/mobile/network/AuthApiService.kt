package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.AuthResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.RegisterRequest
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
