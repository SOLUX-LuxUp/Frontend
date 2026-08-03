package com.solux.luxup.taptap.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 단순 안내용 모달. 문구만 바꿔 여러 곳에서 재사용한다.
 *
 * 사용처
 *  - 탭 권한 없이 버튼을 눌렀을 때
 *  - 수정 권한 없이 버튼 정보 화면의 수정 아이콘을 눌렀을 때
 */
@Composable
fun NoticeDialog(
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "확인",
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        NoticeContent(
            message = message,
            confirmText = confirmText,
            onConfirm = onDismiss,
        )
    }
}

/** Dialog는 preview가 안 돼서 내용만 분리 */
@Composable
fun NoticeContent(
    message: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
) {
    Column(
        modifier = modifier
            .width(268.dp)
            .height(178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(11.dp))
                .clickable(onClick = onConfirm),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = confirmText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
            )
        }
    }
}

/** 탭 권한 없이 버튼을 눌렀을 때 */
@Preview(name = "탭 권한 없음", showBackground = true, widthDp = 348, heightDp = 258)
@Composable
private fun NoticeContentNoTapPermissionPreview() {
    NoticeContent(
        message = "이 버튼을 누를 권한이 없어요.\n버튼 정보에서 권한을 요청해 보세요.",
        onConfirm = {},
        modifier = Modifier.padding(40.dp),
    )
}

/** 수정 권한 없이 버튼 정보 화면의 수정 아이콘을 눌렀을 때 */
@Preview(name = "수정 권한 없음", showBackground = true, widthDp = 348, heightDp = 258)
@Composable
private fun NoticeContentNoEditPermissionPreview() {
    NoticeContent(
        message = "지금은 팀장만 버튼을 수정할 수 있어요.",
        onConfirm = {},
        modifier = Modifier.padding(40.dp),
    )
}