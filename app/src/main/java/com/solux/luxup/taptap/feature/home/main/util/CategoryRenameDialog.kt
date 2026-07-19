package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// "카테고리 수정" 팝업 - 기존 카테고리 이름을 변경
@Composable
fun CategoryRenameDialog(
    category: String,
    onDismiss: () -> Unit,
    onRename: (newName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(category) { mutableStateOf(category) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(27.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp)
    ) {
        Text(
            text = "카테고리 수정",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text("카테고리 이름", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFE2E2E2))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF6D6D6D)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
                text = "저장",
                textColor = Color(0xFFF6989C),
                modifier = Modifier.weight(1f),
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) {
                        onRename(trimmed.uppercase())
                    }
                }
            )
        }
    }
}
