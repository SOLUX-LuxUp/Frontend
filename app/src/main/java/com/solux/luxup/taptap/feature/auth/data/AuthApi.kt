package com.solux.luxup.taptap.feature.auth.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
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

    // Retrofit은 DELETE를 "body를 가질 수 없는 메서드"로 취급해 @DELETE + @Body 조합은 요청 생성 시점에
    // IllegalArgumentException("Non-body HTTP method cannot contain @Body")을 던진다 — hasBody로 명시해 우회한다.
    @HTTP(method = "DELETE", path = "api/auth/sessions", hasBody = true)
    suspend fun logout(@Body request: LogoutRequestDto): Response<BaseResponse<JsonElement?>>
}