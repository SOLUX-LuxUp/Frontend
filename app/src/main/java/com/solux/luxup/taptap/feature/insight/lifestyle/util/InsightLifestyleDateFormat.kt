package com.solux.luxup.taptap.feature.insight.lifestyle.util

import java.time.LocalDateTime

/** ISO 문자열(lastRecordedAt) → "4월 3일" */
fun String.toMonthDayText(): String =
    try {
        val date = LocalDateTime.parse(this)
        "${date.monthValue}월 ${date.dayOfMonth}일"
    } catch (_: Exception) {
        this
    }