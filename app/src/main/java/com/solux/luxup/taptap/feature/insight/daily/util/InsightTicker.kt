package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId

/** 서버가 내려주는 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "지금"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

/**
 * 화면이 떠 있는 동안 [intervalMillis]마다 갱신되는 현재 시각(한국 시간 기준).
 * [String.toElapsedText]에 그냥 기본값(호출 시점 1회)을 넘기면 화면을 계속 띄워놔도
 * "방금 전"에 멈춰있으므로, 타임라인처럼 오래 떠 있는 화면에서 이 값을 넘겨 시간이 흐르며 갱신되게 한다.
 */
@Composable
fun rememberTickingNow(intervalMillis: Long = 30_000L): LocalDateTime {
    var now by remember { mutableStateOf(LocalDateTime.now(ServiceZone)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis)
            now = LocalDateTime.now(ServiceZone)
        }
    }
    return now
}