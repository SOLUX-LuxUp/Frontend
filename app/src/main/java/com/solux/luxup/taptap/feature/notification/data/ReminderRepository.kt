package com.solux.luxup.taptap.feature.notification.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.model.ReminderConfig
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val BackendTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val KoreanTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
private val WeekdayShortLabels = listOf("월", "화", "수", "목", "금", "토", "일")

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderApi: ReminderApi,
    private val apiCallHandler: ApiCallHandler,
) {
    /** GET /api/reminders — categoryId·search는 서버 필터링용(둘 다 생략하면 버튼 전체) */
    suspend fun getReminders(categoryId: Long? = null, search: String? = null): Result<List<NotificationItem>> =
        apiCallHandler.execute { reminderApi.getReminders(categoryId, search?.takeIf { it.isNotBlank() }) }
            .map { list -> list.map { it.toModel() } }

    /** PATCH /api/reminders/{button_id} — 켬/끔 토글 */
    suspend fun toggle(buttonId: Long, isEnabled: Boolean): Result<Boolean> =
        apiCallHandler.execute { reminderApi.toggle(buttonId, ReminderToggleRequestDto(isEnabled)) }
            .map { it.isEnabled }

    /** PUT /api/reminders/{button_id}/detail — 별도 생성 API가 없어 추가·수정 모두 이 호출로 처리한다 */
    suspend fun saveDetail(buttonId: Long, config: ReminderConfig): Result<Unit> =
        apiCallHandler.execute {
            reminderApi.updateDetail(
                buttonId,
                ReminderDetailRequestDto(
                    frequencyType = config.frequencyType,
                    daysOfWeek = config.daysOfWeek.ifEmpty { null },
                    intervalWeeks = config.intervalWeeks,
                    dayOfMonth = config.dayOfMonth.ifEmpty { null },
                    reminderMode = config.reminderMode,
                    remindTimes = config.remindTimes.ifEmpty { null },
                    intervalHours = config.intervalHours,
                    activeStartTime = config.activeStartTime,
                    activeEndTime = config.activeEndTime,
                )
            )
        }.map { }

    /**
     * DELETE /api/reminders/{button_id}
     * 응답 data가 빈 오브젝트로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun deleteReminder(buttonId: Long): Result<Unit> = runCatching {
        val response = reminderApi.deleteReminder(buttonId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "알림을 삭제하지 못했어요.")
        }
    }
}

private fun ReminderListItemDto.toModel(): NotificationItem {
    val config = frequencyType?.let {
        ReminderConfig(
            frequencyType = it,
            daysOfWeek = daysOfWeek.orEmpty(),
            intervalWeeks = intervalWeeks,
            dayOfMonth = dayOfMonth.orEmpty(),
            reminderMode = reminderMode.orEmpty(),
            remindTimes = remindTimes.orEmpty(),
            intervalHours = intervalHours,
            activeStartTime = activeStartTime,
            activeEndTime = activeEndTime,
        )
    }
    return NotificationItem(
        id = buttonId,
        reminderId = reminderId,
        title = buttonName,
        category = categoryName.orEmpty(),
        categoryId = categoryId,
        iconRes = ButtonIcons.resOf(iconName),
        iconTint = IconColor.from(iconColor).color,
        scheduleText = buildScheduleText(),
        isEnabled = isEnabled,
        config = config,
    )
}

private fun ReminderListItemDto.buildScheduleText(): String {
    if (frequencyType == null) return "알림 설정 필요"

    val freqText = when (frequencyType) {
        "DAILY" -> "매일"
        "WEEKLY" -> daysOfWeek.orEmpty().sorted()
            .joinToString("") { WeekdayShortLabels.getOrElse(it - 1) { "" } }
        "MONTHLY" -> "매달 " + dayOfMonth.orEmpty().sorted().joinToString(", ") { "${it}일" }
        "CUSTOM" -> intervalWeeks?.let { "${it}주마다" } ?: "설정 반복"
        "ONCE" -> "오늘"
        else -> frequencyType
    }

    val modeText = when (reminderMode) {
        "TIME" -> remindTimes.orEmpty().joinToString(", ") { it.toKoreanTimeText() }
        "INTERVAL" -> intervalHours?.let { "${it}시간 마다" }.orEmpty()
        else -> ""
    }

    return if (modeText.isBlank()) freqText else "$freqText · $modeText"
}

private fun String.toKoreanTimeText(): String =
    runCatching { LocalTime.parse(this, BackendTimeFormatter).format(KoreanTimeFormatter) }.getOrElse { this }