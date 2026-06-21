package com.solux.luxup.taptap.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(val label: String, val icon: ImageVector) {
    TEAM("팀", Icons.Default.Group),
    NOTIFICATION("알림", Icons.Default.Notifications),
    HOME("홈", Icons.Default.Home),
    RECORD("기록", Icons.AutoMirrored.Filled.List),
    SETTINGS("설정", Icons.Default.Settings)
}