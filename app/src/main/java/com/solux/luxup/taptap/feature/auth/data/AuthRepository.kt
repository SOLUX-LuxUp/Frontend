package com.solux.luxup.taptap.feature.auth.data

import com.solux.luxup.taptap.core.auth.TokenManager
import com.solux.luxup.taptap.core.network.ApiCallHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val apiCallHandler: ApiCallHandler,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<LoginResponseDto> =
        apiCallHandler.execute { authApi.login(LoginRequestDto(email, password)) }
            .onSuccess {
                tokenManager.saveTokens(it.accessToken, it.refreshToken)
                tokenManager.saveUserId(it.userId)
                tokenManager.saveOnboardingRequired(it.isOnboardingRequired)
            }

    suspend fun register(
        email: String,
        verificationCode: String,
        password: String,
        username: String
    ): Result<RegisterResponseDto> =
        apiCallHandler.execute {
            authApi.register(RegisterRequestDto(email, verificationCode, password, username))
        }.onSuccess {
            tokenManager.saveTokens(it.accessToken, it.refreshToken)
            tokenManager.saveUserId(it.userId)
            tokenManager.saveOnboardingRequired(it.isOnboardingRequired)
        }

    suspend fun googleLogin(idToken: String): Result<GoogleLoginResponseDto> =
        apiCallHandler.execute { authApi.googleLogin(GoogleLoginRequestDto(idToken)) }
            .onSuccess {
                tokenManager.saveTokens(it.accessToken, it.refreshToken)
                tokenManager.saveUserId(it.userId)
                tokenManager.saveOnboardingRequired(it.isOnboardingRequired)
            }

    suspend fun sendVerificationCode(email: String): Result<VerificationCodeResponseDto> =
        apiCallHandler.execute { authApi.sendVerificationCode(VerificationCodeRequestDto(email)) }

    /** 서버 호출 성공 여부와 무관하게 로컬 토큰은 항상 지운다 (best-effort 로그아웃). */
    suspend fun logout() {
        tokenManager.getRefreshToken()?.let { refreshToken ->
            runCatching { authApi.logout(LogoutRequestDto(refreshToken)) }
        }
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
}
