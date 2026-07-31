package com.solux.luxup.taptap.feature.notification.data

import kotlinx.serialization.Serializable

// ---- reminder-controller ----

/** GET /api/reminders?categoryId=&search= — 버튼 전체(설정 여부 무관)를 알림 상태와 함께 내려준다 */
@Serializable
data class ReminderListItemDto(
    val reminderId: Long? = null,
    val buttonId: Long,
    val buttonName: String,
    val iconName: String? = null,
    val iconColor: String? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val isEnabled: Boolean = false,
    val frequencyType: String? = null,
    val daysOfWeek: List<Int>? = null,
    val intervalWeeks: Int? = null,
    val dayOfMonth: List<Int>? = null,
    val onceActivatedAt: String? = null,
    val reminderMode: String? = null,
    val remindTimes: List<String>? = null,
    val intervalHours: Int? = null,
    val activeStartTime: String? = null,
    val activeEndTime: String? = null,
    val updatedAt: String? = null,
)

/** PUT /api/reminders/{button_id}/detail — 추가·수정 겸용(별도 생성 API가 없다) */
@Serializable
data class ReminderDetailRequestDto(
    val frequencyType: String,
    val daysOfWeek: List<Int>? = null,
    val intervalWeeks: Int? = null,
    val dayOfMonth: List<Int>? = null,
    val reminderMode: String,
    val remindTimes: List<String>? = null,
    val intervalHours: Int? = null,
    val activeStartTime: String? = null,
    val activeEndTime: String? = null,
)

@Serializable
data class ReminderDetailResponseDto(
    val reminderId: Long? = null,
    val buttonId: Long? = null,
    val frequencyType: String? = null,
    val daysOfWeek: List<Int>? = null,
    val intervalWeeks: Int? = null,
    val dayOfMonth: List<Int>? = null,
    val onceActivatedAt: String? = null,
    val reminderMode: String? = null,
    val remindTimes: List<String>? = null,
    val intervalHours: Int? = null,
    val activeStartTime: String? = null,
    val activeEndTime: String? = null,
    val updatedAt: String? = null,
)

/** PATCH /api/reminders/{button_id} */
@Serializable
data class ReminderToggleRequestDto(
    val isEnabled: Boolean,
)

@Serializable
data class ReminderToggleResponseDto(
    val reminderId: Long? = null,
    val buttonId: Long? = null,
    val isEnabled: Boolean = false,
    val updatedAt: String? = null,
)