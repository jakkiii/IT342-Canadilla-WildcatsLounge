package edu.cit.canadilla.wildcatslounge.mobile.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val studentId: String?
)

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class UserData(
    val email: String,
    val firstname: String,
    val lastname: String,
    val studentId: String?,
    val role: String?
)

data class AuthData(
    val user: UserData,
    val accessToken: String,
    val refreshToken: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?,
    val timestamp: String?
)
