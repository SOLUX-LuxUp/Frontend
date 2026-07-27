package com.solux.luxup.taptap.feature.auth.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<BaseResponse<RegisterResponseDto>>

    @POST("api/auth/sessions")
    suspend fun login(@Body request: LoginRequestDto): Response<BaseResponse<LoginResponseDto>>

    @POST("api/auth/google/sessions")
    suspend fun googleLogin(@Body request: GoogleLoginRequestDto): Response<BaseResponse<GoogleLoginResponseDto>>

    @POST("api/auth/email/verification-code")
    suspend fun sendVerificationCode(@Body request: VerificationCodeRequestDto): Response<BaseResponse<VerificationCodeResponseDto>>

    @DELETE("api/auth/sessions")
    suspend fun logout(@Body request: LogoutRequestDto): Response<BaseResponse<JsonElement?>>
}