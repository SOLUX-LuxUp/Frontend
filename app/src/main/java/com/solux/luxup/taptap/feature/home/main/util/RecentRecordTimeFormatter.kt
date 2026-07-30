package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

/** 서버가 내려주는 기록 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "지금"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

fun nowInServiceZone(): LocalDateTime = LocalDateTime.now(ServiceZone)

/**
 * 서버는 오프셋 없는 LocalDateTime 문자열("2026-07-30T10:49:31[.123456]")을 내려준다.
 * 예전에는 'Z'가 붙은 UTC 형식을 기대해 파싱이 항상 실패했었다 — 팀 쪽(TeamRecentRecordBanner)과
 * 동일하게 LocalDateTime.parse로 맞춘다.
 */
private fun parseRecordedAt(isoTimestamp: String): LocalDateTime? =
    runCatching { LocalDateTime.parse(isoTimestamp) }.getOrNull()

fun formatElapsedText(isoTimestamp: String, now: LocalDateTime = nowInServiceZone()): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    val rawMinutes = Duration.between(recordedAt, now).toMinutes()
    // 초 단위 클럭 오차로 방금 남긴 기록이 잠깐 "미래"처럼 보이는 정도는 0분으로 눙친다.
    // 그보다 큰 음수(시간 단위)라면 클럭 오차가 아니라 타임존이 어긋난 것 — 값을 숨겨 실제 문제가 드러나게 한다.
    if (rawMinutes < -5) return ""
    val minutes = rawMinutes.coerceAtLeast(0)
    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        minutes < 60 * 24 -> "${minutes / 60}시간 전"
        else -> "${minutes / (60 * 24)}일 전"
    }
}

/** "AM 9:58" */
fun formatRecordedAtText(isoTimestamp: String): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    val hour24 = recordedAt.hour
    val marker = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        hour24 % 12 == 0 -> 12
        else -> hour24 % 12
    }
    return "%s %d:%02d".format(marker, hour12, recordedAt.minute)
}

/**
 * "N분 전" 같은 경과 시간 텍스트는 기록 시점에 한 번 계산되면, 다른 상태 변화가 없는 한
 * Compose가 다시 그릴 이유가 없어 시간이 흘러도 화면에 그대로 멈춰 있는다.
 * 일정 주기로 현재 시각을 다시 읽어 recomposition을 유발해 이 텍스트들이 계속 갱신되게 한다.
 */
@Composable
fun rememberTickingNow(intervalMillis: Long = 30_000L): State<LocalDateTime> =
    produceState(initialValue = nowInServiceZone()) {
        while (true) {
            delay(intervalMillis)
            value = nowInServiceZone()
        }
    }