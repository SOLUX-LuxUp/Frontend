package com.solux.luxup.taptap.feature.auth.account.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/** 계정 삭제(탈퇴) 확인 — 기억 삭제 경고 문구 포함 (Frame 107) */
@Composable
fun AccountDeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFDEEFFF))
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "정말로 탈퇴하시겠어요?",
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "그동안 탭탭과 함께한 모든 기억들이 삭제되며,\n재가입하여도 복구할 수 없습니다",
                fontFamily = Pretendard,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "팀에 저장된 기억은 자동으로 삭제되지 않습니다",
                fontFamily = Pretendard,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccountDialogButton(
                    text = "취소",
                    textColor = CancelColor,
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                )
                AccountDialogButton(
                    text = "탈퇴",
                    textColor = DangerColor,
                    isDanger = true,
                    modifier = Modifier.weight(1f),
                    onClick = onConfirm,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountDeleteConfirmDialogContentPreview() {
    // Dialog는 프리뷰가 안 되어 내용 레이아웃만 확인
    Column(
        modifier = Modifier
            .padding(40.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "정말로 탈퇴하시겠어요?",
            fontFamily = Pretendard,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "그동안 탭탭과 함께한 모든 기억들이 삭제되며,\n재가입하여도 복구할 수 없습니다",
            fontFamily = Pretendard,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "팀에 저장된 기억은 자동으로 삭제되지 않습니다",
            fontFamily = Pretendard,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AccountDialogButton(text = "취소", textColor = CancelColor, modifier = Modifier.weight(1f), onClick = {})
            AccountDialogButton(text = "탈퇴", textColor = DangerColor, isDanger = true, modifier = Modifier.weight(1f), onClick = {})
        }
    }
}