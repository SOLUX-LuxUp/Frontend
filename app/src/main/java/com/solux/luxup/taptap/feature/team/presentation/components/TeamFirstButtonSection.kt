package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockSuggestionsTogether
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import com.solux.luxup.taptap.R

/**
 * 버튼을 빠르게 만들도록 돕는 유도 섹션.
 *
 * 두 상황에서 노출된다.
 *  - 아직 버튼이 하나도 없는 팀 → "첫 번째 버튼을 만들어보세요" (활동 탭 진입 시 자동)
 *  - 버튼이 있는 팀에서 "+" → 빠르게 만들기 → "버튼을 빠르게 만들어보세요" (닫기 버튼 노출)
 *
 * 템플릿을 선택한 팀만 추천 리스트가 채워지며, 카테고리 탭으로 분류해 보여준다.
 * 추천 항목 탭 → POST /api/teams/{team_id}/buttons 로 개별 생성.
 * 생성된 항목은 다음 조회부터 목록에서 자동 제외된다.
 *
 * @param isFirstButton 버튼이 아직 없는 팀이면 true. 헤더 문구를 결정한다
 * @param onClose null이 아니면 우측 상단에 닫기 버튼을 노출한다 (빠르게 만들기 모드)
 */
@Composable
fun TeamFirstButtonSection(
    suggestions: List<TeamButtonSuggestion>,
    onSuggestionClick: (TeamButtonSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    isFirstButton: Boolean = true,
    onClose: (() -> Unit)? = null,
) {
    SectionCard(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isFirstButton) "첫 번째 버튼을 만들어보세요"
                else "버튼을 빠르게 만들어보세요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
                modifier = Modifier.align(Alignment.Center),
            )

            if (onClose != null) {
                Icon(
                    painter = painterResource(R.drawable.ic_x),
                    contentDescription = "닫기",
                    tint = Color(0xFFB1B1B1),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClose,
                        ),
                )
            }
        }

        if (suggestions.isEmpty()) return@SectionCard

        Spacer(modifier = Modifier.height(16.dp))

        // 카테고리 탭. categoryName이 null인 프리셋은 "기타"로 묶는다.
        // TODO: No Category 탭 라벨 확정 필요
        val categories = remember(suggestions) {
            suggestions.map { it.categoryName ?: NO_CATEGORY_LABEL }.distinct()
        }
        var selectedCategory by remember(categories) {
            mutableStateOf(categories.firstOrNull().orEmpty())
        }

        // 탭은 최대 4개(가족/친구/연인/기타)라 가로 스크롤 없이 가운데 정렬
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            categories.forEach { category ->
                CategoryTab(
                    label = category,
                    isSelected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filtered = remember(suggestions, selectedCategory) {
            suggestions.filter {
                (it.categoryName ?: NO_CATEGORY_LABEL) == selectedCategory
            }
        }

        // 추천 버튼만 감싸는 내부 스크롤 영역
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(filtered) { suggestion ->
                SuggestionRow(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion) },
                )
            }
        }
    }
}

@Composable
private fun CategoryTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFF2085FF) else Color(0xFFF2F2F2))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF757575),
        )
    }
}

@Composable
private fun SuggestionRow(
    suggestion: TeamButtonSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFDEEFFF))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(ButtonIcons.resOf(suggestion.iconName)),
            contentDescription = null,
            tint = IconColor.from(suggestion.iconColor).color,
            modifier = Modifier.size(16.dp),
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = suggestion.buttonName,
            fontSize = 12.sp,
            color = Color(0xFF1A1A1A),
        )
    }
}

private const val NO_CATEGORY_LABEL = "기타"

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun TeamFirstButtonSectionSkipPreview() {
    PreviewContainer {
        TeamFirstButtonSection(
            suggestions = emptyList(),
            onSuggestionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun TeamFirstButtonSectionTemplatePreview() {
    PreviewContainer {
        TeamFirstButtonSection(
            suggestions = mockSuggestionsTogether,
            onSuggestionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}