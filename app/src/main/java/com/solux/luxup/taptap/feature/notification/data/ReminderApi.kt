package com.solux.luxup.taptap.feature.notification.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ReminderApi {

    // ---- reminder-controller ----

    @GET("api/reminders")
    suspend fun getReminders(
        @Query("categoryId") categoryId: Long?,
        @Query("search") search: String?,
    ): Response<BaseResponse<List<ReminderListItemDto>>>

    @PUT("api/reminders/{button_id}/detail")
    suspend fun updateDetail(
        @Path("button_id") buttonId: Long,
        @Body request: ReminderDetailRequestDto,
    ): Response<BaseResponse<ReminderDetailResponseDto>>

    @PATCH("api/reminders/{button_id}")
    suspend fun toggle(
        @Path("button_id") buttonId: Long,
        @Body request: ReminderToggleRequestDto,
    ): Response<BaseResponse<ReminderToggleResponseDto>>

    @DELETE("api/reminders/{button_id}")
    suspend fun deleteReminder(@Path("button_id") buttonId: Long): Response<BaseResponse<JsonElement>>
}