package edu.cit.canadilla.wildcatslounge.mobile.feature.auth.data

import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.AuthResponse
import edu.cit.canadilla.wildcatslounge.mobile.core.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.core.model.RegisterVerifyRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
	@POST("auth/login")
	suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

	@POST("auth/register/send-code")
	suspend fun sendRegisterCode(@Body request: RegisterRequest): ApiResponse<String>

	@POST("auth/register/verify")
	suspend fun verifyRegister(@Body request: RegisterVerifyRequest): ApiResponse<AuthResponse>

	@GET("auth/health")
	suspend fun health(): ApiResponse<String>
}
