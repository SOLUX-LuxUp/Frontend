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