package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.mockSuggestionsTogether
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion

/**
 * "+" → "빠르게 만들기"를 눌렀을 때 뜨는 추천 버튼 팝업 (개인 파트 TemplateSuggestionPopupCard 참고).
 * 항목을 탭하면 그 자리에서 생성되고 팝업이 닫힌다.
 */
@Composable
fun TeamQuickCreatePopup(
    suggestions: List<TeamButtonSuggestion>,
    onDismiss: () -> Unit,
    onSuggestionClick: (TeamButtonSuggestion) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamQuickCreatePopupContent(
            suggestions = suggestions,
            onDismiss = onDismiss,
            onSuggestionClick = onSuggestionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
    }
}

/** Dialog는 preview가 안 돼서 내용만 분리 */
@Composable
fun TeamQuickCreatePopupContent(
    suggestions: List<TeamButtonSuggestion>,
    onDismiss: () -> Unit,
    onSuggestionClick: (TeamButtonSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_x),
                contentDescription = "닫기",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(23.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "빠르게 버튼을 만들어보세요",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        if (suggestions.isEmpty()) return@Column

        Spacer(Modifier.height(20.dp))

        // 카테고리 탭. categoryName이 null인 프리셋은 "기타"로 묶는다.
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

        Spacer(Modifier.height(12.dp))

        val filtered = remember(suggestions, selectedCategory) {
            suggestions
                .filter { (it.categoryName ?: NO_CATEGORY_LABEL) == selectedCategory }
                .shuffled()
        }

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

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TeamQuickCreatePopupContentPreview() {
    PreviewContainer {
        TeamQuickCreatePopupContent(
            suggestions = mockSuggestionsTogether,
            onDismiss = {},
            onSuggestionClick = {},
            modifier = Modifier.padding(40.dp),
        )
    }
}
