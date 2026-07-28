package com.solux.luxup.taptap.feature.home.main.data

import com.solux.luxup.taptap.core.network.BaseResponse
import retrofit2.Response
import retrofit2.http.GET

interface ButtonApi {

    // ---- button-controller ----

    @GET("api/buttons")
    suspend fun getButtons(): Response<BaseResponse<ButtonListResponseDto>>

    // ---- 기록 ----

    @GET("api/records/recent")
    suspend fun getRecentRecord(): Response<BaseResponse<RecordRecentResponseDto>>
}