package com.solux.luxup.taptap.feature.notification.data

import androidx.compose.ui.graphics.Color
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.notification.model.NotificationItem

val mockNotifications: List<NotificationItem> = listOf(
    NotificationItem(
        id = 1L,
        title = "일기 쓰기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_bookmark,
        iconTint = Color(0xFFFFC760),
        scheduleText = "매일 · 오후 9:00",
        isEnabled = true
    ),
    NotificationItem(
        id = 2L,
        title = "물 마시기",
        category = "HEALTH",
        iconRes = R.drawable.bt_water_drop,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "매일 · 2시간 마다",
        isEnabled = true
    ),
    NotificationItem(
        id = 3L,
        title = "약 먹기",
        category = "HEALTH",
        iconRes = R.drawable.bt_medicine,
        iconTint = Color(0xFFFF7171),
        scheduleText = "매일 · 오전 10:00",
        isEnabled = true
    ),
    NotificationItem(
        id = 4L,
        title = "러닝",
        category = "HEALTH",
        iconRes = R.drawable.bt_dumbbell,
        iconTint = Color(0xFF4C9AFF),
        scheduleText = "화목토 · 오후 5:00",
        isEnabled = true
    ),
    NotificationItem(
        id = 5L,
        title = "적금 이체",
        category = "WORK",
        iconRes = R.drawable.bt_pay,
        iconTint = Color(0xFF4C9AFF),
        scheduleText = "매달 · 1일",
        isEnabled = true
    ),
    NotificationItem(
        id = 6L,
        title = "구독 프로그램 갱신",
        category = "WORK",
        iconRes = R.drawable.bt_labtop,
        iconTint = Color(0xFFDE76FB),
        scheduleText = "매달 · 16일",
        isEnabled = false
    ),
    NotificationItem(
        id = 7L,
        title = "병원 방문",
        category = "HEALTH",
        iconRes = R.drawable.bt_health,
        iconTint = Color(0xFFFF7171),
        scheduleText = "매주 월 · 오후 4:30",
        isEnabled = false
    ),
    NotificationItem(
        id = 8L,
        title = "가스불 확인",
        category = "ROUTINE",
        iconRes = R.drawable.bt_fire,
        iconTint = Color(0xFF707070),
        scheduleText = "오늘 · 오전 10:00",
        isEnabled = false
    ),
    NotificationItem(
        id = 9L,
        title = "식물 물주기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_water_drop,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "매일 · 오후 2:00",
        isEnabled = false
    ),
    NotificationItem(
        id = 6L,
        title = "구독 프로그램 갱신",
        category = "WORK",
        iconRes = R.drawable.bt_labtop,
        iconTint = Color(0xFFDE76FB),
        scheduleText = "매달 · 16일",
        isEnabled = false
    ),
    NotificationItem(
        id = 7L,
        title = "병원 방문",
        category = "HEALTH",
        iconRes = R.drawable.bt_health,
        iconTint = Color(0xFFFF7171),
        scheduleText = "매주 월 · 오후 4:30",
        isEnabled = false
    ),
    NotificationItem(
        id = 8L,
        title = "가스불 확인",
        category = "ROUTINE",
        iconRes = R.drawable.bt_fire,
        iconTint = Color(0xFF707070),
        scheduleText = "오늘 · 오전 10:00",
        isEnabled = false
    )
)

// "알림 추가" 모달에서 선택할 수 있는 버튼 후보 목록 (isEnabled는 모달의 초기 체크 상태로 사용)
val mockAddableNotifications: List<NotificationItem> = listOf(
    NotificationItem(
        id = 101L,
        title = "물 마시기",
        category = "HEALTH",
        iconRes = R.drawable.bt_water_drop,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 102L,
        title = "일기 쓰기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_bookmark,
        iconTint = Color(0xFFFFC760),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 103L,
        title = "러닝 하기",
        category = "HEALTH",
        iconRes = R.drawable.bt_dumbbell,
        iconTint = Color(0xFF4C9AFF),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 104L,
        title = "샤워 하기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_shower,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "",
        isEnabled = false
    ),
    NotificationItem(
        id = 105L,
        title = "구독 프로그램 갱신",
        category = "WORK",
        iconRes = R.drawable.bt_labtop,
        iconTint = Color(0xFFDE76FB),
        scheduleText = "",
        isEnabled = false
    ),
    NotificationItem(
        id = 101L,
        title = "물 마시기",
        category = "HEALTH",
        iconRes = R.drawable.bt_water_drop,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 102L,
        title = "일기 쓰기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_bookmark,
        iconTint = Color(0xFFFFC760),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 103L,
        title = "러닝 하기",
        category = "HEALTH",
        iconRes = R.drawable.bt_dumbbell,
        iconTint = Color(0xFF4C9AFF),
        scheduleText = "",
        isEnabled = true
    ),
    NotificationItem(
        id = 104L,
        title = "샤워 하기",
        category = "ROUTINE",
        iconRes = R.drawable.bt_shower,
        iconTint = Color(0xFF63C7FF),
        scheduleText = "",
        isEnabled = false
    ),
    NotificationItem(
        id = 105L,
        title = "구독 프로그램 갱신",
        category = "WORK",
        iconRes = R.drawable.bt_labtop,
        iconTint = Color(0xFFDE76FB),
        scheduleText = "",
        isEnabled = false
    )
)