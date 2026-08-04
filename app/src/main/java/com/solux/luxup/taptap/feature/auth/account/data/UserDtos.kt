package com.solux.luxup.taptap.feature.auth.account.data

import kotlinx.serialization.Serializable

/** GET /api/users/profile */
@Serializable
data class UserProfileResponseDto(
    val userId: Long,
    val username: String,
    val profileImageUrl: String? = null,
    val loginType: String? = null,
    val email: String,
)

/** PATCH /api/users/profile — 부분 업데이트. null 필드는 전송되지 않는다. */
@Serializable
data class UpdateUserProfileRequestDto(
    val username: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class UpdateUserProfileResponseDto(
    val userId: Long,
    val username: String,
    val profileImageUrl: String? = null,
)

/** PATCH /api/users/password */
@Serializable
data class ChangePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String,
    val newPasswordConfirm: String,
)