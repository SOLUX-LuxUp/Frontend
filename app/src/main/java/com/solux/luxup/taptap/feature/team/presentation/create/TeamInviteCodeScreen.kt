package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon
import com.solux.luxup.taptap.feature.team.data.mockTeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle

/**
 * 팀 생성 직후 초대코드를 공유하는 화면.
 * 코드는 POST /api/teams 응답의 inviteCode를 그대로 노출한다.
 *
 * 우측 상단 확인(✓) → 템플릿 선택 화면으로 이동
 *
 * 색상·치수는 사용처에 직접 기입. Figma 값 확정 시 해당 위치에서 수정.
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ConfirmCheckIcon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .clickable(onClick = onConfirmClick),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            TeamProfileCircle(
                imageUrl = team.teamImageUrl,
                iconName = team.iconName,
                iconColor = team.iconColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = team.teamName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "팀 코드",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F7F7))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = team.inviteCode,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 720)
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