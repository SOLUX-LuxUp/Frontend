package com.solux.luxup.taptap.feature.auth.account.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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

/** 탈퇴 완료 안내 — 버튼 없이 탭하면 닫힘 (Frame 108) */
@Composable
fun AccountDeleteCompleteDialog(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onConfirm) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFDEEFFF))
                .clickable(onClick = onConfirm)
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "탈퇴가 완료되었습니다",
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "탭탭과의 여정은 끝났지만\n당신의 기억은 앞으로도 계속됩니다",
                fontFamily = Pretendard,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountDeleteCompleteDialogContentPreview() {
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
            text = "탈퇴가 완료되었습니다",
            fontFamily = Pretendard,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "탭탭과의 여정은 끝났지만\n당신의 기억은 앞으로도 계속됩니다",
            fontFamily = Pretendard,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center
        )
    }
}