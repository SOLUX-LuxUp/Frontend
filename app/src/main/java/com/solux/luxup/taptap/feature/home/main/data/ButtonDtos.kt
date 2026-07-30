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
    val favoriteOrder: Int? = null,
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
    val expiryEnabled: Boolean = false,
    val expiredAt: String? = null,
    val lastRecordedAt: String? = null,
)

/** POST /api/buttons */
@Serializable
data class CreateButtonRequestDto(
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val expiryEnabled: Boolean? = null,
    val expiredAt: String? = null,
)

@Serializable
data class ButtonResponseDto(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val expiryEnabled: Boolean = false,
    val expiredAt: String? = null,
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val lastRecordedAt: String? = null,
    val createdAt: String? = null,
)

/** PATCH /api/buttons/{button_id} */
@Serializable
data class UpdateButtonRequestDto(
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val clearCategory: Boolean? = null,
    val expiryEnabled: Boolean? = null,
    val expiredAt: String? = null,
)

@Serializable
data class UpdateButtonResponseDto(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val expiryEnabled: Boolean = false,
    val expiredAt: String? = null,
    val updatedAt: String? = null,
)

/** PATCH /api/buttons/category-order — categoryIds를 원하는 순서 그대로 담아 보낸다 */
@Serializable
data class CategoryOrderRequestDto(
    val categoryIds: List<Long>,
)

@Serializable
data class CategoryOrderItemDto(
    val categoryId: Long,
    val displayOrder: Int,
)

/** PATCH /api/buttons/{button_id}/favorite */
@Serializable
data class FavoriteRequestDto(
    val isFavorite: Boolean,
)

@Serializable
data class FavoriteResponseDto(
    val isFavorite: Boolean = false,
)

/** PATCH /api/buttons/favorite-order — buttonIds를 원하는 순서 그대로 담아 보낸다 */
@Serializable
data class FavoriteOrderRequestDto(
    val buttonIds: List<Long>,
)

@Serializable
data class FavoriteOrderItemDto(
    val buttonId: Long,
    val favoriteOrder: Int,
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

/** POST /api/buttons/{button_id}/records */
@Serializable
data class RecordCreateResponseDto(
    val recordId: Long,
    val buttonId: Long,
    val recordedAt: String? = null,
)

/** GET /api/buttons/{button_id}/records/latest */
@Serializable
data class RecordLatestResponseDto(
    val buttonId: Long,
    val lastRecordedAt: String? = null,
    val elapsedSeconds: Long? = null,
)

/** GET /api/buttons/{button_id}/records/summary — 5.2 최근 기록 조회 (버튼 상세) */
@Serializable
data class RecordSummaryResponseDto(
    val buttonId: Long,
    val lastRecordedAt: String? = null,
    val todayCount: Long = 0,
    val totalCount: Long = 0,
)

/** GET /api/buttons/{button_id}/records/timeline — 5.3 타임라인 조회 */
@Serializable
data class RecordTimelineResponseDto(
    val records: List<RecordTimelineItemDto> = emptyList(),
    val nextCursor: Long? = null,
    val hasMore: Boolean = false,
)

@Serializable
data class RecordTimelineItemDto(
    val recordId: Long,
    val recordedAt: String? = null,
    val memo: String? = null,
    val emoji: String? = null,
)

/** PATCH /api/buttons/{button_id}/records/{record_id}/detail — 5.4 타임라인 상세 기록 추가 (메모·이모지) */
@Serializable
data class RecordDetailUpdateRequestDto(
    val memo: String?,
    val emoji: String?,
)

@Serializable
data class RecordDetailResponseDto(
    val recordId: Long,
    val memo: String? = null,
    val emoji: String? = null,
    val recordedAt: String? = null,
)