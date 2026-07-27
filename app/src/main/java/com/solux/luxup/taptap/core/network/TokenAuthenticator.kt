package com.solux.luxup.taptap.core.network

import com.solux.luxup.taptap.core.auth.AuthEventBus
import com.solux.luxup.taptap.core.auth.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

/**
 * 401 응답을 받았을 때만 OkHttp 가 자동으로 호출하는 Authenticator.
 * 리프레시 토큰으로 액세스 토큰을 재발급받아 원래 요청을 재시도하고,
 * 리프레시 토큰까지 무효면 로그아웃 처리 후 재로그인을 요청한다.
 */
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val refreshApiProvider: Provider<RefreshApi>,
    private val authEventBus: AuthEventBus
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 재발급 후 재시도에서도 계속 401이 나면(재발급 토큰도 무효 등) 무한 루프 방지
        if (responseCount(response) >= MAX_RETRY_COUNT) {
            return null
        }

        val refreshToken = tokenManager.getRefreshToken() ?: return null
        val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        val newAccessToken = synchronized(this) {
            val currentAccessToken = tokenManager.getAccessToken()
            if (!currentAccessToken.isNullOrBlank() && currentAccessToken != failedAccessToken) {
                // 다른 요청이 대기 중에 이미 재발급을 끝낸 경우
                currentAccessToken
            } else {
                requestNewAccessToken(refreshToken)
            }
        }

        if (newAccessToken == null) {
            tokenManager.clearTokens()
            authEventBus.notifySessionExpired()
            return null
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun requestNewAccessToken(refreshToken: String): String? = runCatching {
        val result = refreshApiProvider.get()
            .refreshToken(TokenRefreshRequestDto(refreshToken))
            .execute()
        result.body()?.takeIf { it.success }?.data?.accessToken
    }.getOrNull()?.also { tokenManager.saveAccessToken(it) }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_RETRY_COUNT = 3
    }
}