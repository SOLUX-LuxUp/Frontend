package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.home.main.data.TEMPLATE_FIRST_BUTTON_TITLE
import com.solux.luxup.taptap.feature.home.template.data.mockTemplateSuggestionsMemory
import com.solux.luxup.taptap.feature.home.template.model.TemplateButtonSuggestion

/**
 * 홈 화면의 "첫 번째 버튼을 만들어보세요" 유도 섹션.
 *
 * 템플릿을 건너뛴 경우(suggestions가 비어있음) 제목만 노출한다.
 * - 템플릿을 골랐을 때(groupByCategory = true): 그 템플릿의 카테고리 탭으로 나눠 보여준다.
 * - 건너뛰었을 때(groupByCategory = false): 모든 템플릿의 추천을 카테고리 구분 없이 한 목록으로 보여준다.
 * 두 경우 모두 목록 높이를 5개 행 정도로 제한해 나머지는 스크롤로 훑어보게 한다.
 * 추천 항목 탭 → POST /api/templates/{template_id}/apply (selectedPresetIds = [presetId]) 로 개별 생성.
 */
@Composable
fun HomeFirstButtonSection(
    suggestions: List<TemplateButtonSuggestion>,
    onSuggestionClick: (TemplateButtonSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    groupByCategory: Boolean = true,
) {
    SectionCard(
        modifier = modifier,
        cornerRadius = 13.dp,
        contentPadding = PaddingValues(20.dp),
    ) {
        Text(
            text = TEMPLATE_FIRST_BUTTON_TITLE,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        if (suggestions.isEmpty()) return@SectionCard

        Spacer(modifier = Modifier.height(20.dp))

        val visibleSuggestions = if (groupByCategory) {
            val categories = remember(suggestions) {
                suggestions.map { it.categoryName }.distinct()
            }
            var selectedCategory by remember(categories) {
                mutableStateOf(categories.firstOrNull().orEmpty())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
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

            Spacer(modifier = Modifier.height(15.dp))

            remember(suggestions, selectedCategory) {
                suggestions.filter { it.categoryName == selectedCategory }
            }
        } else {
            suggestions
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(visibleSuggestions) { suggestion ->
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
            .clip(RoundedCornerShape(30.dp))
            .background(if (isSelected) Color(0xFF2085FF) else Color(0xFFF2F2F2))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 2.dp),
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
    suggestion: TemplateButtonSuggestion,
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

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun HomeFirstButtonSectionSkipPreview() {
    PreviewContainer {
        HomeFirstButtonSection(
            suggestions = emptyList(),
            onSuggestionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun HomeFirstButtonSectionTemplatePreview() {
    PreviewContainer {
        HomeFirstButtonSection(
            suggestions = mockTemplateSuggestionsMemory,
            onSuggestionClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

/** 온보딩 건너뛰기 — 모든 템플릿의 추천을 카테고리 구분 없이 한 목록으로 */
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun HomeFirstButtonSectionAllTemplatesPreview() {
    PreviewContainer {
        HomeFirstButtonSection(
            suggestions = mockTemplateSuggestionsMemory,
            onSuggestionClick = {},
            modifier = Modifier.padding(16.dp),
            groupByCategory = false,
        )
    }
}
