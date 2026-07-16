package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.solux.luxup.taptap.core.ui.theme.Pretendard

@Composable
fun TeamInviteCodeModal(
    inviteCode: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit,                 // 공유 방식 미정 → 콜백만
) {
    Dialog(onDismissRequest = onDismiss) {
        InviteCodeContent(inviteCode = inviteCode, onShare = onShare)
    }
}

@Composable
private fun InviteCodeContent(
    inviteCode: String,
    onShare: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)                          // 화면 대비 폭 (조정 가능)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))                // 연파랑
            .padding(vertical = 14.dp, horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {24
        // "팀 코드" 라벨
        Text(
            text = "팀 코드",
            fontFamily = Pretendard,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(16.dp))

        // 코드 박스 (흰색)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inviteCode,
                fontFamily = Pretendard,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB1B1B1)
            )
        }
        Spacer(Modifier.height(10.dp))

        // 공유하기 버튼 (흰색)
        // 공유하기 버튼 (흰색 + 테두리)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(11.dp))                    // 12 → 11
                .background(Color(0xFFFEFEFE))                      // #FEFEFE
                .border(1.dp, Color(0xFFB1B1B1), RoundedCornerShape(11.dp))   // 테두리 추가
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onShare() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "공유하기",
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB1B1B1)
            )
        }
    }
}

// 프리뷰는 Dialog 없이 내용만 → 렌더됨
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390)
@Composable
private fun TeamInviteCodeModalPreview() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        InviteCodeContent(inviteCode = "SE4EDI", onShare = {})
    }
}