package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.components.ChevronRightIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingRow
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingValueText

private val ScreenPadding = 40.dp

/**
 * 팀 관리 화면. 팀 설정 → 팀 관리 로 진입하며 팀장만 접근한다.
 *
 * 팀원 관리 · 팀 권한 관리 · 팀장 위임 · 팀 삭제하기
 */
@Composable
fun TeamManageScreen(
    settings: TeamSettings,
    onBack: () -> Unit,
    onMemberManageClick: () -> Unit,
    onPermissionClick: () -> Unit,
    onDelegateClick: () -> Unit,
    onDeleteTeamClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingTopBar(
            title = "팀 관리",
            onBack = onBack,
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )

        Column(modifier = Modifier.padding(horizontal = ScreenPadding)) {
            Spacer(Modifier.height(24.dp))

            // 시안에는 화살표 없이 "최대 N명" 만 있지만, 팀원 관리 페이지로 이동한다.
            SettingRow(
                label = "팀원 관리",
                trailing = { SettingValueText("최대 ${settings.maxMember}명") },
                onClick = onMemberManageClick,
            )

            SettingRow(
                label = "팀 권한 관리",
                trailing = { ChevronRightIcon() },
                onClick = onPermissionClick,
            )

            SettingRow(
                label = "팀장 위임",
                trailing = { ChevronRightIcon() },
                onClick = onDelegateClick,
            )

            SettingRow(
                label = "팀 삭제하기",
                trailing = { ChevronRightIcon(tint = Color(0xFFFF9B9B)) },
                labelColor = Color(0xFFFF6B6B),
                onClick = onDeleteTeamClick,
            )
        }
    }
}

@Preview(showBackground = true, name = "팀 관리")
@Composable
private fun TeamManageScreenPreview() {
    PreviewContainer {
        TeamManageScreen(
            settings = mockTeamSettings,
            onBack = {},
            onMemberManageClick = {},
            onPermissionClick = {},
            onDelegateClick = {},
            onDeleteTeamClick = {},
        )
    }
}