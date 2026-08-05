package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.auth.TokenManager
import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser
import com.solux.luxup.taptap.feature.auth.account.model.NotificationSettings
import com.solux.luxup.taptap.feature.auth.account.model.NotificationSoundOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val apiCallHandler: ApiCallHandler,
    private val tokenManager: TokenManager,
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

    /**
     * DELETE /api/users/me — 회원 탈퇴.
     * 응답 data가 없는(=null) 성공 응답이라 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다. 성공하면 로컬 토큰도 지운다.
     */
    suspend fun withdraw(): Result<Unit> {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return Result.failure(ApiException("로그인 정보가 없어요."))
        val response = userApi.withdraw(WithdrawRequestDto(refreshToken))
        val body = response.body()
        return if (response.isSuccessful && body?.success == true) {
            tokenManager.clearTokens()
            Result.success(Unit)
        } else {
            Result.failure(ApiException(body?.message ?: "계정을 삭제하지 못했어요.", response.code()))
        }
    }

    /** GET /api/users/notification-settings */
    suspend fun getNotificationSettings(): Result<NotificationSettings> =
        apiCallHandler.execute { userApi.getNotificationSettings() }
            .mapCatching { it.toModel() }

    /** PATCH /api/users/notification-settings — 4개 필드를 항상 전부 보낸다 */
    suspend fun updateNotificationSettings(settings: NotificationSettings): Result<NotificationSettings> =
        apiCallHandler.execute { userApi.updateNotificationSettings(settings.toRequestDto()) }
            .mapCatching { it.toModel() }
}

private fun UserProfileResponseDto.toModel() = AccountUser(
    nickname = username,
    email = email,
    profileImageUrl = profileImageUrl,
)

private fun NotificationSettingResponseDto.toModel() = NotificationSettings(
    enabled = masterEnabled,
    soundOption = NotificationSoundOption.fromFlags(soundEnabled, vibrationEnabled),
    showOverOtherApps = popupOverlay,
)

private fun NotificationSettings.toRequestDto() = NotificationSettingUpdateRequestDto(
    masterEnabled = enabled,
    soundEnabled = soundOption.soundEnabled,
    vibrationEnabled = soundOption.vibrationEnabled,
    popupOverlay = showOverOtherApps,
)