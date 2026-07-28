package com.solux.luxup.taptap.feature.home.template.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OnboardingApi {

    // ---- template-controller ----

    @GET("api/templates")
    suspend fun listTemplates(): Response<BaseResponse<List<TemplateResponseDto>>>

    @GET("api/templates/{template_id}/recommendations")
    suspend fun getTemplatePreview(
        @Path("template_id") templateId: Long,
    ): Response<BaseResponse<TemplatePreviewResponseDto>>

    @POST("api/templates/{template_id}/apply")
    suspend fun applyTemplate(
        @Path("template_id") templateId: Long,
        @Body request: TemplateApplyRequestDto,
    ): Response<BaseResponse<TemplateApplyResponseDto>>

    /** data가 Object(빈 값 가능)라 JsonElement로 받아 apiCallHandler 없이 성공 여부만 확인한다 */
    @POST("api/templates/skip")
    suspend fun skipTemplate(): Response<BaseResponse<JsonElement>>
}