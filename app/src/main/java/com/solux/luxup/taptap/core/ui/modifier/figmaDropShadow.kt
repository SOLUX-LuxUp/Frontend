package com.solux.luxup.taptap.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
/**
 * Figma 스타일의 부드러운 Drop Shadow를 적용하는 Modifier
 *
 * 기본 elevation shadow보다 더 자연스러운 디자인을 위해 사용
 *
 * @param cornerRadius 카드/박스의 둥근 정도
 * @param color 그림자 색상
 * @param alpha 그림자 투명도
 * @param blurRadius 그림자 퍼짐 정도
 */

fun Modifier.figmaDropShadow(
    cornerRadius: Dp,
    color: Color = Color.Black,
    alpha: Float = 0.15f,
    blurRadius: Dp = 7.dp
): Modifier = this.drawBehind {

    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = transparent
                setShadowLayer(
                    blurRadius.toPx(),
                    0f,
                    0f,
                    shadowColor
                )
            }
        }

        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )
    }
}