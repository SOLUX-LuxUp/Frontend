package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    // ---- Notification Setting ----

    @GET("api/users/notification-settings")
    suspend fun getNotificationSettings(): Response<BaseResponse<NotificationSettingResponseDto>>

    @PATCH("api/users/notification-settings")
    suspend fun updateNotificationSettings(
        @Body request: NotificationSettingUpdateRequestDto,
    ): Response<BaseResponse<NotificationSettingResponseDto>>
}