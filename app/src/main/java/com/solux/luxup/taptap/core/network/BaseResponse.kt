package com.solux.luxup.taptap.core.network

import kotlinx.serialization.Serializable

/** 서버 공통 응답 포맷. 성공/실패 모두 이 형태로 내려온다 ({ success, message, data }). */
@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

class ApiException(message: String, val statusCode: Int? = null) : Exception(message)