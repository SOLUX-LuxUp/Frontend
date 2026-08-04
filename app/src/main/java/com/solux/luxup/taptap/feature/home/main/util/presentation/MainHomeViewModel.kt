package com.solux.luxup.taptap.feature.home.main.util.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.auth.account.data.UserRepository
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.feature.home.main.model.HomeUser
import com.solux.luxup.taptap.feature.home.main.model.RecentRecord
import com.solux.luxup.taptap.feature.home.template.data.OnboardingTemplateRepository
import com.solux.luxup.taptap.feature.home.template.model.TemplateButtonSuggestion
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 메인 홈 화면.
 *
 * GET /api/users/profile로 닉네임을, GET /api/buttons·GET /api/records/recent로
 * 습관 버튼·즐겨찾기·최근 기록을 불러온다.
 *
 * 온보딩에서 템플릿을 골랐다면 templateId가 넘어온다 — 이 경우
 * GET /api/templates/{templateId}/recommendations로 그 템플릿의 카테고리별 추천 버튼만 불러와
 * "첫 번째 버튼을 만들어보세요" 섹션에 노출한다.
 * 건너뛴 경우(templateId 없음)는 모든 템플릿의 추천 버튼을 모아 보여준다.
 *
 * 추천 항목을 탭하면 POST /api/templates/{templateId}/apply로 해당 프리셋 하나만 생성한다
 * (건너뛴 경우도 각 추천이 어느 템플릿 소속인지 알고 있어야 해서 suggestion.templateId를 쓴다).
 */
/** cancelRecord는 서버 정책상 기록 생성 3초 이내에만 가능하다 */
private const val RECORD_CANCEL_WINDOW_MILLIS = 3_000L

