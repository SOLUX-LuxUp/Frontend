package com.solux.luxup.taptap.feature.home.template.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.feature.home.template.model.OnboardingTemplate
import com.solux.luxup.taptap.feature.home.template.model.TemplateButtonSuggestion
import com.solux.luxup.taptap.feature.home.template.model.TemplatePreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingTemplateRepository @Inject constructor(
    private val onboardingApi: OnboardingApi,
    private val apiCallHandler: ApiCallHandler,
) {
    /** GET /api/templates — 온보딩 카드 목록 */
    suspend fun listTemplates(): Result<List<OnboardingTemplate>> =
        apiCallHandler.execute { onboardingApi.listTemplates() }
            .mapCatching { list -> list.map { it.toModel() } }

    /** GET /api/templates/{template_id}/recommendations — 카테고리별 추천 버튼 미리보기 */
    suspend fun getTemplatePreview(templateId: Long): Result<TemplatePreview> =
        apiCallHandler.execute { onboardingApi.getTemplatePreview(templateId) }
            .mapCatching { it.toModel() }

    /**
     * 온보딩을 건너뛴 경우 특정 템플릿이 없으므로, 모든 템플릿의 추천 버튼을 모아
     * "첫 번째 버튼을 만들어보세요" 섹션에 전체 목록으로 보여준다.
     *
     * 템플릿 순서를 매번 랜덤으로 섞어서, 볼 때마다 다른 템플릿이 먼저 보이게 한다.
     */
    suspend fun getAllTemplateSuggestions(): Result<List<TemplateButtonSuggestion>> =
        listTemplates().mapCatching { templates ->
            coroutineScope {
                templates
                    .shuffled()
                    .map { template -> async { getTemplatePreview(template.templateId).getOrNull()?.suggestions.orEmpty() } }
                    .awaitAll()
                    .flatten()
            }
        }

    /** POST /api/templates/{template_id}/apply — 선택한 프리셋으로 카테고리·버튼 생성 */
    suspend fun applyTemplate(templateId: Long, presetIds: List<Long>): Result<TemplateApplyResponseDto> =
        apiCallHandler.execute {
            onboardingApi.applyTemplate(templateId, TemplateApplyRequestDto(presetIds))
        }

    /**
     * POST /api/templates/skip
     * 응답 data가 빈 Object라 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun skipTemplate(): Result<Unit> = runCatching {
        val response = onboardingApi.skipTemplate()
        val body = response.body()
        if (response.isSuccessful && body?.success == true) {
            Unit
        } else {
            throw ApiException(body?.message ?: "템플릿을 건너뛰지 못했어요.")
        }
    }
}

private fun TemplateResponseDto.toModel() = OnboardingTemplate(
    templateId = templateId,
    templateType = templateType.orEmpty(),
    templateName = templateName,
    description = description.orEmpty(),
)

private fun TemplatePreviewResponseDto.toModel() = TemplatePreview(
    templateId = templateId,
    templateName = templateName,
    suggestions = categories.flatMap { group ->
        group.buttons.map { button ->
            TemplateButtonSuggestion(
                presetId = button.presetId,
                templateId = templateId,
                buttonName = button.buttonName,
                iconName = button.iconName,
                iconColor = button.iconColor,
                categoryName = group.categoryName.orEmpty(),
            )
        }
    },
)