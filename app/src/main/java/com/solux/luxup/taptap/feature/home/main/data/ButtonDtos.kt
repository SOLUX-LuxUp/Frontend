package com.solux.luxup.taptap.feature.home.main.data

import kotlinx.serialization.Serializable

// ---- button-controller ----

/** GET /api/buttons */
@Serializable
data class ButtonListResponseDto(
    val favorites: List<FavoriteButtonItemDto> = emptyList(),
    val categories: List<CategoryGroupDto> = emptyList(),
)

@Serializable
data class FavoriteButtonItemDto(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val isFavorite: Boolean = false,
    val lastRecordedAt: String? = null,
)

@Serializable
data class CategoryGroupDto(
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val buttons: List<CategoryButtonItemDto> = emptyList(),
)

@Serializable
data class CategoryButtonItemDto(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val isFavorite: Boolean = false,
    val lastRecordedAt: String? = null,
)

// ---- button-category-controller ----

/** GET /api/buttons/categories — displayOrder 오름차순으로 정렬되어 내려온다 */
@Serializable
data class CategoryListItemDto(
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int? = null,
)

/** POST /api/buttons/categories */
@Serializable
data class CreateCategoryRequestDto(
    val categoryName: String,
)

@Serializable
data class CategoryResponseDto(
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int? = null,
    val createdAt: String? = null,
)

/** PATCH /api/buttons/categories/{category_id} */
@Serializable
data class UpdateCategoryNameRequestDto(
    val categoryName: String,
)

@Serializable
data class CategoryUpdateResponseDto(
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int? = null,
    val updatedAt: String? = null,
)

// ---- 기록 ----

/** GET /api/records/recent */
@Serializable
data class RecordRecentResponseDto(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val lastRecordedAt: String? = null,
)