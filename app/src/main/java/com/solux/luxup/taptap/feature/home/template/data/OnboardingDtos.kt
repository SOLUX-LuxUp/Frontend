package com.solux.luxup.taptap.feature.home.template.data

import kotlinx.serialization.Serializable

// ---- template-controller ----

/** GET /api/templates */
@Serializable
data class TemplateResponseDto(
    val templateId: Long,
    val templateType: String? = null,
    val templateName: String,
    val description: String? = null,
)

/** GET /api/templates/{template_id}/recommendations */
@Serializable
data class TemplatePreviewResponseDto(
    val templateId: Long,
    val templateName: String,
    val categories: List<TemplateCategoryGroupDto> = emptyList(),
)

@Serializable
data class TemplateCategoryGroupDto(
    val categoryName: String? = null,
    val buttons: List<TemplatePreviewButtonDto> = emptyList(),
)

@Serializable
data class TemplatePreviewButtonDto(
    val presetId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
)

/** POST /api/templates/{template_id}/apply */
@Serializable
data class TemplateApplyRequestDto(
    val selectedPresetIds: List<Long>,
)

@Serializable
data class TemplateApplyResponseDto(
    val templateId: Long,
    val templateName: String? = null,
    val createdCategories: List<CreatedCategoryDto> = emptyList(),
    val createdButtons: List<CreatedButtonDto> = emptyList(),
)

@Serializable
data class CreatedCategoryDto(
    val categoryId: Long,
    val categoryName: String? = null,
)

@Serializable
data class CreatedButtonDto(
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val sourcePresetId: Long? = null,
)