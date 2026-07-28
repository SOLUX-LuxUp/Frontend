package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue

private val DeleteColor = Color(0xFFF6989C)

// "정말로 기록을 삭제할까요?" 카테고리 삭제 확인 팝업
@Composable
fun CategoryDeleteConfirmDialog(
    category: String,
    onDismiss: () -> Unit,
    onConfirmDelete: (deleteButtonsToo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteButtonsToo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BrandWhiteBlue)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "카테고리를 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10))
                .background(Color.White)
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(category, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        }
        Spacer(Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { deleteButtonsToo = !deleteButtonsToo }
        ) {
            Checkbox(
                checked = deleteButtonsToo,
                onCheckedChange = { deleteButtonsToo = it },
                colors = CheckboxDefaults.colors(checkedColor = BlueGradientEnd),
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "카테고리에 속한 버튼들을 함께 삭제합니다",
                fontSize = 13.sp,
                color = Color(0xFF6D6D6D)
            )
        }
        Spacer(Modifier.height(30.dp))

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
                onClick = { onConfirmDelete(deleteButtonsToo) }
            )
        }
    }
}