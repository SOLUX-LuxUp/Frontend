package com.solux.luxup.taptap.feature.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val isOnboardingRequired: Boolean
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val verificationCode: String,
    val password: String,
    val username: String
)

@Serializable
data class RegisterResponseDto(
    val userId: Long,
    val email: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val isOnboardingRequired: Boolean,
    val createdAt: String
)

@Serializable
data class GoogleLoginRequestDto(
    val idToken: String
)

@Serializable
data class GoogleLoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val isNewUser: Boolean,
    val isOnboardingRequired: Boolean
)

@Serializable
data class VerificationCodeRequestDto(
    val email: String
)

@Serializable
data class VerificationCodeResponseDto(
    val expiresIn: Int
)

@Serializable
data class LogoutRequestDto(
    val refreshToken: String
)