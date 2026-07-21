package com.solux.luxup.taptap.feature.home.buttondetail.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, RecordDeleteColor, RoundedCornerShape(10.dp))
                .clickable { onDeleteClick() }
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("기록 삭제", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = RecordDeleteColor)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(10.dp))
                .clickable { onAddMemoClick() }
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (hasMemo) "메모 수정" else "메모 추가",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
        }
    }
}