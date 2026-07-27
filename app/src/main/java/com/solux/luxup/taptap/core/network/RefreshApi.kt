package com.solux.luxup.taptap.core.network

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class TokenRefreshRequestDto(val refreshToken: String)

@Serializable
data class TokenRefreshResponseDto(val accessToken: String)

/**
 * TokenAuthenticator 전용 리프레시 API. Authenticator 는 코루틴이 아닌 OkHttp 백그라운드
 * 스레드에서 동기로 호출되므로 suspend 가 아닌 Call<T> 로 선언한다.
 * (feature/auth 의 AuthApi 와는 별개로, 인증 인터셉터가 없는 "plain" 클라이언트에 물린다 —
 * 안 그러면 401 응답이 다시 이 Authenticator 를 재귀적으로 태우게 된다)
 */
interface RefreshApi {
    @POST("api/auth/tokens/refresh")
    fun refreshToken(@Body request: TokenRefreshRequestDto): Call<BaseResponse<TokenRefreshResponseDto>>
}