package com.solux.luxup.taptap.feature.notification.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val LabelGray = Color(0xFF6D6D6D)
private val SelectedGradient = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd))

/** 선택된 날짜(1~31)를 연속 구간은 "20~25일", 단일 날짜는 "15일" 형태로 묶어 요약한다. */
fun formatMonthDaySummary(days: Set<Int>): String {
    if (days.isEmpty()) return ""
    val sorted = days.sorted()
    val segments = mutableListOf<String>()
    var start = sorted.first()
    var prev = start
    for (day in sorted.drop(1)) {
        if (day == prev + 1) {
            prev = day
        } else {
            segments += if (start == prev) "${start}일" else "${start}~${prev}일"
            start = day
            prev = day
        }
    }
    segments += if (start == prev) "${start}일" else "${start}~${prev}일"
    return segments.joinToString("    ")
}

/** "매달" 반복 시 몇 일에 알림을 울릴지 1~31 중에서 다중 선택하는 팝업. */
@Composable
fun MonthDayPickerDialog(
    selectedDays: Set<Int>,
    onDaysChange: (Set<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFFFFF))
                .padding(30.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "날짜 선택",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..31).chunked(7).forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowDays.forEach { day ->
                                val selected = day in selectedDays
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .then(
                                            if (selected) {
                                                Modifier.background(SelectedGradient)
                                            } else {
                                                Modifier.background(Color(0xFFFFFFFF))
                                            }
                                        )
                                        .clickable {
                                            onDaysChange(
                                                if (selected) selectedDays - day else selectedDays + day
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else LabelGray
                                    )
                                }
                            }
                            repeat(7 - rowDays.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(43.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SelectedGradient)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}