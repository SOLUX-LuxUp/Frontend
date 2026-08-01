package com.solux.luxup.taptap.feature.team.presentation.button.timeline

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord

/**
 * 팀 타임라인 기록 모달.
 *
 *   기록 섹션 탭
 *     → TeamRecordActionMenu (기록 삭제 / 메모 추가·수정)
 *         → TeamRecordDeleteConfirmDialog
 *
 * 메모/이모지 입력은 core/util/button의 공용 MemoEmojiDialog를 쓴다 (개인 홈과 공유).
 * 본인이 남긴 기록에서만 열리므로, 모달 자체는 권한을 따지지 않는다.
 */

private val DangerColor = Color(0xFFF6989C)
private val TextColor = Color(0xFF6D6D6D)
private val DialogBackground = Color(0xFFDEEFFF)

// ---------------------------------------------------------------- 액션 메뉴

@Composable
fun TeamRecordActionMenu(
    hasMemo: Boolean,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onMemoClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamRecordActionMenuContent(
            hasMemo = hasMemo,
            onDeleteClick = onDeleteClick,
            onMemoClick = onMemoClick,
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}

@Composable
fun TeamRecordActionMenuContent(
    hasMemo: Boolean,
    onDeleteClick: () -> Unit,
    onMemoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(268.dp)
            .height(178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(DialogBackground)
            .padding(horizontal = 29.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        DialogOutlinedButton(
            text = "기록 삭제",
            contentColor = DangerColor,
            onClick = onDeleteClick,
            height = 57.dp,
        )
        DialogOutlinedButton(
            // 이미 메모가 있으면 "메모 수정"
            text = if (hasMemo) "메모 수정" else "메모 추가",
            contentColor = TextColor,
            onClick = onMemoClick,
            height = 57.dp,
        )
    }
}

// ------------------------------------------------------------ 삭제 확인 모달

@Composable
fun TeamRecordDeleteConfirmDialog(
    record: TeamButtonTimelineRecord,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamRecordDeleteConfirmContent(
            recordedAt = record.recordedAt,
            onCancel = onDismiss,
            onConfirmDelete = onConfirmDelete,
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}

@Composable
fun TeamRecordDeleteConfirmContent(
    recordedAt: String,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val parsed = parseRecordedAt(recordedAt)

    Column(
        modifier = modifier
            .width(268.dp)
            .height(178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(DialogBackground)
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(22.dp))

        Text(
            "정말로 기록을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextColor,
        )

        Spacer(Modifier.height(17.dp))

        // 어떤 기록을 지우는지 확인할 수 있게 시각과 경과 시간을 함께 보여준다
        Row(
            modifier = Modifier
                .width(210.dp)
                .height(43.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = parsed?.let { timeOfDay(it) } ?: "-",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextColor,
            )
            Spacer(Modifier.width(25.dp))
            Text(
                text = parsed?.let { elapsedText(it) }.orEmpty(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextColor,
            )
        }

        Spacer(Modifier.height(11.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            DialogOutlinedButton(
                text = "취소",
                contentColor = Color(0xFFB1B1B1),
                onClick = onCancel,
                modifier = Modifier.width(99.dp),
                height = 43.dp,
                fontSize = 18.sp,
                cornerRadius = 100.dp,
            )
            DialogOutlinedButton(
                text = "삭제",
                contentColor = Color(0xFFF6989C),
                onClick = onConfirmDelete,
                modifier = Modifier.width(99.dp),
                height = 43.dp,
                fontSize = 18.sp,
                cornerRadius = 100.dp,
            )
        }
    }
}

// ------------------------------------------------------------------ 공통 버튼

@Composable
private fun DialogOutlinedButton(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 20.dp,
    height: Dp? = null,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    cornerRadius: Dp = 11.dp,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val base = modifier
        .fillMaxWidth()
        .let { if (height != null) it.height(height) else it }
        .clip(shape)
        .background(Color.White)
        .border(1.dp, contentColor, shape)
        .clickable(onClick = onClick)
    Box(
        modifier = if (height != null) base else base.padding(vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontSize = fontSize, fontWeight = FontWeight.Medium, color = contentColor)
    }
}

// --------------------------------------------------------------------- Preview

@Preview(name = "액션 메뉴 - 메모 없음", showBackground = true, widthDp = 390)
@Composable
private fun TeamRecordActionMenuPreview() {
    TeamRecordActionMenuContent(
        hasMemo = false,
        onDeleteClick = {},
        onMemoClick = {},
        modifier = Modifier.padding(40.dp),
    )
}

@Preview(name = "액션 메뉴 - 메모 있음", showBackground = true, widthDp = 390)
@Composable
private fun TeamRecordActionMenuWithMemoPreview() {
    TeamRecordActionMenuContent(
        hasMemo = true,
        onDeleteClick = {},
        onMemoClick = {},
        modifier = Modifier.padding(40.dp),
    )
}

@Preview(name = "삭제 확인", showBackground = true, widthDp = 390)
@Composable
private fun TeamRecordDeleteConfirmPreview() {
    TeamRecordDeleteConfirmContent(
        recordedAt = java.time.LocalDateTime.now().minusMinutes(31).toString(),
        onCancel = {},
        onConfirmDelete = {},
        modifier = Modifier.padding(40.dp),
    )
}