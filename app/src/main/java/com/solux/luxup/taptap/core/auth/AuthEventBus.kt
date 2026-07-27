package com.solux.luxup.taptap.core.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AuthEvent {
    /** 리프레시 토큰까지 만료/무효화되어 재로그인이 필요한 상태 */
    data object SessionExpired : AuthEvent
}

/**
 * TokenAuthenticator 처럼 Compose 밖(백그라운드 스레드)에서 발생하는 인증 이벤트를
 * 화면단(NavHost 등)에 알리기 위한 전역 이벤트 버스.
 */
@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun notifySessionExpired() {
        _events.tryEmit(AuthEvent.SessionExpired)
    }
}