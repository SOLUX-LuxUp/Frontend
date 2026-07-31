package com.solux.luxup.taptap.feature.notification.presentation

import com.solux.luxup.taptap.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.feature.notification.data.mockNotifications
import com.solux.luxup.taptap.feature.notification.model.AlarmInterval
import com.solux.luxup.taptap.feature.notification.model.AlarmMode
import com.solux.luxup.taptap.feature.notification.model.CustomRepeatInterval
import com.solux.luxup.taptap.feature.notification.model.CustomRepeatUnit
import com.solux.luxup.taptap.feature.notification.model.IntervalUnit
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.model.ReminderConfig
import com.solux.luxup.taptap.feature.notification.model.RepeatOption
import com.solux.luxup.taptap.feature.notification.model.TimeRange
import com.solux.luxup.taptap.feature.notification.model.backendTimeToDisplay
import com.solux.luxup.taptap.feature.notification.model.displayTimeToBackend
import com.solux.luxup.taptap.feature.notification.model.parseTimeInput
import com.solux.luxup.taptap.feature.notification.model.toBackendDaysOfWeek
import com.solux.luxup.taptap.feature.notification.model.toDisplayText
import com.solux.luxup.taptap.feature.notification.model.toWeekdayIndices
import com.solux.luxup.taptap.feature.notification.util.MonthDayPickerDialog
import com.solux.luxup.taptap.feature.notification.util.NotificationSwitch
import com.solux.luxup.taptap.feature.notification.util.WeekdayLabels
import com.solux.luxup.taptap.feature.notification.util.formatMonthDaySummary
import com.solux.luxup.taptap.feature.notification.util.formatWeekdaySummary
import com.solux.luxup.taptap.feature.notification.util.notificationBadgeDropShadow
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import java.time.LocalTime

private val LabelGray = Color(0xFF6D6D6D)
private val PlaceholderGray = Color(0xFFB1B1B1)
private val SelectedGradient = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd))

