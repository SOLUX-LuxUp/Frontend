package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon
import com.solux.luxup.taptap.feature.team.data.mockTeamCreateResult
import com.solux.luxup.taptap.feature.team.data.mockTeamTemplates
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamTemplate

/**
 * 팀의 방향을 정하는 화면.
 *
 * 카드 선택 → POST /api/teams/{team_id}/template (팀당 1회, 재선택 불가)
 * 건너뛰기 → 호출 없이 활동 탭으로 이동
 *
 * 선택 즉시 카테고리만 프리셋 생성되고 버튼은 생성되지 않는다.
 * 버튼은 활동 탭의 추천 리스트에서 개별 생성.
 *
 * 색상·치수는 사용처에 직접 기입. Figma 값 확정 시 해당 위치에서 수정.
 */
@Composable
fun TeamTemplateScreen(
    team: TeamCreateResult,
    templates: List<TeamTemplate>,
    onTemplateClick: (TeamTemplate) -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedTemplateId: Long? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        TemplateTeamProfile(
            imageUrl = team.teamImageUrl,
            iconName = team.iconName,
            iconColor = team.iconColor,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = team.teamName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "무엇을 함께 기억하고 싶나요?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF616161),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            templates.forEach { template ->
                TemplateCard(
                    template = template,
                    isSelected = template.templateId == selectedTemplateId,
                    onClick = { onTemplateClick(template) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "건너뛰기",
            fontSize = 13.sp,
            color = Color(0xFFBDBDBD),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSkipClick,
            ),
        )
    }
}

@Composable
private fun TemplateCard(
    template: TeamTemplate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEAF4FF))
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF2085FF), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Column {
            Text(
                text = template.templateName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2085FF),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                fontSize = 11.sp,
                color = Color(0xFF7A7A7A),
            )
        }

        Text(
            text = template.subDescription,
            fontSize = 10.sp,
            color = Color(0xFF9E9E9E),
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun TemplateTeamProfile(
    imageUrl: String?,
    iconName: String?,
    iconColor: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0xFFEDEDED), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when {
            imageUrl != null -> AsyncImage(
                model = imageUrl,
                contentDescription = "팀 이미지",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            iconName != null -> Icon(
                painter = painterResource(TeamIcon.from(iconName).resId),
                contentDescription = null,
                tint = IconColor.from(iconColor).color,
                modifier = Modifier.size(40.dp),
            )

            else -> Unit
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TeamTemplateScreenPreview() {
    PreviewContainer {
        TeamTemplateScreen(
            team = mockTeamCreateResult,
            templates = mockTeamTemplates,
            onTemplateClick = {},
            onSkipClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TeamTemplateScreenSelectedPreview() {
    PreviewContainer {
        TeamTemplateScreen(
            team = mockTeamCreateResult,
            templates = mockTeamTemplates,
            onTemplateClick = {},
            onSkipClick = {},
            selectedTemplateId = 1,
        )
    }
}