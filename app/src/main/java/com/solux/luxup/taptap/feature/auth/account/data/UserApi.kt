package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH

interface UserApi {

    // ---- User ----

    @GET("api/users/profile")
    suspend fun getProfile(): Response<BaseResponse<UserProfileResponseDto>>

    @PATCH("api/users/profile")
    suspend fun updateProfile(
        @Body request: UpdateUserProfileRequestDto,
    ): Response<BaseResponse<UpdateUserProfileResponseDto>>

    @PATCH("api/users/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto,
    ): Response<BaseResponse<JsonElement>>

    /**
     * DELETE /api/users/me — 9.4 회원 탈퇴.
     * Retrofit은 DELETE를 "body를 가질 수 없는 메서드"로 취급해 @DELETE + @Body 조합은 요청 생성 시점에
     * IllegalArgumentException("Non-body HTTP method cannot contain @Body")을 던진다 — hasBody로 명시해 우회한다.
     */
    @HTTP(method = "DELETE", path = "api/users/me", hasBody = true)
    suspend fun withdraw(@Body request: WithdrawRequestDto): Response<BaseResponse<JsonElement?>>

    // ---- Notification Setting ----

    @GET("api/users/notification-settings")
    suspend fun getNotificationSettings(): Response<BaseResponse<NotificationSettingResponseDto>>

    @PATCH("api/users/notification-settings")
    suspend fun updateNotificationSettings(
        @Body request: NotificationSettingUpdateRequestDto,
    ): Response<BaseResponse<NotificationSettingResponseDto>>
}