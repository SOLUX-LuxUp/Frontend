package com.solux.luxup.taptap.core.network

import javax.inject.Qualifier

/** Authorization 헤더 자동 첨부 + 401 재발급까지 처리하는 클라이언트 (일반 API 호출용) */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

/** 인증 관련 인터셉터가 전혀 없는 순수 클라이언트 (토큰 재발급 요청 전용) */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlainClient