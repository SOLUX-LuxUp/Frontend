package com.solux.luxup.taptap.feature.notification.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.feature.notification.data.ReminderRepository
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.model.ReminderConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 알림 화면 — 하단 네비 NOTIFICATION 탭.
 *
 *  - GET    /api/reminders                     이미 알림이 설정된(reminder 레코드가 있는) 버튼 목록
 *  - PATCH  /api/reminders/{button_id}          켬/끔 토글
 *  - PUT    /api/reminders/{button_id}/detail   세부 설정 저장 — 추가·수정 겸용(별도 생성 API 없음)
 *  - DELETE /api/reminders/{button_id}          설정 삭제(= 알림 추가 후보로 되돌아감)
 *
 * GET /api/reminders는 reminder 레코드가 이미 있는 버튼만 내려주고 갓 만든 버튼은 포함하지 않아,
 * "알림 추가" 후보는 GET /api/buttons 전체 목록에서 이 목록에 없는 버튼만 걸러 별도로 계산한다.
 * 카테고리 필터는 버튼 카테고리(button-category-controller)를 그대로 재사용한다.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val buttonRepository: ButtonRepository,
) : ViewModel() {

    var reminders by mutableStateOf<List<NotificationItem>>(emptyList())
        private set

    var addableButtons by mutableStateOf<List<NotificationItem>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadCategories()
        load()
    }

    /** 다른 화면(버튼 생성/수정, 카테고리 관리 등)을 다녀왔을 때(ON_RESUME) 최신 상태로 맞춘다. */
    fun refresh() {
        load()
        loadCategories()
    }

    fun load() {
        viewModelScope.launch {
            isLoading = true

            reminderRepository.getReminders()
                .onSuccess { reminders = it }
                .onFailure { errorMessage = it.message ?: "알림 목록을 불러오지 못했어요." }

            val configuredIds = reminders.map { it.id }.toSet()
            buttonRepository.getButtons()
                .onSuccess { result ->
                    addableButtons = result.habitButtons
                        .filterNot { it.buttonId in configuredIds }
                        .map { it.toAddableNotificationItem() }
                }
                .onFailure { errorMessage = it.message ?: "버튼 목록을 불러오지 못했어요." }

            isLoading = false
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            buttonRepository.getCategories()
                .onSuccess { categories = it }
                .onFailure { errorMessage = it.message ?: "카테고리를 불러오지 못했어요." }
        }
    }

    fun toggle(buttonId: Long, isEnabled: Boolean) {
        val previous = reminders
        reminders = reminders.map { if (it.id == buttonId) it.copy(isEnabled = isEnabled) else it }
        viewModelScope.launch {
            reminderRepository.toggle(buttonId, isEnabled)
                .onFailure {
                    reminders = previous
                    errorMessage = it.message ?: "알림 상태를 변경하지 못했어요."
                }
        }
    }

    fun saveDetail(buttonId: Long, config: ReminderConfig) {
        viewModelScope.launch {
            reminderRepository.saveDetail(buttonId, config)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "알림을 저장하지 못했어요." }
        }
    }

    fun delete(buttonId: Long) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(buttonId)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "알림을 삭제하지 못했어요." }
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
                .onSuccess {
                    // 알림 목록 행에 표시되는 카테고리 이름도 최신화해야 해서 목록도 함께 새로고침한다.
                    loadCategories()
                    load()
                }
                .onFailure { errorMessage = it.message ?: "카테고리 이름을 변경하지 못했어요." }
        }
    }

    fun deleteCategory(categoryId: Long, deleteButtonsToo: Boolean) {
        viewModelScope.launch {
            buttonRepository.deleteCategory(categoryId, deleteButtonsToo)
                .onSuccess {
                    loadCategories()
                    load()
                }
                .onFailure { errorMessage = it.message ?: "카테고리를 삭제하지 못했어요." }
        }
    }

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

    fun consumeError() {
        errorMessage = null
    }
}

private fun HabitButton.toAddableNotificationItem() = NotificationItem(
    id = buttonId,
    title = title,
    category = category,
    categoryId = categoryId,
    iconRes = iconRes,
    iconTint = iconTint,
    scheduleText = "",
    isEnabled = false,
)