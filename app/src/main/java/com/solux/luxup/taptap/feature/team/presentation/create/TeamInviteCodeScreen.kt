package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockTeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle

/**
 * 팀 생성 직후 초대코드를 공유하는 화면.
 * 코드는 POST /api/teams 응답의 inviteCode를 그대로 노출한다.
 *
 * 우측 상단 확인(✓) → 템플릿 선택 화면으로 이동
 * 공유하기 → ACTION_SEND chooser (호출부에서 처리)
 */
@Composable
fun TeamInviteCodeScreen(
    team: TeamCreateResult,
    onShareClick: (String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ConfirmCheckIcon(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onConfirmClick,
                    ),
            )
        }

        Spacer(Modifier.height(119.5.dp))

        TeamProfileCircle(
            imageUrl = team.teamImageUrl,
            iconName = team.iconName,
            iconColor = team.iconColor,
        )

        Spacer(Modifier.height(29.dp))

        Text(
            text = team.teamName,
            fontSize = 22.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(51.dp))

        Text(
            text = "팀 코드",
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )

        Spacer(Modifier.height(17.dp))

        Box(
            modifier = Modifier
                .size(width = 204.dp, height = 56.dp)
                .figmaDropShadow(cornerRadius = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEFEFE)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = team.inviteCode,
                fontSize = 25.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB1B1B1),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(width = 166.dp, height = 45.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onShareClick(team.inviteCode) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "공유하기",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TeamInviteCodeScreenPreview() {
    PreviewContainer {
        TeamInviteCodeScreen(
            team = mockTeamCreateResult,
            onShareClick = {},
            onConfirmClick = {},
        )
    }
}