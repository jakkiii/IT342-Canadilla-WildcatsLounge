package edu.cit.canadilla.wildcatslounge.mobile.network

import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.AuthData
import edu.cit.canadilla.wildcatslounge.mobile.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>
}
