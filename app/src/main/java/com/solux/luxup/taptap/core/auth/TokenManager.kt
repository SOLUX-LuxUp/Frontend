package com.solux.luxup.taptap.core.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 액세스/리프레시 토큰 저장소.
 * OkHttp Interceptor/Authenticator(백그라운드 스레드, 동기 호출)에서 바로 읽고 써야 해서
 * DataStore 대신 EncryptedSharedPreferences 를 쓴다.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Synchronized
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    @Synchronized
    fun saveAccessToken(accessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    @Synchronized
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    /**
     * 로그인/회원가입 응답의 isOnboardingRequired를 저장해둔다.
     * 버튼을 전부 삭제해서 지금 0개인 것과, 애초에 온보딩을 마친 적 없는 첫 사용자를 구분하는 데 쓴다
     * ([com.solux.luxup.taptap.feature.home.main.util.presentation.MainHomeViewModel] 참고).
     */
    @Synchronized
    fun saveOnboardingRequired(required: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_REQUIRED, required).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): Long? = prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }

    fun isOnboardingRequired(): Boolean = prefs.getBoolean(KEY_ONBOARDING_REQUIRED, true)

    @Synchronized
    fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_ONBOARDING_REQUIRED)
            .apply()
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    private companion object {
        const val PREFS_FILE_NAME = "auth_secure_prefs"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_ONBOARDING_REQUIRED = "onboarding_required"
    }
}