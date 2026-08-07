package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import kotlin.math.atan2
import kotlin.math.hypot

private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)
private val CountBlue = Color(0xFF0099FF)
private val BubbleWidth = 70.dp
private val BubbleHeight = 44.dp
private val BubbleGap = 10.dp

/** 상위 몇 개 카테고리까지 개별 표시할지 — 초과분은 "기타"로 묶임 */
private const val MAX_VISIBLE_CATEGORIES = 4

// 도넛 조각 팔레트 — 상위 4개 카테고리 + 기타(마지막 색)
private val DonutPalette = listOf(
    Color(0xFFFF6565),
    Color(0xFFFFD53F),
    Color(0xFF2085FF),
    Color(0xFF8DBFFE),
    Color(0xFFA9F594),
)

/** 도넛 조각/범례 표시 단위 (개별 카테고리 또는 "기타" 묶음) */
private data class CategorySlice(
    val name: String,
    val count: Int,
    val color: Color
)

/** 카테고리별 활동량 비율 — 도넛 + 범례 (기록비율 전체보기 전용) */
@Composable
fun InsightCategoryDonutChart(
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    val sorted = remember(categoryTapCounts) { categoryTapCounts.sortedByDescending { it.count } }
    val total = sorted.sumOf { it.count }

    if (sorted.isEmpty() || total <= 0) {
        Text("아직 기록이 없어요", fontSize = 14.sp, color = Color(0xFFB0B0B0), modifier = modifier)
        return
    }

    val slices = remember(sorted) {
        buildList {
            sorted.take(MAX_VISIBLE_CATEGORIES).forEachIndexed { index, category ->
                add(CategorySlice(category.categoryName, category.count, DonutPalette[index]))
            }
            val rest = sorted.drop(MAX_VISIBLE_CATEGORIES)
            if (rest.isNotEmpty()) {
                add(CategorySlice("기타", rest.sumOf { it.count }, DonutPalette.last()))
            }
        }
    }
    var selectedIndex by remember(slices) { mutableStateOf<Int?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DonutChart(
            slices = slices,
            total = total,
            selectedIndex = selectedIndex,
            onSliceTap = { index -> selectedIndex = if (selectedIndex == index) null else index },
            diameter = 130.dp,
            ringWidth = 30.dp
        )

        Spacer(Modifier.width(30.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            slices.forEach { slice ->
                LegendRow(slice = slice)
            }
        }
    }
}

@Composable
private fun DonutChart(
    slices: List<CategorySlice>,
    total: Int,
    selectedIndex: Int?,
    onSliceTap: (Int?) -> Unit,
    diameter: Dp,
    ringWidth: Dp
) {
    val sweepProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700),
        label = "insightDonutSweep"
    )

    // 각 조각의 (시작각, 스윕각) — 탭 히트테스트와 팝업 위치 계산에 공용으로 사용
    val arcRanges = remember(slices, total) {
        var startAngle = -90f
        slices.map { slice ->
            val sweep = slice.count.toFloat() / total * 360f
            (startAngle to sweep).also { startAngle += sweep }
        }
    }
    var tapOffsetPx by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier.size(diameter),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(diameter)
                .pointerInput(arcRanges) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val distance = hypot(dx, dy)
                        val outerRadius = size.width / 2f
                        val innerRadius = outerRadius - ringWidth.toPx()

                        if (distance < innerRadius || distance > outerRadius) {
                            onSliceTap(null)
                            return@detectTapGestures
                        }

                        var angle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                        if (angle < -90f) angle += 360f

                        val tappedIndex = arcRanges.indexOfFirst { (start, sweep) ->
                            angle >= start && angle < start + sweep
                        }
                        if (tappedIndex >= 0) {
                            tapOffsetPx = tapOffset
                        }
                        onSliceTap(if (tappedIndex >= 0) tappedIndex else null)
                    }
                }
        ) {
            val strokePx = ringWidth.toPx()
            val inset = strokePx / 2
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)

            slices.forEachIndexed { index, slice ->
                val (startAngle, sweep) = arcRanges[index]
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweep * sweepProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "총", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NameColor)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "$total", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CountBlue)
                Text(text = "회", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TitleColor)
            }
        }

        val tappedSlice = selectedIndex?.let { slices.getOrNull(it) }
        if (tappedSlice != null) {
            val density = LocalDensity.current
            val bubbleWidthPx = with(density) { BubbleWidth.toPx() }
            val bubbleHeightPx = with(density) { BubbleHeight.toPx() }
            val bubbleGapPx = with(density) { BubbleGap.toPx() }
            val popupOffset = IntOffset(
                x = (tapOffsetPx.x - bubbleWidthPx / 2f).toInt(),
                y = (tapOffsetPx.y - bubbleHeightPx - bubbleGapPx).toInt()
            )
            Popup(
                alignment = Alignment.TopStart,
                offset = popupOffset,
                onDismissRequest = { onSliceTap(null) }
            ) {
                SliceBubble(slice = tappedSlice)
            }
        }
    }
}

@Composable
private fun SliceBubble(slice: CategorySlice) {
    Column(
        modifier = Modifier
            .width(BubbleWidth)
            .height(BubbleHeight)
            .clip(RoundedCornerShape(30))
            .background(slice.color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = slice.name,
            fontSize = 14.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "${slice.count}회",
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
private fun LegendRow(slice: CategorySlice) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(slice.color)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = slice.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, heightDp = 220)
@Composable
private fun InsightCategoryDonutChartPreview() {
    PreviewContainer {
        InsightCategoryDonutChart(
            categoryTapCounts = listOf(
                InsightCategoryTapCount(10L, "건강", 27, 0.491, "#4C8DFF"),
                InsightCategoryTapCount(20L, "자기계발", 28, 0.509, "#FFB84C"),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "5개 초과 (기타 묶음)", heightDp = 260)
@Composable
private fun InsightCategoryDonutChartEtcPreview() {
    PreviewContainer {
        InsightCategoryDonutChart(
            categoryTapCounts = listOf(
                InsightCategoryTapCount(10L, "건강", 10, 0.2, null),
                InsightCategoryTapCount(20L, "자기계발", 9, 0.18, null),
                InsightCategoryTapCount(30L, "여가", 8, 0.16, null),
                InsightCategoryTapCount(40L, "업무", 6, 0.12, null),
                InsightCategoryTapCount(50L, "가사", 4, 0.08, null),
                InsightCategoryTapCount(60L, "기타2", 3, 0.06, null),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}