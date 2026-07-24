package com.solux.luxup.taptap.feature.insight.daily.util

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

private val DangerColor = Color(0xFFF6989C)
private val TextColor = Color(0xFF6D6D6D)
private val DialogBackground = Color(0xFFDEEFFF)

/** 타임라인 행의 "..." → "기록 삭제" / "기록으로 이동" */
@Composable
fun InsightRecordActionMenu(
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveToRecordClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        InsightRecordActionMenuContent(
            onDeleteClick = onDeleteClick,
            onMoveToRecordClick = onMoveToRecordClick
        )
    }
}

@Composable
fun InsightRecordActionMenuContent(
    onDeleteClick: () -> Unit,
    onMoveToRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(DialogBackground)
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MenuFlatButton(text = "기록 삭제", contentColor = DangerColor, onClick = onDeleteClick)
        MenuFlatButton(text = "기록으로 이동", contentColor = TextColor, onClick = onMoveToRecordClick)
    }
}

/** 삭제 확인 모달 — 대상 기록의 시각/경과시간을 연동해 표시 */
@Composable
fun InsightRecordDeleteConfirmDialog(
    recordedAt: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        InsightRecordDeleteConfirmContent(
            recordedAt = recordedAt,
            onCancel = onDismiss,
            onConfirmDelete = onConfirmDelete
        )
    }
}

@Composable
fun InsightRecordDeleteConfirmContent(
    recordedAt: String,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(DialogBackground)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "정말로 기록을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(recordedAt.toClockText(), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextColor)
            Spacer(Modifier.width(16.dp))
            Text(recordedAt.toElapsedText(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextColor)
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuFlatButton(
                text = "취소",
                contentColor = TextColor,
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
            MenuFlatButton(
                text = "삭제",
                contentColor = DangerColor,
                onClick = onConfirmDelete,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/** 점 세 개 메뉴 항목 — 테두리 있는 흰색 버튼 (Frame 43) */
@Composable
private fun MenuFlatButton(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, contentColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = contentColor, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun InsightRecordActionMenuPreview() {
    InsightRecordActionMenuContent(
        onDeleteClick = {},
        onMoveToRecordClick = {},
        modifier = Modifier.padding(40.dp)
    )
}