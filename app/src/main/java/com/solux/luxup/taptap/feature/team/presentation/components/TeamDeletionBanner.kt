package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

private val BannerColor = Color(0xCCFFACAF)
private val BannerTextColor = Color(0xFFEE3A3A)

/**
 * 팀 삭제 유예 배너 — Persistent Overlay.
 * X 누르면 이번 화면 진입 세션에서만 숨기고, 팀 재진입시 다시 노출된다.
 *
 * scheduledDeletionAt: "yyyy-MM-ddTHH:mm:ss" 형식 (settings/팀 목록 응답 필드 그대로)
 */
@Composable
fun TeamDeletionBanner(
    scheduledDeletionAt: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dismissed by remember { mutableStateOf(false) }
    if (dismissed) return

    val message = remember(scheduledDeletionAt) {
        remainingTimeMessage(scheduledDeletionAt)
    } ?: return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(BannerColor)
            .padding(start = 25.dp, end = 29.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_information),
            contentDescription = null,
            colorFilter = ColorFilter.tint(BannerTextColor),
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            letterSpacing = 0.sp,
            color = BannerTextColor,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_x),
            contentDescription = "닫기",
            colorFilter = ColorFilter.tint(BannerTextColor),
            modifier = Modifier
                .size(20.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDismiss() },          // ← 변경
        )
    }
}

/** 24시간 이상이면 "N일 뒤", 미만이면 "N시간 뒤" — 임계값 가정, 시안 확인 필요 */
private fun remainingTimeMessage(scheduledDeletionAt: String): String? {
    val target = runCatching { LocalDateTime.parse(scheduledDeletionAt) }.getOrNull() ?: return null
    val duration = Duration.between(LocalDateTime.now(), target)
    if (duration.isNegative) return null

    val totalHours = duration.toHours()
    return if (totalHours >= 24) {
        val days = ceil(totalHours / 24.0).toInt()
        "${days}일 뒤 팀이 삭제됩니다."
    } else {
        val hours = ceil(duration.toMinutes() / 60.0).toInt().coerceAtLeast(1)
        "${hours}시간 뒤 팀이 삭제됩니다."
    }
}