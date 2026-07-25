package com.solux.luxup.taptap.feature.team.presentation.insight.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.feature.team.model.TeamInsightButtonCount
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import androidx.compose.foundation.layout.PaddingValues
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.IconColor

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)   // 시안: natural 70 #6D6D6D
private val CountColor = Color(0xFF6D6D6D)  // ⚠ 횟수 색 시안 확인 — 일단 이름과 동일
private val SubColor = Color(0xFF8A94A6)
private val TrackColor = Color(0xFFE9EBEF)   // ⚠ 시안 트랙색 확인
private val CircleBorder = Color(0xFFECEEF1) // ⚠ 시안 원 테두리 확인
private val DashColor = Color(0xFFD4D9E0)    // ⚠ 시안 점선색 확인
private val BarGradient = Brush.horizontalGradient(
    listOf(BlueGradientStart, BlueGradientEnd)
)

@Composable
fun InsightButtonRatioSection(
    buttonTapCounts: List<TeamInsightButtonCount>,
    totalTapCount: Int,
    modifier: Modifier = Modifier
) {
    val categories = remember(buttonTapCounts) {
        val names = buttonTapCounts.mapNotNull { it.categoryName }.distinct()
        listOf("ALL") + names
    }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val filtered = remember(buttonTapCounts, selectedCategoryName) {
        buttonTapCounts
            .filter { selectedCategoryName == null || it.categoryName == selectedCategoryName }
            .sortedByDescending { it.tapCount }
    }
    val denominator = filtered.sumOf { it.tapCount }.coerceAtLeast(1)

    SectionCard(
        modifier = modifier,
        borderColor = Color(0xFFEDEDED),  // ⚠ 시안 테두리색으로 조정
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "기록 비율",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )
        Spacer(Modifier.height(10.dp))

        CategoryDropdown(
            categories = categories,
            onCategorySelected = { name ->
                selectedCategoryName = if (name == "ALL") null else name
            }
        )

        Spacer(Modifier.height(16.dp))

        if (buttonTapCounts.isEmpty() || totalTapCount <= 0) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            filtered.forEachIndexed { index, item ->
                RatioRow(
                    item = item,
                    denominator = denominator,
                    isFirst = index == 0,
                    isLast = index == filtered.lastIndex
                )
            }
        }
    }
}

@Composable
private fun RatioRow(
    item: TeamInsightButtonCount,
    denominator: Int,
    isFirst: Boolean,
    isLast: Boolean
) {
    val ratio = (item.tapCount.toFloat() / denominator).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 600),
        label = "ratioBar"
    )
    val iconColor = IconColor.from(item.iconColor).color
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConnectorIcon(
            initial = item.buttonName.take(1),
            iconColor = iconColor,
            isFirst = isFirst,
            isLast = isLast
        )

        Spacer(Modifier.width(12.dp))

        // 이름(2줄) — 시안 53dp
        Text(
            text = item.buttonName,
            fontSize = 14.sp,               // 시안 Bold/Small 14sp
            fontWeight = FontWeight.Bold,   // 시안 700
            color = NameColor,              // 시안 #6D6D6D
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(53.dp)               // 시안 width 53
                .padding(vertical = 12.dp)
        )

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)              // ⚠ 시안 막대 높이 확인
                .clip(RoundedCornerShape(5.dp))
                .background(TrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedRatio)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(BarGradient)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = "${item.tapCount}회",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CountColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(40.dp)
        )
    }
}

@Composable
private fun ConnectorIcon(
    initial: String,
    iconColor: Color,
    isFirst: Boolean,
    isLast: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        ) {
            val cx = size.width / 2
            val top = if (isFirst) size.height / 2 else 0f
            val bottom = if (isLast) size.height / 2 else size.height
            drawLine(
                color = DashColor,
                start = Offset(cx, top),
                end = Offset(cx, bottom),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f))
            )
        }

        // 흰 원 35dp + 테두리 (그림자 없이 — 결정대로)
        Box(
            modifier = Modifier
                .size(35.dp)                // 시안 35×35
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, CircleBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = iconColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(showBackground = true, heightDp = 500)
@Composable
private fun InsightButtonRatioSectionPreview() {
    PreviewContainer {
        InsightButtonRatioSection(
            buttonTapCounts = listOf(
                TeamInsightButtonCount(6L, "기획서 업데이트", "document", "#FFC107", 20L, "자기계발", 30),
                TeamInsightButtonCount(1L, "프론트 코드 수정", "code", "#4C8DFF", 20L, "자기계발", 19),
                TeamInsightButtonCount(7L, "피그마 업데이트", "pencil", "#FF5C5C", 20L, "자기계발", 10),
                TeamInsightButtonCount(8L, "톡방에 연락", "person", "#4C8DFF", 10L, "건강", 8),
                TeamInsightButtonCount(9L, "계획서 수정", "edit", "#FFC107", 20L, "자기계발", 1),
            ),
            totalTapCount = 68,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun InsightButtonRatioSectionEmptyPreview() {
    PreviewContainer {
        InsightButtonRatioSection(
            buttonTapCounts = emptyList(),
            totalTapCount = 0,
            modifier = Modifier.padding(16.dp)
        )
    }
}