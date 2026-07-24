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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockTeamCreateResult
import com.solux.luxup.taptap.feature.team.data.mockTeamTemplates
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamTemplate
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle

private val ScreenPadding = 36.dp

/**
 * 팀의 방향을 정하는 화면. (8.0.5)
 *
 * 카드 선택 → POST /api/teams/{team_id}/template (팀당 1회, 재선택 불가)
 * 건너뛰기 → POST /api/teams/{team_id}/template/skip (백엔드 추가 예정)
 *
 * 선택 즉시 카테고리만 프리셋 생성되고 버튼은 생성되지 않는다.
 * 버튼은 활동 탭의 추천 리스트에서 개별 생성한다.
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
            .padding(horizontal = ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(131.dp))

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

        Spacer(Modifier.height(52.dp))

        Text(
            text = "무엇을 함께 기억하고 싶나요?",
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(34.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            templates.forEach { template ->
                TemplateCard(
                    template = template,
                    isSelected = template.templateId == selectedTemplateId,
                    onClick = { onTemplateClick(template) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "건너뛰기",
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB1B1B1),
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
            .height(124.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0xFFDEEFFF))
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF2085FF), RoundedCornerShape(17.dp))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(20.dp),
    ) {
        Column {
            Text(
                text = template.templateName,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = template.description,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
            )
        }

        Text(
            text = template.subDescription,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 844)
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

@Preview(showBackground = true, widthDp = 402, heightDp = 844)
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