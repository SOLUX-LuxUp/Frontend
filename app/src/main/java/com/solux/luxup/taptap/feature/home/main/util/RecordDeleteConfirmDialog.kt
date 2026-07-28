package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue

private val DeleteColor = Color(0xFFF6989C)

// "정말로 기록을 삭제할까요?" 버튼 기록 삭제 확인 팝업
@Composable
fun RecordDeleteConfirmDialog(
    recordedAtIsoTimestamp: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "정말로 기록을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                formatRecordedAtText(recordedAtIsoTimestamp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                formatElapsedText(recordedAtIsoTimestamp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D)
            )
        }
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryDialogButton(
                text = "취소",
                textColor = Color(0xFFB1B1B1),
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            CategoryDialogButton(
                text = "삭제",
                textColor = DeleteColor,
                modifier = Modifier.weight(1f),
                onClick = onConfirmDelete
            )
        }
    }
}
