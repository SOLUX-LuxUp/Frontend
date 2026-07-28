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

    private fun loadRecentRecord() {
        viewModelScope.launch {
            buttonRepository.getRecentRecord()
                .onSuccess { recentRecord = it }
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
                }
                .onFailure { e ->
                    errorMessage = e.message ?: "버튼을 추가하지 못했어요."
                }
            isApplying = false
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
