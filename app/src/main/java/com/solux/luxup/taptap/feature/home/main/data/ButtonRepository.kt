package com.solux.luxup.taptap.feature.home.main.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.feature.home.main.model.RecentRecord
import javax.inject.Inject
import javax.inject.Singleton

data class ButtonListResult(
    val favorites: List<FavoriteButton>,
    val habitButtons: List<HabitButton>,
)

@Singleton
class ButtonRepository @Inject constructor(
    private val buttonApi: ButtonApi,
    private val apiCallHandler: ApiCallHandler,
) {
    /** GET /api/buttons — 즐겨찾기 + 카테고리별 습관 버튼 */
    suspend fun getButtons(): Result<ButtonListResult> =
        apiCallHandler.execute { buttonApi.getButtons() }
            .mapCatching { it.toModel() }

    /**
     * GET /api/records/recent
     * 기록이 하나도 없으면 data가 null로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun getRecentRecord(): Result<RecentRecord?> = runCatching {
        val response = buttonApi.getRecentRecord()
        val body = response.body()
        if (response.isSuccessful && body?.success == true) {
            body.data?.toModel()
        } else {
            throw ApiException(body?.message ?: "최근 기록을 불러오지 못했어요.")
        }
    }

    /** GET /api/buttons/categories — displayOrder 오름차순으로 정렬되어 내려온다 */
    suspend fun getCategories(): Result<List<Category>> =
        apiCallHandler.execute { buttonApi.getCategories() }
            .map { list -> list.map { Category(id = it.categoryId, name = it.categoryName) } }

    /** POST /api/buttons/categories */
    suspend fun createCategory(name: String): Result<Category> =
        apiCallHandler.execute { buttonApi.createCategory(CreateCategoryRequestDto(categoryName = name)) }
            .map { Category(id = it.categoryId, name = it.categoryName) }

    /** PATCH /api/buttons/categories/{category_id} */
    suspend fun renameCategory(categoryId: Long, name: String): Result<Category> =
        apiCallHandler.execute {
            buttonApi.updateCategoryName(categoryId, UpdateCategoryNameRequestDto(categoryName = name))
        }.map { Category(id = it.categoryId, name = it.categoryName) }

    /**
     * DELETE /api/buttons/categories/{category_id}
     * 응답 data가 빈 오브젝트로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun deleteCategory(categoryId: Long, deleteButtons: Boolean): Result<Unit> = runCatching {
        val response = buttonApi.deleteCategory(categoryId, deleteButtons)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "카테고리를 삭제하지 못했어요.")
        }
    }
}

private fun ButtonListResponseDto.toModel() = ButtonListResult(
    favorites = favorites.map { it.toModel() },
    habitButtons = categories.flatMap { group ->
        group.buttons.map { it.toModel(categoryName = group.categoryName) }
    },
)

private fun FavoriteButtonItemDto.toModel() = FavoriteButton(
    buttonId = buttonId,
    iconRes = ButtonIcons.resOf(iconName),
    iconTint = IconColor.from(iconColor).color,
    title = buttonName,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)

private fun CategoryButtonItemDto.toModel(categoryName: String?) = HabitButton(
    buttonId = buttonId,
    title = buttonName,
    category = categoryName.orEmpty(),
    iconRes = ButtonIcons.resOf(iconName),
    iconTint = IconColor.from(iconColor).color,
    isFavorite = isFavorite,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)

private fun RecordRecentResponseDto.toModel() = RecentRecord(
    buttonId = buttonId,
    iconRes = ButtonIcons.resOf(iconName),
    iconTint = IconColor.from(iconColor).color,
    title = buttonName,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)