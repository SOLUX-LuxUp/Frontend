package com.solux.luxup.taptap.feature.auth.account.model

/**
 * 서버는 soundEnabled/vibrationEnabled 를 독립된 boolean 두 개로 관리하지만
 * 화면은 무음/진동/소리 중 하나를 고르는 3지선다 드롭다운이라 상호 배타적으로 매핑한다.
 */
enum class NotificationSoundOption(
    val label: String,
    val shortLabel: String,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
) {
    SILENT("무음으로 받기", "무음", soundEnabled = false, vibrationEnabled = false),
    VIBRATE("진동으로 받기", "진동", soundEnabled = false, vibrationEnabled = true),
    SOUND("소리로 받기", "소리", soundEnabled = true, vibrationEnabled = false),
    ;

    companion object {
        fun fromFlags(soundEnabled: Boolean, vibrationEnabled: Boolean): NotificationSoundOption = when {
            soundEnabled -> SOUND
            vibrationEnabled -> VIBRATE
            else -> SILENT
        }
    }
}

data class NotificationSettings(
    val enabled: Boolean,
    val soundOption: NotificationSoundOption,
    val showOverOtherApps: Boolean,
)