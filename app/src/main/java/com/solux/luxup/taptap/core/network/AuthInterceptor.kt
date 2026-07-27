package com.solux.luxup.taptap.core.network

import com.solux.luxup.taptap.core.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** 저장된 액세스 토큰이 있으면 모든 요청에 Authorization 헤더를 자동으로 붙인다. */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val request = if (!accessToken.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}