package com.solux.luxup.taptap.feature.home.main.util

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

@Composable
fun AddButtonMenuPopup(
    onCreateManually: () -> Unit,
    onCreateQuickly: () -> Unit,
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
        AddButtonMenuOption(text = "직접 만들기", onClick = onCreateManually)
        AddButtonMenuOption(text = "빠르게 만들기", onClick = onCreateQuickly)
    }
}

/** 눌렀을 때 파란 그라데이션으로 바뀌는 흰 알약 옵션 */
@Composable
private fun AddButtonMenuOption(
    text: String,
    onClick: () -> Unit,
) {
    val contentColor = Color(0xFF6D6D6D)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = if (isPressed) {
        Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
    } else {
        Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(contentColor, contentColor))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = if (isPressed) Color.White else contentColor)
    }
}