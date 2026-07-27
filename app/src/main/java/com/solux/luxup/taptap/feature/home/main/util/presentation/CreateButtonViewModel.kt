package com.solux.luxup.taptap.feature.home.main.util.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import com.solux.luxup.taptap.feature.home.main.model.Category
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * "버튼 만들기" / "버튼 수정" 화면.
 *
 * 카테고리는 사용자 전역 개념이라 홈 화면과 동일하게 GET /api/buttons/categories를 사용한다.
 * 생성/수정/삭제는 "카테고리 수정" 모달에서만 호출되고, 성공하면 목록을 다시 불러와 최신 상태로 맞춘다.
 *
 * [buttonId]가 있으면 수정 모드 — 단건 조회 API가 없어 GET /api/buttons 목록에서 초기값을 찾아 채우고,
 * 저장 시 PATCH /api/buttons/{button_id}를 호출한다.
 */
@HiltViewModel(assistedFactory = CreateButtonViewModel.Factory::class)
class CreateButtonViewModel @AssistedInject constructor(
    @Assisted private val buttonId: Long?,
    private val buttonRepository: ButtonRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(buttonId: Long?): CreateButtonViewModel
    }

    val isEditMode: Boolean get() = buttonId != null

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    /** 수정 모드일 때 초기값 조회 중 */
    var isLoadingInitial by mutableStateOf(isEditMode)
        private set

    var initialName by mutableStateOf("")
        private set

    var initialCategoryName by mutableStateOf<String?>(null)
        private set

    var initialIconName by mutableStateOf<String?>(null)
        private set

    var initialIconColor by mutableStateOf<IconColor?>(null)
        private set

    var initialDeadlineMillis by mutableStateOf<Long?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 중복 제출 방지 */
    var isSubmitting by mutableStateOf(false)
        private set

    init {
        loadCategories()
        if (isEditMode) loadInitialValues()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            buttonRepository.getCategories()
                .onSuccess { categories = it }
        }
    }

    /** 개별 버튼 단건 조회 API가 없어, 목록 조회 결과에서 수정할 버튼을 찾아 초기값을 채운다 */
    private fun loadInitialValues() {
        viewModelScope.launch {
            buttonRepository.getButtons()
                .onSuccess { result ->
                    val button = result.habitButtons.firstOrNull { it.buttonId == buttonId }
                    if (button != null) {
                        initialName = button.title
                        initialCategoryName = button.category.takeIf { it.isNotBlank() }
                        initialIconName = button.iconName.takeIf { it.isNotBlank() }
                        initialIconColor = button.iconColorKey?.let { IconColor.from(it) }
                        initialDeadlineMillis = if (button.expiryEnabled) {
                            buttonRepository.parseExpiryMillis(button.expiredAt)
                        } else {
                            null
                        }
                    } else {
                        errorMessage = "버튼 정보를 찾지 못했어요."
                    }
                }
                .onFailure { errorMessage = it.message ?: "버튼 정보를 불러오지 못했어요." }
            isLoadingInitial = false
        }
    }

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
                .onSuccess { loadCategories() }
                .onFailure { errorMessage = it.message ?: "카테고리를 수정하지 못했어요." }
        }
    }

    fun deleteCategory(categoryId: Long, deleteButtonsToo: Boolean) {
        viewModelScope.launch {
            buttonRepository.deleteCategory(categoryId, deleteButtonsToo)
                .onSuccess { loadCategories() }
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

    /**
     * POST /api/buttons 또는 PATCH /api/buttons/{button_id}.
     * categoryName은 화면 드롭다운이 이름으로 들고 있어 categories에서 id를 찾는다.
     */
    fun save(
        name: String,
        categoryName: String?,
        iconName: String?,
        iconColor: String?,
        deadlineMillis: Long?,
        onSuccess: () -> Unit,
    ) {
        if (isSubmitting) return
        isSubmitting = true
        val categoryId = categories.firstOrNull { it.name == categoryName }?.id

        viewModelScope.launch {
            val result = if (isEditMode) {
                buttonRepository.updateButton(
                    buttonId = requireNotNull(buttonId),
                    name = name,
                    categoryId = categoryId,
                    iconName = iconName,
                    iconColor = iconColor,
                    deadlineMillis = deadlineMillis,
                )
            } else {
                buttonRepository.createButton(
                    name = name,
                    categoryId = categoryId,
                    iconName = iconName,
                    iconColor = iconColor,
                    deadlineMillis = deadlineMillis,
                )
            }

            result.onSuccess {
                isSubmitting = false
                onSuccess()
            }.onFailure { e ->
                isSubmitting = false
                errorMessage = e.message ?: if (isEditMode) "버튼을 수정하지 못했어요." else "버튼을 생성하지 못했어요."
            }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}