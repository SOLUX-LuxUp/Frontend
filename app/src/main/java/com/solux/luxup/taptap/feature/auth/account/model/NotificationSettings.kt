package com.solux.luxup.taptap.feature.auth.account.model

enum class NotificationSoundOption(val label: String, val shortLabel: String) {
    SILENT("무음으로 받기", "무음"),
    VIBRATE("진동으로 받기", "진동"),
    SOUND("소리로 받기", "소리"),
}

data class NotificationSettings(
    val enabled: Boolean,
    val soundOption: NotificationSoundOption,
    val showOverOtherApps: Boolean,
)