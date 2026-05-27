package edu.cit.canadilla.wildcatslounge.mobile.core.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.cit.canadilla.wildcatslounge.mobile.core.model.ApiResponse
import retrofit2.HttpException
import java.io.IOException

private val gson = Gson()

suspend fun <T> safeApiCall(block: suspend () -> ApiResponse<T>): ApiResponse<T> {
	return try {
		block()
	} catch (error: HttpException) {
		ApiResponse(success = false, error = parseHttpError(error))
	} catch (_: IOException) {
		ApiResponse(
			success = false,
			error = "Cannot reach the backend. Make sure the Spring Boot server is running."
		)
	} catch (error: Exception) {
		ApiResponse(success = false, error = error.message ?: "Request failed.")
	}
}

private fun parseHttpError(error: HttpException): String {
	val body = error.response()?.errorBody()?.string()
	if (!body.isNullOrBlank()) {
		runCatching {
			val json = gson.fromJson(body, JsonObject::class.java)
			when {
				json.has("error") && !json.get("error").isJsonNull -> json.get("error").asString
				json.has("message") && !json.get("message").isJsonNull -> json.get("message").asString
				else -> null
			}
		}.getOrNull()?.let { return it }
	}

	return error.message ?: "Request failed."
}
