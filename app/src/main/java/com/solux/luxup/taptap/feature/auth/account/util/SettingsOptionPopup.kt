package com.solux.luxup.taptap.feature.auth.account.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
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
import com.solux.luxup.taptap.core.ui.theme.Pretendard

private val PopupBackgroundColor = Color(0xFFDEEFFF)

/** 소리 설정/화면 위 표시 같은 항목을 누르면 옆에 뜨는 선택지 팝업 */
@Composable
fun SettingsOptionPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PopupBackgroundColor)
            .padding(top = 4.dp, bottom = 4.dp, start = 10.dp, end = 10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEachIndexed { index, option ->
                SettingsOptionPopupItem(
                    text = option,
                    onClick = {
                        onSelect(index)
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingsOptionPopupItem(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsOptionPopupContentPreview() {
    // DropdownMenu(Popup)는 프리뷰가 안 되어 내용 레이아웃만 확인
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PopupBackgroundColor),
    ) {
        listOf("무음", "진동", "소리").forEach { option ->
            SettingsOptionPopupItem(text = option, onClick = {})
        }
    }
}