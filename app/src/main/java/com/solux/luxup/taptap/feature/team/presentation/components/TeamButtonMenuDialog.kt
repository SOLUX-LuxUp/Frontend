package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 카드의 ⋮ 를 눌렀을 때 뜨는 메뉴.
 * 버튼 정보 → 버튼 정보 화면 / 버튼 삭제 → 삭제 확인 모달
 */
@Composable
fun TeamButtonMenuDialog(
    onDismiss: () -> Unit,
    onSelectInfo: () -> Unit,
    onSelectDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamButtonMenuContent(
            onSelectInfo = onSelectInfo,
            onSelectDelete = onSelectDelete,
        )
    }
}

@Composable
fun TeamButtonMenuContent(
    onSelectInfo: () -> Unit,
    onSelectDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(172.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 21.dp, vertical = 19.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MenuButton(text = "버튼 정보", onClick = onSelectInfo)
        MenuButton(text = "버튼 삭제", onClick = onSelectDelete)
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(41.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
        )
    }
}

/**
 * 삭제 확인 모달.
 * 취소 → 목록 유지 / 삭제 → DELETE 호출 후 목록 갱신
 */
@Composable
fun TeamButtonDeleteConfirmDialog(
    buttonName: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamButtonDeleteConfirmContent(
            buttonName = buttonName,
            onCancel = onDismiss,
            onConfirmDelete = onConfirmDelete,
        )
    }
}

@Composable
fun TeamButtonDeleteConfirmContent(
    buttonName: String,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 21.dp, vertical = 19.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "정말로 버튼을 삭제할까요?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = buttonName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ConfirmButton(
                text = "취소",
                textColor = Color(0xFFB1B1B1),
                borderColor = Color(0xFF6D6D6D),
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            )
            ConfirmButton(
                text = "삭제",
                textColor = Color(0xFFFF7171),
                borderColor = Color(0xFFFFC0C0),
                onClick = onConfirmDelete,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ConfirmButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(41.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Preview(showBackground = true, widthDp = 260, heightDp = 200)
@Composable
private fun TeamButtonMenuContentPreview() {
    TeamButtonMenuContent(
        onSelectInfo = {},
        onSelectDelete = {},
        modifier = Modifier.padding(40.dp),
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 220)
@Composable
private fun TeamButtonDeleteConfirmContentPreview() {
    TeamButtonDeleteConfirmContent(
        buttonName = "물 마시기",
        onCancel = {},
        onConfirmDelete = {},
        modifier = Modifier.padding(40.dp),
    )
}