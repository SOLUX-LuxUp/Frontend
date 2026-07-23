package com.solux.luxup.taptap.feature.auth.account.model

enum class NotificationSoundOption(val label: String) {
    SILENT("무음으로 받기"),
    VIBRATE("진동으로 받기"),
    SOUND("소리로 받기"),
}

data class NotificationSettings(
    val enabled: Boolean,
    val soundOption: NotificationSoundOption,
    val showOverOtherApps: Boolean,
)