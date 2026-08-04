package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.solux.luxup.taptap.feature.auth.account.util.SettingsOptionPopup

private val DividerColor = Color(0xFFB1B1B1)
private val ScreenPadding = 40.dp

private val soundOptionLabels = NotificationSoundOption.entries.map { it.shortLabel }

/**
 * 설정 (하단 탭)
 * 계정 정보 진입 + 알림 on/off, 알림 켜졌을 때 소리/오버레이 표시 옵션 노출
 */
@Composable
fun AccountSettingsScreen(
    user: AccountUser = MockAccountUser,
    notificationSettings: NotificationSettings = MockNotificationSettings,
    onNavigateToAccountInfo: () -> Unit = {},
    onEditProfileImage: () -> Unit = {},
    onSaveNickname: (String) -> Unit = {},
    onNavItemSelected: (BottomNavItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var settings by remember(notificationSettings) { mutableStateOf(notificationSettings) }

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
                onEditProfileClick = onEditProfileImage,
                onSaveNickname = onSaveNickname,
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
                    NotificationSwitch(
                        checked = settings.enabled,
                        onCheckedChange = {
                            settings = settings.copy(enabled = it)
                            // TODO: 알림 on/off API 연동
                        },
                    )
                },
            )

            if (settings.enabled) {
                NotificationOptionRow(
                    label = "소리 설정",
                    value = settings.soundOption.label,
                    options = soundOptionLabels,
                    onOptionSelected = { index ->
                        settings = settings.copy(soundOption = NotificationSoundOption.entries[index])
                        // TODO: 알림 소리 설정 API 연동
                    },
                )
                NotificationToggleRow(
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
private fun NotificationSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackWidth = 52.dp
    val trackHeight = 32.dp
    val edgeInset = 4.dp

    val thumbSize by animateDpAsState(if (checked) 24.dp else 22.dp, label = "thumbSize")
    val thumbOffset by animateDpAsState(
        if (checked) trackWidth - thumbSize - edgeInset else edgeInset,
        label = "thumbOffset",
    )
    val trackColor by animateColorAsState(
        if (checked) Color(0xFF2085FF) else Color(0xFFE2E2E2),
        label = "trackColor",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(1.dp, CircleShape)
                .background(Color.White, CircleShape),
        )
    }
}

@Composable
private fun NotificationOptionRow(
    label: String,
    value: String,
    options: List<String>,
    onOptionSelected: (index: Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
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
        SettingsOptionPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            options = options,
            onSelect = onOptionSelected,
        )
    }
}

@Composable
private fun NotificationToggleRow(
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