// 체크리스트에서 항목을 탭했을 때 뜨는 알림 세부 설정 화면 - 미체크 항목은 "알림 추가", 체크된 항목은 "알림 수정"으로 열림
@Composable
fun NotificationDetailScreen(
    item: NotificationItem,
    isNew: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: (ReminderConfig) -> Unit
) {
    val initialConfig = item.config

    var repeatEnabled by remember { mutableStateOf(initialConfig?.let { it.frequencyType != "ONCE" } ?: true) }
    var repeatOption by remember {
        mutableStateOf(
            initialConfig?.frequencyType
                ?.takeIf { it != "ONCE" }
                ?.let { runCatching { RepeatOption.valueOf(it) }.getOrNull() }
                ?: RepeatOption.DAILY
        )
    }
    var selectedWeekdays by remember {
        mutableStateOf(
            initialConfig?.takeIf { it.frequencyType == "WEEKLY" }?.daysOfWeek?.toWeekdayIndices() ?: emptySet()
        )
    }
    var selectedMonthDays by remember {
        mutableStateOf(
            initialConfig?.takeIf { it.frequencyType == "MONTHLY" }?.dayOfMonth?.toSet() ?: emptySet()
        )
    }
    var showMonthDayPicker by remember { mutableStateOf(false) }
    // "설정"(CUSTOM) 반복은 서버에서 daysOfWeek·intervalWeeks가 항상 함께 필요하고 dayOfMonth는 늘 null이어야 해서
    // — 달 단위 커스텀 반복 필드가 서버에 따로 없어 — 항상 주 단위로만 저장된다.
    var customInterval by remember {
        mutableStateOf(
            initialConfig?.takeIf { it.frequencyType == "CUSTOM" }?.intervalWeeks
                ?.let { weeks -> CustomRepeatInterval(weeks, CustomRepeatUnit.WEEK) }
                ?: CustomRepeatInterval(value = 1, unit = CustomRepeatUnit.WEEK)
        )
    }
    var customWeekdays by remember {
        mutableStateOf(
            initialConfig?.takeIf { it.frequencyType == "CUSTOM" }?.daysOfWeek?.toWeekdayIndices() ?: emptySet()
        )
    }
    var showCustomWeekdayPicker by remember { mutableStateOf(false) }
    var customMonthDays by remember { mutableStateOf(emptySet<Int>()) }
    var showCustomMonthDayPicker by remember { mutableStateOf(false) }
    var alarmMode by remember {
        mutableStateOf(
            initialConfig?.reminderMode?.let { runCatching { AlarmMode.valueOf(it) }.getOrNull() } ?: AlarmMode.INTERVAL
        )
    }
    var interval by remember {
        mutableStateOf(
            initialConfig?.intervalHours?.let { AlarmInterval(value = it, unit = IntervalUnit.HOUR) }
                ?: AlarmInterval(value = 1, unit = IntervalUnit.HOUR)
        )
    }
    var activeRanges by remember {
        mutableStateOf(
            initialConfig?.let { cfg ->
                val start = cfg.activeStartTime
                val end = cfg.activeEndTime
                if (start != null && end != null) {
                    listOf(TimeRange(start.backendTimeToDisplay(), end.backendTimeToDisplay()))
                } else {
                    null
                }
            } ?: listOf(TimeRange("12:00 AM", "12:00 AM"))
        )
    }
    var scheduledTimes by remember {
        mutableStateOf(
            initialConfig?.remindTimes?.takeIf { it.isNotEmpty() }?.map { it.backendTimeToDisplay() }
                ?: listOf("7:00 AM")
        )
    }

    // 화면의 현재 설정 상태를 저장 요청(PUT /api/reminders/{button_id}/detail) payload로 변환한다.
    // 서버 validation: CUSTOM은 daysOfWeek·intervalWeeks가 항상 함께 필요하고 dayOfMonth는 늘 null이어야 해서
    // — 달 단위 커스텀 반복(customInterval.unit == MONTH)은 서버에 대응하는 필드가 없다 — CUSTOM은 항상 주 단위로 저장한다.
    fun buildConfig(): ReminderConfig {
        val frequencyType = if (!repeatEnabled) "ONCE" else repeatOption.name

        val daysOfWeek = when {
            repeatEnabled && repeatOption == RepeatOption.WEEKLY -> selectedWeekdays.toBackendDaysOfWeek()
            repeatEnabled && repeatOption == RepeatOption.CUSTOM -> customWeekdays.toBackendDaysOfWeek()
            else -> emptyList()
        }

        val dayOfMonth = if (repeatEnabled && repeatOption == RepeatOption.MONTHLY) {
            selectedMonthDays.sorted()
        } else {
            emptyList()
        }

        val intervalWeeks = if (repeatEnabled && repeatOption == RepeatOption.CUSTOM) {
            customInterval.value
        } else {
            null
        }

        val remindTimes = if (alarmMode == AlarmMode.TIME) {
            scheduledTimes.map { it.displayTimeToBackend() }
        } else {
            emptyList()
        }

        // 백엔드는 시간(intervalHours) 단위만 지원해서, 분 단위로 설정했다면 반올림해 시간으로 환산한다.
        val intervalHours = if (alarmMode == AlarmMode.INTERVAL) {
            when (interval.unit) {
                IntervalUnit.HOUR -> interval.value
                IntervalUnit.MINUTE -> (interval.value / 60).coerceAtLeast(1)
            }
        } else {
            null
        }

        // 백엔드는 활성화 시간대를 1개(activeStartTime/activeEndTime)만 지원하고, ONCE에서는 항상 null이어야 한다.
        // 화면에서 여러 구간을 추가했다면 첫 구간만 저장한다.
        val firstActiveRange = activeRanges.firstOrNull()
        val activeTimeRangeAllowed = repeatEnabled && alarmMode == AlarmMode.INTERVAL
        val activeStartTime = if (activeTimeRangeAllowed) firstActiveRange?.start?.displayTimeToBackend() else null
        val activeEndTime = if (activeTimeRangeAllowed) firstActiveRange?.end?.displayTimeToBackend() else null

        return ReminderConfig(
            frequencyType = frequencyType,
            daysOfWeek = daysOfWeek,
            intervalWeeks = intervalWeeks,
            dayOfMonth = dayOfMonth,
            reminderMode = alarmMode.name,
            remindTimes = remindTimes,
            intervalHours = intervalHours,
            activeStartTime = activeStartTime,
            activeEndTime = activeEndTime,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp)
    ) {
        Spacer(Modifier.height(70.dp))

        Box(Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = PlaceholderGray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clickable { onBack() }
            )
            Text(
                text = if (isNew) "알림 추가" else "알림 수정",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(SelectedGradient)
                    .clickable { onConfirm(buildConfig()) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (isNew) "알림 추가 완료" else "알림 수정 완료",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .notificationBadgeDropShadow()
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = item.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF727272),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("반복 설정", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            NotificationSwitch(checked = repeatEnabled, onCheckedChange = { repeatEnabled = it })
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RepeatOption.entries.forEach { option ->
                SegmentPill(
                    text = option.label,
                    selected = repeatOption == option,
                    enabled = repeatEnabled,
                    onClick = { repeatOption = option },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (repeatOption == RepeatOption.WEEKLY) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeekdayLabels.forEachIndexed { index, label ->
                    WeekdayPill(
                        text = label,
                        selected = index in selectedWeekdays,
                        enabled = repeatEnabled,
                        onClick = {
                            selectedWeekdays = if (index in selectedWeekdays) {
                                selectedWeekdays - index
                            } else {
                                selectedWeekdays + index
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (repeatOption == RepeatOption.MONTHLY) {
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "날짜 선택",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(28.dp)
                        .then(
                            if (repeatEnabled) {
                                Modifier.clickable { showMonthDayPicker = true }
                            } else {
                                Modifier
                            }
                        )
                        .alpha(if (repeatEnabled) 1f else 0.4f)
                )
                Spacer(Modifier.width(12.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(43.dp)
                        .figmaDropShadow(cornerRadius = 10.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable(enabled = repeatEnabled) { showMonthDayPicker = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatMonthDaySummary(selectedMonthDays).ifEmpty { "날짜 선택" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedMonthDays.isEmpty()) PlaceholderGray else LabelGray
                    )
                }
            }
            if (showMonthDayPicker) {
                MonthDayPickerDialog(
                    selectedDays = selectedMonthDays,
                    onDaysChange = { selectedMonthDays = it },
                    onDismiss = { showMonthDayPicker = false }
                )
            }
        }

        if (repeatOption == RepeatOption.CUSTOM) {
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .figmaDropShadow(cornerRadius = 10.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepperSymbol(symbol = "-", onClick = { customInterval = customInterval.decreased() })
                    Spacer(Modifier.width(14.dp))
                    IntervalDivider()
                    Spacer(Modifier.width(14.dp))
                    NumberValueField(
                        value = customInterval.value,
                        range = customInterval.unit.range,
                        onValueChange = { customInterval = customInterval.withValueInput(it) }
                    )
                    Spacer(Modifier.width(14.dp))
                    IntervalDivider()
                    Spacer(Modifier.width(14.dp))
                    StepperSymbol(symbol = "+", onClick = { customInterval = customInterval.increased() })
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .figmaDropShadow(cornerRadius = 8.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable { customInterval = customInterval.withUnitToggled() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(customInterval.unit.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LabelGray)
                }
                if (customInterval.unit == CustomRepeatUnit.WEEK) {
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .figmaDropShadow(cornerRadius = 8.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { showCustomWeekdayPicker = !showCustomWeekdayPicker }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = formatWeekdaySummary(customWeekdays).ifEmpty { "요일 선택" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (customWeekdays.isEmpty()) PlaceholderGray else LabelGray
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text("마다", fontSize = 15.sp, color = LabelGray)
            }

            if (customInterval.unit == CustomRepeatUnit.WEEK && showCustomWeekdayPicker) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeekdayLabels.forEachIndexed { index, label ->
                        WeekdayPill(
                            text = label,
                            selected = index in customWeekdays,
                            onClick = {
                                customWeekdays = if (index in customWeekdays) {
                                    customWeekdays - index
                                } else {
                                    customWeekdays + index
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (customInterval.unit == CustomRepeatUnit.MONTH) {
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = "날짜 선택",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { showCustomMonthDayPicker = true }
                    )
                    Spacer(Modifier.width(12.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(43.dp)
                            .figmaDropShadow(cornerRadius = 10.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { showCustomMonthDayPicker = true }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatMonthDaySummary(customMonthDays).ifEmpty { "날짜 선택" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (customMonthDays.isEmpty()) PlaceholderGray else LabelGray
                        )
                    }
                }
                if (showCustomMonthDayPicker) {
                    MonthDayPickerDialog(
                        selectedDays = customMonthDays,
                        onDaysChange = { customMonthDays = it },
                        onDismiss = { showCustomMonthDayPicker = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("알람설정", fontSize = 14.sp, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlarmMode.entries.forEach { mode ->
                SegmentPill(
                    text = mode.label,
                    selected = alarmMode == mode,
                    onClick = { alarmMode = mode },
                    modifier = Modifier
                        .weight(1f)
                        .height(43.dp)
                )
            }
        }

        if (alarmMode == AlarmMode.INTERVAL) {
            Spacer(Modifier.height(20.dp))
            Text("간격설정", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("매", fontSize = 14.sp, color = Color(0xFF6D6D6D))
                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .figmaDropShadow(cornerRadius = 10.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepperSymbol(symbol = "-", onClick = { interval = interval.decreased() })
                    Spacer(Modifier.width(14.dp))
                    IntervalDivider()
                    Spacer(Modifier.width(14.dp))
                    NumberValueField(
                        value = interval.value,
                        range = interval.unit.range,
                        onValueChange = { interval = interval.withValueInput(it) }
                    )
                    Spacer(Modifier.width(14.dp))
                    IntervalDivider()
                    Spacer(Modifier.width(14.dp))
                    StepperSymbol(symbol = "+", onClick = { interval = interval.increased() })
                }
                Spacer(Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .figmaDropShadow(cornerRadius = 8.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable { interval = interval.withUnitToggled() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(interval.unit.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LabelGray)
                }
                Spacer(Modifier.width(10.dp))
                Text("마다", fontSize = 15.sp, color = LabelGray)
            }

            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("활성화 시간대", fontSize = 14.sp, color = Color(0xFF6D6D6D))
                Row(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFE2E2E2),
                        shape = RoundedCornerShape(50.dp)
                        )
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable {
                            activeRanges = activeRanges + TimeRange("12:00 AM", "12:00 AM")
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFB1B1B1), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("추가", fontSize = 14.sp, color = Color(0xFFB1B1B1))
                }
            }
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                activeRanges.forEachIndexed { index, range ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimePill(
                            text = range.start,
                            onValueChange = { newStart ->
                                activeRanges = activeRanges.mapIndexed { i, r ->
                                    if (i == index) r.copy(start = newStart) else r
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        TimePill(
                            text = range.end,
                            onValueChange = { newEnd ->
                                activeRanges = activeRanges.mapIndexed { i, r ->
                                    if (i == index) r.copy(end = newEnd) else r
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_trash),
                            contentDescription = "시간대 삭제",
                            tint = PlaceholderGray,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    activeRanges = activeRanges.filterIndexed { i, _ -> i != index }
                                }
                        )
                    }
                }
            }
        } else {
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("시간설정", fontSize = 14.sp, color = Color(0xFF6D6D6D))
                Row(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFE2E2E2),
                        shape = RoundedCornerShape(50.dp)
                        )
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable {
                            scheduledTimes = scheduledTimes + "12:00 AM"
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFB1B1B1), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("추가", fontSize = 14.sp, color = Color(0xFFB1B1B1))
                }
            }
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                scheduledTimes.forEachIndexed { index, time ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimePill(
                            text = time,
                            onValueChange = { newTime ->
                                scheduledTimes = scheduledTimes.mapIndexed { i, t ->
                                    if (i == index) newTime else t
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_trash),
                            contentDescription = "시간 삭제",
                            tint = PlaceholderGray,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    scheduledTimes = scheduledTimes.filterIndexed { i, _ -> i != index }
                                }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SegmentPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(43.dp)
            .then(
                if (selected) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SelectedGradient)
                } else {
                    Modifier
                        .figmaDropShadow(cornerRadius = 8.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                }
            )
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else LabelGray
        )
    }
}

@Composable
private fun WeekdayPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (selected) {
                    Modifier
                        .figmaDropShadow(cornerRadius = 50.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(CircleShape)
                        .background(SelectedGradient)
                } else {
                    Modifier
                        .figmaDropShadow(cornerRadius = 50.dp, alpha = 0.12f, blurRadius = 7.8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                }
            )
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else LabelGray
        )
    }
}

@Composable
private fun StepperSymbol(symbol: String, onClick: () -> Unit) {
    Text(
        text = symbol,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = LabelGray,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
private fun NumberValueField(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(value.toString()) }
    var hasFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    fun commit() {
        val parsed = text.toIntOrNull()
        onValueChange((parsed ?: value).coerceIn(range))
        isEditing = false
        hasFocused = false
    }

    if (isEditing) {
        BasicTextField(
            value = text,
            onValueChange = { new ->
                if (new.length <= 2 && new.all(Char::isDigit)) text = new
            },
            modifier = Modifier
                .width(24.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        hasFocused = true
                    } else if (hasFocused) {
                        commit()
                    }
                },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { commit() })
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.clickable {
                text = value.toString()
                isEditing = true
            }
        )
    }
}

@Composable
private fun IntervalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(20.dp)
            .background(Color(0xFFE2E2E2))
    )
}

/** 활성화 시간대 박스: 시/분은 세로 스크롤 또는 탭한 뒤 직접 입력으로, AM/PM은 탭으로 변경한다. */
@Composable
private fun TimePill(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeState = remember(text) { mutableStateOf(parseTimeInput(text) ?: LocalTime.NOON) }
    val time = timeState.value
    val hour12 = ((time.hour + 11) % 12) + 1
    val isAm = time.hour < 12

    fun applyHour12(newHour12: Int) {
        val hour = newHour12.coerceIn(1, 12) % 12
        val hour24 = if (timeState.value.hour < 12) hour else hour + 12
        val newTime = timeState.value.withHour(hour24)
        timeState.value = newTime
        onValueChange(newTime.toDisplayText())
    }

    fun applyMinute(newMinute: Int) {
        val newTime = timeState.value.withMinute(newMinute.coerceIn(0, 59))
        timeState.value = newTime
        onValueChange(newTime.toDisplayText())
    }

    fun stepHour(delta: Int) {
        val current = ((timeState.value.hour + 11) % 12) + 1
        applyHour12(current + delta)
    }

    fun stepMinute(delta: Int) {
        applyMinute(timeState.value.minute + delta)
    }

    fun toggleAmPm() {
        val newTime = timeState.value.plusHours(12)
        timeState.value = newTime
        onValueChange(newTime.toDisplayText())
    }

    Box(
        modifier = modifier
            .height(43.dp)
            .figmaDropShadow(cornerRadius = 10.dp, alpha = 0.12f, blurRadius = 7.8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TimeNumberField(
                value = hour12,
                onStep = ::stepHour,
                onDirectInput = ::applyHour12
            )
            Text(":", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D6D6D))
            TimeNumberField(
                value = time.minute,
                zeroPad = true,
                onStep = ::stepMinute,
                onDirectInput = ::applyMinute
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (isAm) "AM" else "PM",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                modifier = Modifier.clickable { toggleAmPm() }
            )
        }
    }
}

/** 세로 드래그(스크롤)로 값을 증감하거나, 탭해서 직접 입력할 수 있는 숫자 필드. */
@Composable
private fun TimeNumberField(
    value: Int,
    onStep: (Int) -> Unit,
    onDirectInput: (Int) -> Unit,
    zeroPad: Boolean = false
) {
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(value.toString()) }
    var hasFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    fun commit() {
        text.toIntOrNull()?.let(onDirectInput)
        isEditing = false
        hasFocused = false
    }

    if (isEditing) {
        BasicTextField(
            value = text,
            onValueChange = { new ->
                if (new.length <= 2 && new.all(Char::isDigit)) text = new
            },
            modifier = Modifier
                .width(20.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        hasFocused = true
                    } else if (hasFocused) {
                        commit()
                    }
                },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { commit() })
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(32.dp)
                .pointerInput(Unit) {
                    val step = 24f
                    awaitEachGesture {
                        var pointerId: PointerId? = null
                        var totalDrag = 0f
                        var dragAccumulator = 0f
                        while (true) {
                            val event = awaitPointerEvent()

                            // 눌린 상태 여부와 무관하게, 마우스 휠은 언제든 값 증감으로 처리한다.
                            if (event.type == PointerEventType.Scroll) {
                                val scrollY = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                                when {
                                    scrollY < 0f -> onStep(1)
                                    scrollY > 0f -> onStep(-1)
                                }
                                event.changes.forEach { it.consume() }
                                continue
                            }

                            val id = pointerId
                            val change = if (id == null) {
                                val newDown = event.changes.firstOrNull { it.changedToDown() } ?: continue
                                pointerId = newDown.id
                                newDown
                            } else {
                                event.changes.firstOrNull { it.id == id } ?: break
                            }

                            val dy = change.positionChange().y
                            if (dy != 0f) {
                                totalDrag += dy
                                if (kotlin.math.abs(totalDrag) > viewConfiguration.touchSlop) {
                                    change.consume()
                                    dragAccumulator += dy
                                    while (dragAccumulator <= -step) {
                                        onStep(1)
                                        dragAccumulator += step
                                    }
                                    while (dragAccumulator >= step) {
                                        onStep(-1)
                                        dragAccumulator -= step
                                    }
                                }
                            }
                            if (!change.pressed) {
                                if (kotlin.math.abs(totalDrag) <= viewConfiguration.touchSlop) {
                                    text = value.toString()
                                    isEditing = true
                                }
                                break
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (zeroPad) value.toString().padStart(2, '0') else value.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun NotificationDetailScreenEditPreview() {
    NotificationDetailScreen(
        item = mockNotifications[1],
        isNew = false,
        onBack = {},
    ) {}
}

@Preview(showSystemUi = true)
@Composable
private fun NotificationDetailScreenAddPreview() {
    NotificationDetailScreen(
        item = mockNotifications[1],
        isNew = true,
        onBack = {},
    ) {}
}