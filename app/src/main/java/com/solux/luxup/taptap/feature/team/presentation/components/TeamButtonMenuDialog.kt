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
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
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
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MenuButton(text = "버튼 정보", textColor = Color(0xFF6D6D6D), onClick = onSelectInfo)
        MenuButton(text = "버튼 삭제", textColor = DeleteColor, onClick = onSelectDelete)
    }
}

@Composable
private fun MenuButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, textColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
    }
}

private val DeleteColor = Color(0xFFF6989C)

@Composable
fun TeamButtonDeleteConfirmContent(
    buttonName: String,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "정말로 버튼을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(buttonName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryDialogButton(
                text = "취소",
                textColor = Color(0xFFB1B1B1),
                modifier = Modifier.weight(1f),
                onClick = onCancel,
            )
            CategoryDialogButton(
                text = "삭제",
                textColor = DeleteColor,
                modifier = Modifier.weight(1f),
                onClick = onConfirmDelete,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 340)
@Composable
private fun TeamButtonMenuContentPreview() {
    TeamButtonMenuContent(
        onSelectInfo = {},
        onSelectDelete = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 320)
@Composable
private fun TeamButtonDeleteConfirmContentPreview() {
    TeamButtonDeleteConfirmContent(
        buttonName = "물 마시기",
        onCancel = {},
        onConfirmDelete = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
    )
}