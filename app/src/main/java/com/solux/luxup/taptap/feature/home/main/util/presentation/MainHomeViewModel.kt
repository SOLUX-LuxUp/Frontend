package com.solux.luxup.taptap.feature.home.main.util.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.core.auth.TokenManager
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

/** 검색어 입력 중 매 타이핑마다 API를 호출하지 않도록 두는 디바운스 간격 */
private const val SEARCH_DEBOUNCE_MILLIS = 300L

/**
 * 메인 화면 버튼 목록 정렬 — 즐겨찾기한 버튼이 먼저 오고, 그 안에서는(그리고 즐겨찾기가 아닌 버튼끼리도)
 * 먼저 만든 버튼일수록 앞에 온다. buttonId는 서버에서 생성 순서대로 증가하는 값이라 오래된 순 정렬에 그대로 쓴다.
 * 결과적으로 새로 만든 버튼은 항상 맨 아래로 간다.
 */
private fun List<HabitButton>.sortedForMainDisplay(): List<HabitButton> =
    sortedWith(compareByDescending<HabitButton> { it.isFavorite }.thenBy { it.buttonId })

@HiltViewModel(assistedFactory = MainHomeViewModel.Factory::class)
class MainHomeViewModel @AssistedInject constructor(
    @Assisted private val templateId: Long?,
    private val onboardingTemplateRepository: OnboardingTemplateRepository,
    private val buttonRepository: ButtonRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
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

    /** GET /api/buttons/search 결과. null이면 검색 중이 아니라는 뜻이라 화면에서는 카테고리 필터 목록을 그대로 보여준다 */
    var searchResults by mutableStateOf<List<HabitButton>?>(null)
        private set

    /**
     * 버튼이 0개일 때 "첫 번째 버튼을 만들어보세요" 추천을 보여줄지, 아니면 "버튼이 없어요"만 보여줄지 결정한다.
     * 로그인/회원가입 때 서버가 내려준 isOnboardingRequired를 시작값으로 쓰고, 버튼이 하나라도 있는 걸
     * 확인하는 순간(생성했든, 원래 있었든) 영구히 false로 굳혀서 — 나중에 전부 삭제해도 다시 추천이 뜨지 않게 한다.
     */
    var isFirstTimeEmptyState by mutableStateOf(tokenManager.isOnboardingRequired())
        private set

    private var searchJob: Job? = null

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
                    habitButtons = result.habitButtons.sortedForMainDisplay()
                    favoriteButtons = result.favorites
                    if (isFirstTimeEmptyState && (habitButtons.isNotEmpty() || favoriteButtons.isNotEmpty())) {
                        isFirstTimeEmptyState = false
                        tokenManager.saveOnboardingRequired(false)
                    }
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
                    // 삭제한 버튼이 "최근 기록" 배너에 떠 있었을 수도 있어 함께 새로고침한다.
                    loadRecentRecord()
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
            .sortedForMainDisplay()
        favoriteButtons = when {
            isFavorite && target != null && favoriteButtons.none { it.buttonId == buttonId } ->
                favoriteButtons + FavoriteButton(
                    buttonId = target.buttonId,
                    iconRes = target.iconRes,
                    iconTint = target.iconTint,
                    title = target.title,
                    lastRecordedAt = target.lastRecordedAt,
                )
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
     * 카테고리 옆 검색창에 입력할 때마다 호출된다 — GET /api/buttons/search를 디바운스해서 호출하고,
     * 검색어가 비어 있으면 검색을 종료해(searchResults = null) 카테고리 필터 목록으로 되돌린다.
     */
    fun searchButtons(keyword: String) {
        searchJob?.cancel()

        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) {
            searchResults = null
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            buttonRepository.searchButtons(trimmed)
                .onSuccess { searchResults = it }
                .onFailure { errorMessage = it.message ?: "버튼을 검색하지 못했어요." }
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
     * "첫 번째 버튼을 만들어보세요" 추천 카드에서 하나를 골랐을 때 호출된다.
     * [quickCreateFromSuggestion]과 동일한 방식(POST /api/buttons)으로 처리한다 —
     * POST /api/templates/{id}/apply는 온보딩을 이미 마친 유저에게 서버가 400을 내려주는데,
     * 이 추천을 하나라도 만들면 그 즉시 온보딩이 완료 처리되어 바로 다음 추천부터도 그 API가 막힌다.
     */
    fun applySuggestion(suggestion: TemplateButtonSuggestion) {
        quickCreateFromSuggestion(suggestion)
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

    /**
     * 버튼 생성 화면, 버튼 상세(기록) 화면 등에서 돌아왔을 때 다시 불러온다 —
     * 새 카테고리를 만들었을 수도 있어 카테고리도, 기록을 남기거나 삭제했을 수도 있어 최근 기록도 함께 새로고침한다.
     */
    fun refreshButtons() {
        loadButtons()
        loadCategories()
        loadRecentRecord()
    }

    /** 설정에서 닉네임/프로필 이미지를 바꾸고 돌아왔을 때 홈 화면에 바로 반영되도록 새로고침한다 */
    fun refreshProfile() {
        loadProfile()
    }

    fun consumeError() {
        errorMessage = null
    }
}
