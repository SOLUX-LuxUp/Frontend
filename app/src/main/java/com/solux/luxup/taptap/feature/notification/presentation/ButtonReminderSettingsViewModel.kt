package com.solux.luxup.taptap.feature.notification.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.notification.data.ReminderRepository
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.model.ReminderConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * "버튼 수정" 화면의 "알림설정" 진입점 — 버튼 하나의 알림을 조회·저장한다.
 *
 * 버튼 단건으로 알림을 조회하는 API가 없어, GET /api/reminders 전체 목록에서 이 buttonId를 찾아 초기값을 채운다.
 * 저장은 PUT /api/reminders/{button_id}/detail 하나로 추가·수정을 모두 처리한다(Upsert).
 */
@HiltViewModel(assistedFactory = ButtonReminderSettingsViewModel.Factory::class)
class ButtonReminderSettingsViewModel @AssistedInject constructor(
    @Assisted("buttonId") private val buttonId: Long,
    @Assisted("title") private val title: String?,
    @Assisted("iconName") private val iconName: String?,
    @Assisted("iconColor") private val iconColor: String?,
    private val reminderRepository: ReminderRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("buttonId") buttonId: Long,
            @Assisted("title") title: String?,
            @Assisted("iconName") iconName: String?,
            @Assisted("iconColor") iconColor: String?,
        ): ButtonReminderSettingsViewModel
    }

    var item by mutableStateOf<NotificationItem?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 저장 중복 제출 방지 */
    var isSubmitting by mutableStateOf(false)
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            reminderRepository.getReminders()
                .onSuccess { reminders -> item = reminders.firstOrNull { it.id == buttonId } ?: fallbackItem() }
                .onFailure {
                    errorMessage = it.message ?: "알림 설정을 불러오지 못했어요."
                    item = fallbackItem()
                }
        }
    }

    private fun fallbackItem() = NotificationItem(
        id = buttonId,
        title = title.orEmpty(),
        category = "",
        iconRes = ButtonIcons.resOf(iconName),
        iconTint = IconColor.from(iconColor).color,
        scheduleText = "",
        isEnabled = false,
    )

    fun saveDetail(config: ReminderConfig, onSuccess: () -> Unit) {
        if (isSubmitting) return
        isSubmitting = true
        viewModelScope.launch {
            reminderRepository.saveDetail(buttonId, config)
                .onSuccess {
                    isSubmitting = false
                    onSuccess()
                }
                .onFailure {
                    isSubmitting = false
                    errorMessage = it.message ?: "알림 설정을 저장하지 못했어요."
                }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}