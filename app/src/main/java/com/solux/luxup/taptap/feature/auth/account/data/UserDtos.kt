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