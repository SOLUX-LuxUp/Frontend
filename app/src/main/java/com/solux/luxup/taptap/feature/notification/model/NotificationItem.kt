package com.solux.luxup.taptap.feature.notification.model

import androidx.compose.ui.graphics.Color

data class NotificationItem(
    val id: Long,
    val title: String,
    val category: String,
    val iconRes: Int,
    val iconTint: Color,
    val scheduleText: String,
    val isEnabled: Boolean,
    val reminderId: Long? = null,
    val categoryId: Long? = null,
    // 한 번도 설정한 적 없는 버튼(= 알림 추가 후보)이면 null
    val config: ReminderConfig? = null,
)

/** PUT /api/reminders/{button_id}/detail 요청·응답에 대응하는 알림 세부 설정 */
data class ReminderConfig(
    val frequencyType: String,
    val daysOfWeek: List<Int> = emptyList(),
    val intervalWeeks: Int? = null,
    val dayOfMonth: List<Int> = emptyList(),
    val reminderMode: String,
    val remindTimes: List<String> = emptyList(),
    val intervalHours: Int? = null,
    val activeStartTime: String? = null,
    val activeEndTime: String? = null,
)