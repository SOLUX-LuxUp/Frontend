package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val apiCallHandler: ApiCallHandler,
) {
    /** GET /api/users/profile — 닉네임(username)·이메일·프로필 이미지 */
    suspend fun getProfile(): Result<AccountUser> =
        apiCallHandler.execute { userApi.getProfile() }
            .mapCatching { it.toModel() }
}

private fun UserProfileResponseDto.toModel() = AccountUser(
    nickname = username,
    email = email,
    profileImageUrl = profileImageUrl,
)