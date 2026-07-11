package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val HabitCardCornerRadius = 15.dp
private val HabitBadgeSize = 60.dp
private val HabitBadgeOffsetX = 15.dp
private val HabitBadgeOffsetY = (-15).dp

// 카드의 오른쪽 위 모서리를 뱃지 원과 정확히 같은 위치·반지름의 원으로 파낸(concave) Shape.
// 카드 크기(가로세로 비율)와 무관하게 파인 부분이 항상 정원으로 유지되도록 반지름을 dp 고정값으로 뺀다.
private class HabitCardShape(
    private val cornerRadius: Dp,
    private val notchRadius: Dp,
    private val notchCenterDxFromTopEnd: Dp,
    private val notchCenterDyFromTopEnd: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerPx = with(density) { cornerRadius.toPx() }
        val notchRadiusPx = with(density) { notchRadius.toPx() }
        val notchCenterPx = Offset(
            x = size.width + with(density) { notchCenterDxFromTopEnd.toPx() },
            y = with(density) { notchCenterDyFromTopEnd.toPx() }
        )

        val basePath = Path().apply {
            addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(cornerPx)))
        }
        val notchPath = Path().apply {
            addOval(Rect(center = notchCenterPx, radius = notchRadiusPx))
        }

        val resultAndroidPath = android.graphics.Path()
        resultAndroidPath.op(basePath.asAndroidPath(), notchPath.asAndroidPath(), android.graphics.Path.Op.DIFFERENCE)

        return Outline.Generic(resultAndroidPath.asComposePath())
    }
}

// 카드의 실제 파인 모양을 그대로 따라가는 드롭섀도 (X:0, Y:0, Blur:7.8, Color:#000000 12%)
private fun Modifier.habitCardDropShadow(shape: Shape): Modifier = this.drawBehind {
    val shadowColor = Color.Black.copy(alpha = 0.12f).toArgb()
    val transparent = Color.Black.copy(alpha = 0f).toArgb()
    val outline = shape.createOutline(size, layoutDirection, this)
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(7.8.dp.toPx(), 0f, 0f, shadowColor)
        val path = (outline as Outline.Generic).path
        canvas.drawPath(path, paint)
    }
}

@Composable
fun HabitButtonGrid(buttons: List<HabitButton>) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        buttons.chunked(2).forEach { rowButtons ->
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                rowButtons.forEach { button ->
                    HabitButtonCard(button = button, modifier = Modifier.weight(1f))
                }
                if (rowButtons.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HabitButtonCard(
    button: HabitButton,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    var isFavorite by remember(button.title) { mutableStateOf(button.isFavorite) }
    val cardShape = remember {
        HabitCardShape(
            cornerRadius = HabitCardCornerRadius,
            notchRadius = HabitBadgeSize / 2 + 5.dp,
            notchCenterDxFromTopEnd = HabitBadgeOffsetX - HabitBadgeSize / 2,
            notchCenterDyFromTopEnd = HabitBadgeOffsetY + HabitBadgeSize / 2
        )
    }

    Box(modifier = modifier) {
        // 카드 모서리가 뱃지 자리만큼 파여 있으므로, 뱃지는 카드 위에 그대로 올려 그린다
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .habitCardDropShadow(shape = cardShape),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star_empty),
                        contentDescription = "즐겨찾기",
                        tint = if (isFavorite) BlueGradientEnd else Color.Unspecified,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { isFavorite = !isFavorite }
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        tint = Color(0xFF6D6D6D),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onMoreClick() }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(button.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                Spacer(Modifier.height(0.dp))
                Text(button.category, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFB1B1B1))
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(colors = listOf(BlueGradientStart, BlueGradientEnd)))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${formatElapsedText(button.lastRecordedAt)} 기록 · ${formatRecordedAtText(button.lastRecordedAt)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = HabitBadgeOffsetX, y = HabitBadgeOffsetY)
                .size(HabitBadgeSize)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFFDEEFFF))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(button.iconRes),
                    contentDescription = null,
                    tint = button.iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}