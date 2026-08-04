package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
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

    /** PATCH /api/users/profile — 부분 업데이트. 이메일은 응답에 없으므로 호출부가 기존 값을 유지한다 */
    suspend fun updateProfile(username: String? = null, profileImageUrl: String? = null): Result<UpdateUserProfileResponseDto> =
        apiCallHandler.execute {
            userApi.updateProfile(UpdateUserProfileRequestDto(username = username, profileImageUrl = profileImageUrl))
        }

    /**
     * PATCH /api/users/password
     *
     * 성공해도 data 가 없는(= null) 응답이라 [ApiCallHandler.execute] 의 "data != null이면 성공"
     * 판정을 그대로 쓰면 성공 케이스가 실패로 잘못 분류된다. success 플래그만으로 직접 판정한다.
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        newPasswordConfirm: String,
    ): Result<Unit> {
        val response = userApi.changePassword(
            ChangePasswordRequestDto(currentPassword, newPassword, newPasswordConfirm)
        )
        val body = response.body()
        return if (response.isSuccessful && body?.success == true) {
            Result.success(Unit)
        } else {
            Result.failure(ApiException(body?.message ?: "비밀번호를 변경하지 못했어요.", response.code()))
        }
    }
}

private fun UserProfileResponseDto.toModel() = AccountUser(
    nickname = username,
    email = email,
    profileImageUrl = profileImageUrl,
)