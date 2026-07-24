package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingRow
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingValueText
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamPermissionSelectModal

private val ScreenPadding = 40.dp

/** 어떤 권한을 수정 중인지 */
enum class TeamPermissionTarget {
    CREATE,
    EDIT,
    DELETE,
}

/**
 * 팀 권한 관리 화면. 팀 관리 → 팀 권한 관리 로 진입하며 팀장만 접근한다.
 *
 * 세 항목 모두 PATCH /api/teams/{team_id}/settings 하나로 갱신한다.
 */
@Composable
fun TeamPermissionScreen(
    settings: TeamSettings,
    onBack: () -> Unit,
    onPermissionChange: (TeamPermissionTarget, TeamButtonPermission) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingTarget by remember { mutableStateOf<TeamPermissionTarget?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingTopBar(
            title = "팀 권한 관리",
            onBack = onBack,
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )

        Column(modifier = Modifier.padding(horizontal = ScreenPadding)) {
            Spacer(Modifier.height(24.dp))

            SettingRow(
                label = "버튼 생성",
                trailing = { SettingValueText(settings.buttonCreatePermission.label) },
                onClick = { editingTarget = TeamPermissionTarget.CREATE },
            )

            SettingRow(
                label = "버튼 수정",
                trailing = { SettingValueText(settings.buttonEditPermission.label) },
                onClick = { editingTarget = TeamPermissionTarget.EDIT },
            )

            SettingRow(
                label = "버튼 삭제",
                trailing = { SettingValueText(settings.buttonDeletePermission.label) },
                onClick = { editingTarget = TeamPermissionTarget.DELETE },
            )
        }
    }

    editingTarget?.let { target ->
        val options = when (target) {
            TeamPermissionTarget.CREATE -> TeamButtonPermission.createOptions
            else -> TeamButtonPermission.editOptions
        }
        val selected = when (target) {
            TeamPermissionTarget.CREATE -> settings.buttonCreatePermission
            TeamPermissionTarget.EDIT -> settings.buttonEditPermission
            TeamPermissionTarget.DELETE -> settings.buttonDeletePermission
        }

        TeamPermissionSelectModal(
            options = options,
            selected = selected,
            onSelect = { permission ->
                editingTarget = null
                onPermissionChange(target, permission)
            },
            onDismiss = { editingTarget = null },
        )
    }
}

@Preview(showBackground = true, name = "팀 권한 관리")
@Composable
private fun TeamPermissionScreenPreview() {
    PreviewContainer {
        TeamPermissionScreen(
            settings = mockTeamSettings,
            onBack = {},
            onPermissionChange = { _, _ -> },
        )
    }
}