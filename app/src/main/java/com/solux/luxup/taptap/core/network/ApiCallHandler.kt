package com.solux.luxup.taptap.core.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BaseResponse<T> 로 감싸인 Retrofit 호출을 공통으로 처리한다.
 * 실패 시(4xx/5xx 또는 success=false) 서버가 내려준 message 를 Result.failure 로 전달한다.
 */
@Singleton
class ApiCallHandler @Inject constructor(
    private val json: Json
) {
    suspend fun <T> execute(block: suspend () -> Response<BaseResponse<T>>): Result<T> {
        return try {
            val response = block()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                val message = body?.message
                    ?: response.errorBody()?.string()?.let(::parseErrorMessage)
                    ?: "알 수 없는 오류가 발생했습니다."
                Result.failure(ApiException(message, response.code()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(raw: String): String? = runCatching {
        json.decodeFromString<BaseResponse<JsonElement>>(raw).message
    }.getOrNull()
}