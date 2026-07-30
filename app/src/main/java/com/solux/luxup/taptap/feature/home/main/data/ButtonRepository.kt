package com.solux.luxup.taptap.feature.home.main.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.feature.home.main.model.RecentRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val buttonExpiryDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

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

    /** PATCH /api/buttons/{button_id}/favorite */
    suspend fun setFavorite(buttonId: Long, isFavorite: Boolean): Result<Boolean> =
        apiCallHandler.execute { buttonApi.setFavorite(buttonId, FavoriteRequestDto(isFavorite)) }
            .map { it.isFavorite }

    /** GET /api/buttons/favorites — favoriteOrder가 클수록 즐겨찾기를 나중에 추가한 것이라 내림차순으로 앞에 오도록 정렬한다 */
    suspend fun getFavoriteButtons(): Result<List<FavoriteButton>> =
        apiCallHandler.execute { buttonApi.getFavoriteButtons() }
            .map { list -> list.sortedByDescending { it.favoriteOrder ?: 0 }.map { it.toModel() } }

    /** PATCH /api/buttons/favorite-order — buttonIds를 원하는 순서 그대로 보내면 그 순서대로 favoriteOrder에 반영된다 */
    suspend fun updateFavoriteOrder(buttonIds: List<Long>): Result<Unit> =
        apiCallHandler.execute { buttonApi.updateFavoriteOrder(FavoriteOrderRequestDto(buttonIds)) }
            .map { }

    /** 수정 화면 초기값 채우기용 — "yyyy-MM-dd" 문자열을 밀리초로 되돌린다 */
    fun parseExpiryMillis(expiredAt: String?): Long? =
        expiredAt?.let { runCatching { buttonExpiryDateFormat.parse(it)?.time }.getOrNull() }

    /** POST /api/buttons */
    suspend fun createButton(
        name: String,
        categoryId: Long?,
        iconName: String?,
        iconColor: String?,
        deadlineMillis: Long?,
    ): Result<Unit> =
        apiCallHandler.execute {
            buttonApi.createButton(
                CreateButtonRequestDto(
                    buttonName = name,
                    iconName = iconName,
                    iconColor = iconColor,
                    categoryId = categoryId,
                    expiryEnabled = deadlineMillis != null,
                    expiredAt = deadlineMillis?.let { buttonExpiryDateFormat.format(Date(it)) },
                )
            )
        }.map { }

    /** PATCH /api/buttons/{button_id} */
    suspend fun updateButton(
        buttonId: Long,
        name: String,
        categoryId: Long?,
        iconName: String?,
        iconColor: String?,
        deadlineMillis: Long?,
    ): Result<Unit> =
        apiCallHandler.execute {
            buttonApi.updateButton(
                buttonId,
                UpdateButtonRequestDto(
                    buttonName = name,
                    iconName = iconName,
                    iconColor = iconColor,
                    categoryId = categoryId,
                    expiryEnabled = deadlineMillis != null,
                    expiredAt = deadlineMillis?.let { buttonExpiryDateFormat.format(Date(it)) },
                )
            )
        }.map { }

    /**
     * DELETE /api/buttons/{button_id}
     * 응답 data가 빈 오브젝트로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun deleteButton(buttonId: Long): Result<Unit> = runCatching {
        val response = buttonApi.deleteButton(buttonId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "버튼을 삭제하지 못했어요.")
        }
    }

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

    /** POST /api/buttons/{button_id}/records — 습관 버튼을 한 번 탭해 바로 기록을 남긴다 */
    suspend fun createRecord(buttonId: Long): Result<RecordCreateResponseDto> =
        apiCallHandler.execute { buttonApi.createRecord(buttonId) }

    /**
     * DELETE /api/buttons/{button_id}/records/{record_id}/cancel — 기록 완료 팝업(3초 이내)에서 "취소"를 눌렀을 때 호출.
     * 응답 data가 빈 오브젝트로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun cancelRecord(buttonId: Long, recordId: Long): Result<Unit> = runCatching {
        val response = buttonApi.cancelRecord(buttonId, recordId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "기록을 취소하지 못했어요.")
        }
    }

    /**
     * GET /api/buttons/{button_id}/records/latest — 기록 생성/취소 직후 버튼 목록 전체를 다시 불러오지 않고,
     * 방금 건드린 버튼 하나의 "마지막 기록 시간"만 가볍게 갱신할 때 쓴다.
     */
    suspend fun getLatestRecord(buttonId: Long): Result<String?> =
        apiCallHandler.execute { buttonApi.getLatestRecord(buttonId) }.map { it.lastRecordedAt }

    /** GET /api/buttons/categories — displayOrder 오름차순으로 정렬되어 내려온다 */
    suspend fun getCategories(): Result<List<Category>> =
        apiCallHandler.execute { buttonApi.getCategories() }
            .map { list -> list.map { Category(id = it.categoryId, name = it.categoryName) } }

    /** POST /api/buttons/categories */
    suspend fun createCategory(name: String): Result<Category> =
        apiCallHandler.execute { buttonApi.createCategory(CreateCategoryRequestDto(categoryName = name)) }
            .map { Category(id = it.categoryId, name = it.categoryName) }

    /** PATCH /api/buttons/category-order — categoryIds 순서 그대로 displayOrder에 반영된다 */
    suspend fun updateCategoryOrder(categoryIds: List<Long>): Result<Unit> =
        apiCallHandler.execute { buttonApi.updateCategoryOrder(CategoryOrderRequestDto(categoryIds)) }
            .map { }

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
    favorites = favorites.sortedByDescending { it.favoriteOrder ?: 0 }.map { it.toModel() },
    habitButtons = categories.flatMap { group ->
        group.buttons.map { it.toModel(categoryId = group.categoryId, categoryName = group.categoryName) }
    },
)

private fun FavoriteButtonItemDto.toModel() = FavoriteButton(
    buttonId = buttonId,
    iconRes = ButtonIcons.resOf(iconName),
    iconTint = IconColor.from(iconColor).color,
    title = buttonName,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)

private fun CategoryButtonItemDto.toModel(categoryId: Long?, categoryName: String?) = HabitButton(
    buttonId = buttonId,
    title = buttonName,
    category = categoryName.orEmpty(),
    categoryId = categoryId,
    iconRes = ButtonIcons.resOf(iconName),
    iconName = iconName.orEmpty(),
    iconTint = IconColor.from(iconColor).color,
    iconColorKey = iconColor,
    isFavorite = isFavorite,
    expiryEnabled = expiryEnabled,
    expiredAt = expiredAt,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)

private fun RecordRecentResponseDto.toModel() = RecentRecord(
    buttonId = buttonId,
    iconRes = ButtonIcons.resOf(iconName),
    iconTint = IconColor.from(iconColor).color,
    title = buttonName,
    lastRecordedAt = lastRecordedAt.orEmpty(),
)