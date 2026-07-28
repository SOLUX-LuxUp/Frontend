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
}