package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingSpec
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 팀 삭제 확인 화면. 모달이 아니라 전체 화면이다.
 *
 * 서버는 soft delete 로 처리하고 3일 뒤 실제로 지운다(scheduledDeletionAt).
 * 유저에게는 취소 수단이 없으므로 문구도 "복구할 수 없습니다" 로 간다.
 *
 * DELETE /api/teams/{team_id}
 */
@Composable
fun TeamDeleteScreen(
    settings: TeamSettings,
    onBack: () -> Unit,
    onDeleteTeam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var agreed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(20.dp))

        // 제목 없이 뒤로가기만 있는 상단바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = SettingSpec.ScreenPadding),
            contentAlignment = Alignment.CenterStart,
        ) {
            BackArrowIcon(
                size = SettingSpec.BackIconSize,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack,
                ),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SettingSpec.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(62.dp))

            Text(
                text = "정말 팀을 삭제할까요?",
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "삭제 결정 후, 3일 뒤 팀이 삭제됩니다",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(38.dp))

            TeamProfileCircle(
                imageUrl = settings.teamImageUrl,
                iconName = settings.iconName,
                iconColor = settings.iconColor,
                onClick = {},
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = settings.teamName,
                fontSize = 25.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "멤버 ${settings.memberCount}명",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
            )

            Spacer(Modifier.height(38.dp))

            Text(
                text = "팀에 저장된 모든 기록이 삭제됩니다\n" +
                        "팀을 삭제하면 복구할 수 없습니다\n" +
                        "팀의 모든 멤버에게 안내가 발송됩니다",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { agreed = !agreed },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (agreed) Color(0xFFF6989C) else Color.White)
                        .border(1.dp, Color(0xFFF6989C), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (agreed) CheckMarkIcon(modifier = Modifier.size(9.dp))
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "주의사항을 확인했으며\n[${settings.teamName}]을 삭제합니다",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D),
                )
            }

            Spacer(Modifier.height(48.dp))

            DeleteActionButton(
                label = "팀 삭제",
                enabled = agreed,
                onClick = onDeleteTeam,
            )

            Spacer(Modifier.height(10.dp))

            DeleteActionButton(
                label = "취소",
                enabled = true,
                onClick = onBack,
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

/**
 * 97 x 33 알약 버튼. 평소 #D9D9D9, 누르는 동안만 파란 그라데이션.
 */
@Composable
private fun DeleteActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val background = if (pressed && enabled) {
        Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFD9D9D9), Color(0xFFD9D9D9)))
    }

    Box(
        modifier = modifier
            .size(width = 97.dp, height = 33.dp)
            .clip(RoundedCornerShape(16.5.dp))
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true, name = "팀 삭제")
@Composable
private fun TeamDeleteScreenPreview() {
    PreviewContainer {
        TeamDeleteScreen(
            settings = mockTeamSettings,
            onBack = {},
            onDeleteTeam = {},
        )
    }
}