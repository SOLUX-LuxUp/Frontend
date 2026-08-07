package com.solux.luxup.taptap.feature.auth.account.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

internal val DangerColor = Color(0xFFF6989C)
internal val CancelColor = Color(0xFFB1B1B1)

/**
 * 계정 관련 확인 팝업(로그아웃/탈퇴 등)에서 공용으로 쓰는 취소·실행 버튼.
 * isDanger(빨간 계열)면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다
 */
@Composable
internal fun AccountDialogButton(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(textColor, textColor))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(50.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else textColor,
        )
    }
}
