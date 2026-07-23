package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.auth.account.data.MockAccountUser
import com.solux.luxup.taptap.feature.auth.account.data.MockNotificationSettings
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser
import com.solux.luxup.taptap.feature.auth.account.model.NotificationSettings
import com.solux.luxup.taptap.feature.auth.account.model.NotificationSoundOption
import com.solux.luxup.taptap.feature.auth.account.presentation.components.AccountProfileHeader
import com.solux.luxup.taptap.feature.auth.account.presentation.components.SettingsRow

private val DividerColor = Color(0xFFB1B1B1)
private val ScreenPadding = 40.dp

/**
 * 설정 (하단 탭)
 * 계정 정보 진입 + 알림 on/off, 알림 켜졌을 때 소리/오버레이 표시 옵션 노출
 */
@Composable
fun AccountSettingsScreen(
    user: AccountUser = MockAccountUser,
    notificationSettings: NotificationSettings = MockNotificationSettings,
    onNavigateToAccountInfo: () -> Unit = {},
    onNavigateToProfileEdit: () -> Unit = {},
    onNavItemSelected: (BottomNavItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var settings by remember(notificationSettings) { mutableStateOf(notificationSettings) }

    fun cycleSoundOption() {
        val options = NotificationSoundOption.entries
        val next = options[(options.indexOf(settings.soundOption) + 1) % options.size]
        settings = settings.copy(soundOption = next)
        // TODO: 알림 소리 설정 API 연동
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        bottomBar = {
            BottomNavBar(selected = BottomNavItem.SETTINGS, onItemSelected = onNavItemSelected)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(18.dp))
            Text(
                text = "설정",
                fontFamily = Pretendard,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(50.dp))

            AccountProfileHeader(
                user = user,
                onEditProfileClick = onNavigateToProfileEdit,
            )
            Spacer(Modifier.height(30.dp))

            SettingsRow(
                title = "계정 정보",
                leadingIcon = R.drawable.ic_information,
                onClick = onNavigateToAccountInfo,
            )
            HorizontalDivider(color = DividerColor)

            SettingsRow(
                title = "알림",
                leadingIcon = R.drawable.ic_bell,
                trailing = {
                    Switch(
                        checked = settings.enabled,
                        onCheckedChange = {
                            settings = settings.copy(enabled = it)
                            // TODO: 알림 on/off API 연동
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF2085FF)),
                    )
                },
            )

            if (settings.enabled) {
                NotificationOptionRow(
                    label = "소리 설정",
                    value = settings.soundOption.label,
                    onClick = ::cycleSoundOption,
                )
                NotificationOptionRow(
                    label = "다른 화면 위에 표시",
                    value = if (settings.showOverOtherApps) "허용" else "비허용",
                    onClick = {
                        settings = settings.copy(showOverOtherApps = !settings.showOverOtherApps)
                        // TODO: 오버레이 권한 설정 연동
                    },
                )
            }
            HorizontalDivider(color = DividerColor)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotificationOptionRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontFamily = Pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )
        Text(
            text = value,
            fontFamily = Pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AccountSettingsScreenPreview() {
    AccountSettingsScreen()
}