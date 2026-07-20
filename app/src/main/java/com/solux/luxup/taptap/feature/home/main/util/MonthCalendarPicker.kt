package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.home.main.presentation.dropShadow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val weekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")
private val monthTitleFormat = SimpleDateFormat("yyyy년 M월", Locale.KOREAN)

// 피그마 "Frame 93" 캘린더 UI - 기한설정 날짜 선택에 쓰는 커스텀 월간 달력
@Composable
fun MonthCalendarPicker(
    selectedDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialCalendar = remember {
        Calendar.getInstance().apply {
            selectedDateMillis?.let { timeInMillis = it }
        }
    }
    var displayedYear by remember { mutableIntStateOf(initialCalendar.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(initialCalendar.get(Calendar.MONTH)) }

    val selectedCalendar = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis -> Calendar.getInstance().apply { timeInMillis = millis } }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(cornerRadius = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(30.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 달",
                tint = Color(0xFF727272),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clickable {
                        if (displayedMonth == Calendar.JANUARY) {
                            displayedMonth = Calendar.DECEMBER
                            displayedYear -= 1
                        } else {
                            displayedMonth -= 1
                        }
                    }
            )
            Text(
                text = monthTitleFormat.format(
                    Calendar.getInstance().apply {
                        clear()
                        set(displayedYear, displayedMonth, 1)
                    }.time
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF727272),
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 달",
                tint = Color(0xFF727272),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .clickable {
                        if (displayedMonth == Calendar.DECEMBER) {
                            displayedMonth = Calendar.JANUARY
                            displayedYear += 1
                        } else {
                            displayedMonth += 1
                        }
                    }
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val monthCalendar = remember(displayedYear, displayedMonth) {
            Calendar.getInstance().apply {
                clear()
                set(displayedYear, displayedMonth, 1)
            }
        }
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val leadingBlanks = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val cells: List<Int?> = List(leadingBlanks) { null } + (1..daysInMonth).toList()
        val weeks = cells.chunked(7)

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            val isSelected = selectedCalendar != null &&
                                selectedCalendar.get(Calendar.YEAR) == displayedYear &&
                                selectedCalendar.get(Calendar.MONTH) == displayedMonth &&
                                selectedCalendar.get(Calendar.DAY_OF_MONTH) == day

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF2085FF) else Color.Transparent)
                                    .clickable {
                                        val picked = Calendar.getInstance().apply {
                                            clear()
                                            set(displayedYear, displayedMonth, day)
                                        }
                                        onDateSelected(picked.timeInMillis)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF6D6D6D)
                                )
                            }
                        }
                    }
                }
                repeat(7 - week.size) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}