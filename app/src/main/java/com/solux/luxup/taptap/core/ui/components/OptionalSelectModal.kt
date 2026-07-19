package com.solux.luxup.taptap.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/**
 * 선택지 2개 이상을 세로로 나열하는 공용 모달.
 * 팀 추가 옵션(새로운 팀 만들기 / 팀 코드로 추가하기),
 * 팀 이미지 설정(이미지로 설정 / 아이콘으로 설정) 등에서 공통 사용.
 *
 * 버튼은 눌리는 동안 파란 그라데이션으로 전환된다.
 */
@Composable
fun OptionSelectModal(
    options: List<OptionItem>,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        OptionSelectModalContent(
            options = options,
            onDismiss = onDismiss,
        )
    }
}

data class OptionItem(
    val label: String,
    val onClick: () -> Unit,
)

/** Dialog는 preview가 불가하므로 내용만 분리 */
@Composable
fun OptionSelectModalContent(
    options: List<OptionItem>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(268.dp)
            .height(178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(ModalBackground)
            .padding(horizontal = 29.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(37.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            CloseIcon(onClick = onDismiss)
        }

        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            options.forEach { option ->
                OptionButton(
                    label = option.label,
                    onClick = option.onClick,
                )
            }
        }
    }
}

@Composable
private fun OptionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val background = if (isPressed) {
        Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
    } else {
        Brush.horizontalGradient(listOf(Color.White, Color.White))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(57.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            lineHeight = 18.sp,          // line-height 100%
            fontWeight = FontWeight.Medium,
            color = if (isPressed) Color.White else Color(0xFF6D6D6D),
        )
    }
}

@Composable
private fun CloseIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            drawLine(
                color = CloseIconColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = stroke.width,
                cap = stroke.cap,
            )
            drawLine(
                color = CloseIconColor,
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height),
                strokeWidth = stroke.width,
                cap = stroke.cap,
            )
        }
    }
}

// TODO: Figma Inspect 값 확정 후 core/ui/theme 로 이동
private val ModalBackground = Color(0xFFDCEBFB)
private val GradientStart = Color(0xFF4FA8FF)
private val GradientEnd = Color(0xFF2E7BFF)
private val CloseIconColor = Color(0xFF2E7BFF)

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun OptionSelectModalPreview() {
    PreviewContainer {
        OptionSelectModalContent(
            options = listOf(
                OptionItem("새로운 팀 만들기") {},
                OptionItem("팀 코드로 추가하기") {},
            ),
            onDismiss = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}