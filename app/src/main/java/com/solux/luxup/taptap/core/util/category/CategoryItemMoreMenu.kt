package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// "카테고리 수정" 목록에서 "..."를 눌렀을 때 뜨는 카테고리 수정/삭제 미니 메뉴
@Composable
fun CategoryItemMoreMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(65.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDEEFFF))
    ) {
        Text(
            text = "수정",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditClick() }
                .padding(vertical = 5.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 10.dp)
                .background(Color(0xFF6D6D6D))
                .padding(horizontal = 10.dp)
        )
        Text(
            text = "삭제",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDeleteClick() }
                .padding(vertical = 5.dp)
        )
    }
}