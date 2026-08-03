package com.solux.luxup.taptap.feature.insight.data

import com.solux.luxup.taptap.core.network.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InsightApi {

    // ---- 인사이트 ----

    @GET("api/insights/daily")
    suspend fun getDailyInsight(@Query("date") date: String? = null): Response<BaseResponse<InsightDailyResponseDto>>

    @GET("api/insights/weekly")
    suspend fun getWeeklyInsight(@Query("weekStart") weekStart: String? = null): Response<BaseResponse<InsightWeeklyResponseDto>>

    @GET("api/insights/monthly")
    suspend fun getMonthlyInsight(
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
    ): Response<BaseResponse<InsightMonthlyResponseDto>>
}