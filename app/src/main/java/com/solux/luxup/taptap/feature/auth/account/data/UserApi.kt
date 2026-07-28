package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.core.network.BaseResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {

    // ---- User ----

    @GET("api/users/profile")
    suspend fun getProfile(): Response<BaseResponse<UserProfileResponseDto>>
}