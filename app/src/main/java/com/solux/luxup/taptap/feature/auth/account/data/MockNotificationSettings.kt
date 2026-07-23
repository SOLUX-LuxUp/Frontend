package com.solux.luxup.taptap.feature.auth.account.data

import com.solux.luxup.taptap.feature.auth.account.model.NotificationSettings
import com.solux.luxup.taptap.feature.auth.account.model.NotificationSoundOption

val MockNotificationSettings = NotificationSettings(
    enabled = true,
    soundOption = NotificationSoundOption.SILENT,
    showOverOtherApps = true,
)