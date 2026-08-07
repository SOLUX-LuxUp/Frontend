package com.solux.luxup.taptap.core.util.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val RecordDeleteColor = Color(0xFFF6989C)

// 타임라인 기록 항목을 눌렀을 때 뜨는 "기록 삭제" / "메모 추가"(또는 "메모 수정") 액션 메뉴
@Composable
fun RecordActionMenu(
    onDeleteClick: () -> Unit,
    onAddMemoClick: () -> Unit,
    hasMemo: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        RecordActionMenuButton(
            text = "기록 삭제",
            contentColor = RecordDeleteColor,
            isDanger = true,
            onClick = onDeleteClick
        )
        RecordActionMenuButton(
            text = if (hasMemo) "메모 수정" else "메모 추가",
            contentColor = Color(0xFF6D6D6D),
            onClick = onAddMemoClick
        )
    }
}

/** isDanger(빨간 계열)면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다 */
@Composable
private fun RecordActionMenuButton(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(contentColor, contentColor))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else contentColor
        )
    }
}