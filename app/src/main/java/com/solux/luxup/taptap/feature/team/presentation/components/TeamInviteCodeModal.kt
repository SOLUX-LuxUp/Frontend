package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun TeamInviteCodeModal(
    inviteCode: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        InviteCodeContent(inviteCode = inviteCode, onShare = onShare)
    }
}

@Composable
private fun InviteCodeContent(
    inviteCode: String,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .size(width = 268.dp, height = 178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(18.dp))

        Text(
            text = "팀 코드",
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )

        Spacer(Modifier.height(11.dp))

        // 코드 박스 204 x 56, radius 10
        Box(
            modifier = Modifier
                .width(204.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = inviteCode,
                fontSize = 25.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB1B1B1),
            )
        }

        Spacer(Modifier.height(9.dp))

        // 공유하기 버튼 204 x 43, radius 100, 테두리 #B1B1B1, 누르면 파란 그라데이션
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val background = if (isPressed) {
            Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        } else {
            Brush.horizontalGradient(listOf(Color(0xFFFEFEFE), Color(0xFFFEFEFE)))
        }
        val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(Color(0xFFB1B1B1), Color(0xFFB1B1B1)))
        Box(
            modifier = Modifier
                .width(204.dp)
                .height(43.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(background)
                .border(1.dp, borderBrush, RoundedCornerShape(100.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onShare,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "공유하기",
                fontSize = 18.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPressed) Color.White else Color(0xFFB1B1B1),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamInviteCodeModalPreview() {
    PreviewContainer {
        InviteCodeContent(
            inviteCode = "SE4EDI",
            onShare = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}