@HiltViewModel(assistedFactory = MainHomeViewModel.Factory::class)
class MainHomeViewModel @AssistedInject constructor(
    @Assisted private val templateId: Long?,
    private val onboardingTemplateRepository: OnboardingTemplateRepository,
    private val buttonRepository: ButtonRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(templateId: Long?): MainHomeViewModel
    }

    private data class PendingRecord(val buttonId: Long, val recordId: Long)

    var homeUser by mutableStateOf(HomeUser(nickname = ""))
        private set

    var suggestions by mutableStateOf<List<TemplateButtonSuggestion>>(emptyList())
        private set

    var habitButtons by mutableStateOf<List<HabitButton>>(emptyList())
        private set

    var favoriteButtons by mutableStateOf<List<FavoriteButton>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var recentRecord by mutableStateOf<RecentRecord?>(null)
        private set

    /** 방금 남긴 기록 — null이 아니면 화면 상단에 "기록 완료!" 취소 배너를 띄운다 */
    private var pendingRecord by mutableStateOf<PendingRecord?>(null)
    val showRecordCompleteBanner: Boolean
        get() = pendingRecord != null

    private var recordCancelWindowJob: Job? = null

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var isApplying = false

    init {
        loadProfile()
        loadSuggestions()
        loadButtons()
        loadCategories()
        loadRecentRecord()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.getProfile()
                .onSuccess { homeUser = HomeUser(nickname = it.nickname) }
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            val id = templateId
            val result = if (id != null) {
                onboardingTemplateRepository.getTemplatePreview(id).map { it.suggestions }
            } else {
                onboardingTemplateRepository.getAllTemplateSuggestions()
            }
            result
                .onSuccess { suggestions = it }
                .onFailure { suggestions = emptyList() }
        }
    }

    private fun loadButtons() {
        viewModelScope.launch {
            buttonRepository.getButtons()
                .onSuccess { result ->
                    habitButtons = result.habitButtons
                    favoriteButtons = result.favorites
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            buttonRepository.getCategories()
                .onSuccess { categories = it }
        }
    }

    /** 카테고리 생성/수정/삭제는 "카테고리 수정" 모달에서만 호출된다 — 성공하면 목록을 다시 불러와 최신 상태로 맞춘다. */
    fun createCategory(name: String) {
        viewModelScope.launch {
            buttonRepository.createCategory(name)
                .onSuccess { loadCategories() }
                .onFailure { errorMessage = it.message ?: "카테고리를 추가하지 못했어요." }
        }
    }

    fun renameCategory(categoryId: Long, newName: String) {
        viewModelScope.launch {
            buttonRepository.renameCategory(categoryId, newName)
                .onSuccess {
                    // 습관 버튼 카드에 표시되는 카테고리 이름도 최신화해야 해서 버튼 목록도 함께 새로고침한다.
                    loadCategories()
                    loadButtons()
                }
                .onFailure { errorMessage = it.message ?: "카테고리를 수정하지 못했어요." }
        }
    }

    fun deleteCategory(categoryId: Long, deleteButtonsToo: Boolean) {
        viewModelScope.launch {
            buttonRepository.deleteCategory(categoryId, deleteButtonsToo)
                .onSuccess {
                    loadCategories()
                    loadButtons()
                }
                .onFailure { errorMessage = it.message ?: "카테고리를 삭제하지 못했어요." }
        }
    }

    /**
     * PATCH /api/buttons/category-order — 드래그로 바뀐 순서를 낙관적으로 먼저 반영하고,
     * 실패하면 원래 순서로 되돌린다.
     */
    fun reorderCategories(categoryIds: List<Long>) {
        val previous = categories
        categories = categoryIds.mapNotNull { id -> previous.find { it.id == id } }

        viewModelScope.launch {
            buttonRepository.updateCategoryOrder(categoryIds)
                .onFailure {
                    categories = previous
                    errorMessage = it.message ?: "카테고리 순서를 변경하지 못했어요."
                }
        }
    }

    /** DELETE /api/buttons/{button_id} — 성공하면 목록에서 즉시 제거한다 */
    fun deleteButton(buttonId: Long) {
        viewModelScope.launch {
            buttonRepository.deleteButton(buttonId)
                .onSuccess {
                    habitButtons = habitButtons.filterNot { it.buttonId == buttonId }
                    favoriteButtons = favoriteButtons.filterNot { it.buttonId == buttonId }
                }
                .onFailure { errorMessage = it.message ?: "버튼을 삭제하지 못했어요." }
        }
    }

    /**
     * PATCH /api/buttons/{button_id}/favorite — 별 아이콘 상태를 낙관적으로 먼저 반영하고,
     * 성공하면 GET /api/buttons/favorites로 즐겨찾기 목록을 새로고침해 순서를 맞춘다.
     * 실패하면 원래 상태로 되돌린다.
     */
    fun setFavorite(buttonId: Long, isFavorite: Boolean) {
        val previousHabitButtons = habitButtons
        val previousFavoriteButtons = favoriteButtons
        val target = habitButtons.find { it.buttonId == buttonId }

        habitButtons = habitButtons.map { if (it.buttonId == buttonId) it.copy(isFavorite = isFavorite) else it }
        favoriteButtons = when {
            isFavorite && target != null && favoriteButtons.none { it.buttonId == buttonId } ->
                listOf(
                    FavoriteButton(
                        buttonId = target.buttonId,
                        iconRes = target.iconRes,
                        iconTint = target.iconTint,
                        title = target.title,
                        lastRecordedAt = target.lastRecordedAt,
                    )
                ) + favoriteButtons
            !isFavorite -> favoriteButtons.filterNot { it.buttonId == buttonId }
            else -> favoriteButtons
        }

        viewModelScope.launch {
            buttonRepository.setFavorite(buttonId, isFavorite)
                .onSuccess { loadFavoriteButtons() }
                .onFailure {
                    habitButtons = previousHabitButtons
                    favoriteButtons = previousFavoriteButtons
                    errorMessage = it.message ?: "즐겨찾기를 변경하지 못했어요."
                }
        }
    }

    /**
     * PATCH /api/buttons/favorite-order — 즐겨찾기 수정 팝업에서 드래그로 바뀐 순서를 낙관적으로 먼저 반영하고,
     * 실패하면 원래 순서로 되돌린다.
     */
    fun reorderFavorites(buttonIds: List<Long>) {
        val previous = favoriteButtons
        favoriteButtons = buttonIds.mapNotNull { id -> previous.find { it.buttonId == id } }

        viewModelScope.launch {
            buttonRepository.updateFavoriteOrder(buttonIds)
                .onFailure {
                    favoriteButtons = previous
                    errorMessage = it.message ?: "즐겨찾기 순서를 변경하지 못했어요."
                }
        }
    }

    private fun loadFavoriteButtons() {
        viewModelScope.launch {
            buttonRepository.getFavoriteButtons()
                .onSuccess { favoriteButtons = it }
        }
    }

    private fun loadRecentRecord() {
        viewModelScope.launch {
            buttonRepository.getRecentRecord()
                .onSuccess { recentRecord = it }
        }
    }

    /**
     * 습관 버튼 카드를 한 번 탭했을 때 — POST /api/buttons/{button_id}/records로 바로 기록을 남기고,
     * 화면 상단에 "기록 완료!" 취소 배너를 3초간 띄운다. 그 안에 취소하지 않으면 배너는 그냥 사라지고
     * 기록은 그대로 유지된다(별도 확정 호출 없음).
     */
    fun quickRecord(button: HabitButton) {
        viewModelScope.launch {
            buttonRepository.createRecord(button.buttonId)
                .onSuccess { result ->
                    pendingRecord = PendingRecord(buttonId = result.buttonId, recordId = result.recordId)
                    refreshButtonLastRecordedAt(result.buttonId)
                    loadRecentRecord()

                    recordCancelWindowJob?.cancel()
                    recordCancelWindowJob = viewModelScope.launch {
                        delay(RECORD_CANCEL_WINDOW_MILLIS)
                        pendingRecord = null
                    }
                }
                .onFailure { errorMessage = it.message ?: "기록하지 못했어요." }
        }
    }

    /** "기록 완료!" 배너의 "취소"를 눌렀을 때 — DELETE .../records/{record_id}/cancel */
    fun cancelPendingRecord() {
        val pending = pendingRecord ?: return
        recordCancelWindowJob?.cancel()
        pendingRecord = null

        viewModelScope.launch {
            buttonRepository.cancelRecord(pending.buttonId, pending.recordId)
                .onSuccess {
                    refreshButtonLastRecordedAt(pending.buttonId)
                    loadRecentRecord()
                }
                .onFailure { errorMessage = it.message ?: "기록 취소에 실패했어요." }
        }
    }

    /** 방금 기록/취소한 버튼 하나만 GET .../records/latest로 다시 조회해 카드에 즉시 반영한다 */
    private fun refreshButtonLastRecordedAt(buttonId: Long) {
        viewModelScope.launch {
            buttonRepository.getLatestRecord(buttonId)
                .onSuccess { lastRecordedAt ->
                    val value = lastRecordedAt.orEmpty()
                    habitButtons = habitButtons.map {
                        if (it.buttonId == buttonId) it.copy(lastRecordedAt = value) else it
                    }
                    favoriteButtons = favoriteButtons.map {
                        if (it.buttonId == buttonId) it.copy(lastRecordedAt = value) else it
                    }
                }
        }
    }

    /**
     * 추천 버튼 하나를 골라 실제 버튼으로 생성한다.
     * 성공하면 다음 조회부터 빠지도록 목록에서 즉시 제거한다.
     */
    fun applySuggestion(suggestion: TemplateButtonSuggestion) {
        if (isApplying) return
        isApplying = true

        viewModelScope.launch {
            onboardingTemplateRepository.applyTemplate(suggestion.templateId, listOf(suggestion.presetId))
                .onSuccess {
                    suggestions = suggestions.filterNot {
                        it.presetId == suggestion.presetId && it.templateId == suggestion.templateId
                    }
                    loadButtons()
                }
                .onFailure { e ->
                    errorMessage = e.message ?: "버튼을 추가하지 못했어요."
                }
            isApplying = false
        }
    }

    /**
     * "빠르게 만들기" 팝업에서 추천 항목을 골랐을 때 — POST /api/templates/{id}/apply는
     * 온보딩을 이미 마친 유저에게는 서버가 400을 내려주므로, 이미 버튼이 있어도 항상 쓸 수 있도록
     * 일반 버튼 생성 API(POST /api/buttons)로 같은 이름·아이콘·카테고리의 버튼을 만든다.
     * 카테고리가 아직 없으면 먼저 만들고 그 id를 쓴다.
     */
    fun quickCreateFromSuggestion(suggestion: TemplateButtonSuggestion) {
        if (isApplying) return
        isApplying = true

        viewModelScope.launch {
            val existingCategoryId = categories.firstOrNull { it.name == suggestion.categoryName }?.id
            val categoryIdResult = existingCategoryId?.let { Result.success(it) }
                ?: buttonRepository.createCategory(suggestion.categoryName).map { it.id }

            categoryIdResult
                .mapCatching { categoryId ->
                    buttonRepository.createButton(
                        name = suggestion.buttonName,
                        categoryId = categoryId,
                        iconName = suggestion.iconName,
                        iconColor = suggestion.iconColor,
                        deadlineMillis = null,
                    ).getOrThrow()
                }
                .onSuccess {
                    suggestions = suggestions.filterNot {
                        it.presetId == suggestion.presetId && it.templateId == suggestion.templateId
                    }
                    loadCategories()
                    loadButtons()
                }
                .onFailure { e ->
                    errorMessage = e.message ?: "버튼을 추가하지 못했어요."
                }
            isApplying = false
        }
    }

    /** 버튼 생성 화면 등에서 돌아왔을 때 목록을 다시 불러온다 — 그 화면에서 새 카테고리를 만들었을 수도 있어 카테고리도 함께 새로고침한다 */
    fun refreshButtons() {
        loadButtons()
        loadCategories()
    }

    /** 설정에서 닉네임/프로필 이미지를 바꾸고 돌아왔을 때 홈 화면에 바로 반영되도록 새로고침한다 */
    fun refreshProfile() {
        loadProfile()
    }

    fun consumeError() {
        errorMessage = null
    }
}
