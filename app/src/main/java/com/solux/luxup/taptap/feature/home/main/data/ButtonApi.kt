package com.solux.luxup.taptap.feature.home.main.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ButtonApi {

    // ---- button-controller ----

    @GET("api/buttons")
    suspend fun getButtons(): Response<BaseResponse<ButtonListResponseDto>>

    @POST("api/buttons")
    suspend fun createButton(@Body request: CreateButtonRequestDto): Response<BaseResponse<ButtonResponseDto>>

    @PATCH("api/buttons/{button_id}")
    suspend fun updateButton(
        @Path("button_id") buttonId: Long,
        @Body request: UpdateButtonRequestDto,
    ): Response<BaseResponse<UpdateButtonResponseDto>>

    @DELETE("api/buttons/{button_id}")
    suspend fun deleteButton(@Path("button_id") buttonId: Long): Response<BaseResponse<JsonElement>>

    @PATCH("api/buttons/category-order")
    suspend fun updateCategoryOrder(
        @Body request: CategoryOrderRequestDto,
    ): Response<BaseResponse<List<CategoryOrderItemDto>>>

    @PATCH("api/buttons/{button_id}/favorite")
    suspend fun setFavorite(
        @Path("button_id") buttonId: Long,
        @Body request: FavoriteRequestDto,
    ): Response<BaseResponse<FavoriteResponseDto>>

    @GET("api/buttons/favorites")
    suspend fun getFavoriteButtons(): Response<BaseResponse<List<FavoriteButtonItemDto>>>

    @PATCH("api/buttons/favorite-order")
    suspend fun updateFavoriteOrder(
        @Body request: FavoriteOrderRequestDto,
    ): Response<BaseResponse<List<FavoriteOrderItemDto>>>

    // ---- button-category-controller ----

    @GET("api/buttons/categories")
    suspend fun getCategories(): Response<BaseResponse<List<CategoryListItemDto>>>

    @POST("api/buttons/categories")
    suspend fun createCategory(@Body request: CreateCategoryRequestDto): Response<BaseResponse<CategoryResponseDto>>

    @PATCH("api/buttons/categories/{category_id}")
    suspend fun updateCategoryName(
        @Path("category_id") categoryId: Long,
        @Body request: UpdateCategoryNameRequestDto,
    ): Response<BaseResponse<CategoryUpdateResponseDto>>

    @DELETE("api/buttons/categories/{category_id}")
    suspend fun deleteCategory(
        @Path("category_id") categoryId: Long,
        @Query("delete_buttons") deleteButtons: Boolean,
    ): Response<BaseResponse<JsonElement>>

    // ---- 기록 ----

    @GET("api/records/recent")
    suspend fun getRecentRecord(): Response<BaseResponse<RecordRecentResponseDto>>

    @POST("api/buttons/{button_id}/records")
    suspend fun createRecord(
        @Path("button_id") buttonId: Long,
    ): Response<BaseResponse<RecordCreateResponseDto>>

    @DELETE("api/buttons/{button_id}/records/{record_id}/cancel")
    suspend fun cancelRecord(
        @Path("button_id") buttonId: Long,
        @Path("record_id") recordId: Long,
    ): Response<BaseResponse<JsonElement>>

    @GET("api/buttons/{button_id}/records/latest")
    suspend fun getLatestRecord(
        @Path("button_id") buttonId: Long,
    ): Response<BaseResponse<RecordLatestResponseDto>>

    @GET("api/buttons/{button_id}/records/summary")
    suspend fun getButtonSummary(
        @Path("button_id") buttonId: Long,
    ): Response<BaseResponse<RecordSummaryResponseDto>>

    @GET("api/buttons/{button_id}/records/timeline")
    suspend fun getTimeline(
        @Path("button_id") buttonId: Long,
        @Query("cursor") cursor: Long?,
        @Query("limit") limit: Int,
    ): Response<BaseResponse<RecordTimelineResponseDto>>

    @DELETE("api/buttons/{button_id}/records/{record_id}")
    suspend fun deleteTimelineRecord(
        @Path("button_id") buttonId: Long,
        @Path("record_id") recordId: Long,
    ): Response<BaseResponse<JsonElement>>

    @PATCH("api/buttons/{button_id}/records/{record_id}/detail")
    suspend fun updateRecordDetail(
        @Path("button_id") buttonId: Long,
        @Path("record_id") recordId: Long,
        @Body request: RecordDetailUpdateRequestDto,
    ): Response<BaseResponse<RecordDetailResponseDto>>
}