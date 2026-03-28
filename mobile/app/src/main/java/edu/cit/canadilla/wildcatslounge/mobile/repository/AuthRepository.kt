package edu.cit.canadilla.wildcatslounge.mobile.repository

import com.google.gson.Gson
import edu.cit.canadilla.wildcatslounge.mobile.model.ApiResponse
import edu.cit.canadilla.wildcatslounge.mobile.model.AuthData
import edu.cit.canadilla.wildcatslounge.mobile.model.LoginRequest
import edu.cit.canadilla.wildcatslounge.mobile.model.RegisterRequest
import edu.cit.canadilla.wildcatslounge.mobile.network.AuthApiService
import okhttp3.ResponseBody

class AuthRepository(private val api: AuthApiService) {

    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = api.register(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Registration failed"))
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            val response = api.login(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: parseApiError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Login failed"))
        }
    }

    private fun parseApiError(errorBody: ResponseBody?): String {
        return try {
            if (errorBody == null) {
                "Request failed"
            } else {
                val apiError = Gson().fromJson(errorBody.charStream(), ApiResponse::class.java)
                apiError.error as? String ?: "Request failed"
            }
        } catch (_: Exception) {
            "Request failed"
        }
    }
}
