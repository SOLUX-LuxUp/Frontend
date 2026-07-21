package com.solux.luxup.taptap.feature.home.buttondetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.home.buttondetail.data.mockButtonDetail
import com.solux.luxup.taptap.feature.home.buttondetail.data.mockButtonRecordEntries
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonDetail
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordEntry
import com.solux.luxup.taptap.feature.home.buttondetail.util.MemoEmojiDialog
import com.solux.luxup.taptap.feature.home.buttondetail.util.RecentRecordBanner
import com.solux.luxup.taptap.feature.home.buttondetail.util.RecordActionMenu
import com.solux.luxup.taptap.feature.home.buttondetail.util.RecordTimelineSection
import com.solux.luxup.taptap.feature.home.buttondetail.util.groupRecordsByDay
import com.solux.luxup.taptap.feature.home.main.util.RecordDeleteConfirmDialog
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor

@Composable
fun ButtonDetailScreen(
    detail: ButtonDetail = mockButtonDetail,
    initialRecords: List<ButtonRecordEntry> = mockButtonRecordEntries,
    onNavigateBack: () -> Unit = {},
    onEditButton: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var records by remember(initialRecords) { mutableStateOf(initialRecords) }

    // 타임라인 항목을 눌렀을 때 뜨는 "기록 삭제"/"메모 추가" 액션 메뉴 대상
    var actionMenuTarget by remember { mutableStateOf<ButtonRecordEntry?>(null) }
    var recordPendingDelete by remember { mutableStateOf<ButtonRecordEntry?>(null) }
    var recordPendingMemo by remember { mutableStateOf<ButtonRecordEntry?>(null) }

    val latestRecord = remember(records) { records.maxByOrNull { it.recordedAt } }
    val timelineGroups = remember(records) { groupRecordsByDay(records) }

    Scaffold(
        modifier = modifier,
        containerColor = BaseWhiteColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BaseWhiteColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp)
        ) {
            Spacer(Modifier.height(15.dp))
            ButtonDetailTopBar(onNavigateBack = onNavigateBack, onEditClick = onEditButton)

            Spacer(Modifier.height(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .iconCircleDropShadow()
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(detail.iconRes),
                        contentDescription = null,
                        tint = detail.iconTint,
                        modifier = Modifier.size(27.dp)
                    )
                }
                Spacer(Modifier.width(18.dp))
                Text(detail.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
            }

            if (latestRecord != null) {
                Spacer(Modifier.height(30.dp))
                RecentRecordBanner(recordedAtIsoTimestamp = latestRecord.recordedAt)
            }

            Spacer(Modifier.height(30.dp))
            Text("타임라인", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(20.dp))

            timelineGroups.forEach { group ->
                RecordTimelineSection(
                    group = group,
                    onItemClick = { actionMenuTarget = it }
                )
                Spacer(Modifier.height(20.dp))
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    actionMenuTarget?.let { target ->
        Dialog(
            onDismissRequest = { actionMenuTarget = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            RecordActionMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                hasMemo = target.memo != null,
                onDeleteClick = {
                    actionMenuTarget = null
                    recordPendingDelete = target
                },
                onAddMemoClick = {
                    actionMenuTarget = null
                    recordPendingMemo = target
                }
            )
        }
    }

    recordPendingDelete?.let { target ->
        Dialog(
            onDismissRequest = { recordPendingDelete = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            RecordDeleteConfirmDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                recordedAtIsoTimestamp = target.recordedAt,
                onDismiss = { recordPendingDelete = null },
                onConfirmDelete = {
                    // TODO: 실제 기록 삭제 API 연결
                    records = records.filterNot { it.recordId == target.recordId }
                    recordPendingDelete = null
                }
            )
        }
    }

    recordPendingMemo?.let { target ->
        Dialog(
            onDismissRequest = { recordPendingMemo = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MemoEmojiDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                initialMemo = target.memo,
                initialEmoji = target.emoji,
                onDismiss = { recordPendingMemo = null },
                onSave = { memo, emoji ->
                    // TODO: 실제 메모/이모지 저장 API 연결
                    records = records.map {
                        if (it.recordId == target.recordId) it.copy(memo = memo, emoji = emoji) else it
                    }
                    recordPendingMemo = null
                }
            )
        }
    }
}

// 아이콘 원형 배지 드롭섀도 (X:0, Y:0, Blur:4.8, Color:#000000 24%)
private fun Modifier.iconCircleDropShadow(): Modifier = this.drawBehind {
    val shadowColor = Color.Black.copy(alpha = 0.24f).toArgb()
    val transparent = Color.Black.copy(alpha = 0f).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(4.8.dp.toPx(), 0f, 0f, shadowColor)
        canvas.drawCircle(
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.minDimension / 2f,
            paint = paint
        )
    }
}

@Composable
private fun ButtonDetailTopBar(
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            tint = Color(0xFFB1B1B1),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
                .clickable { onNavigateBack() }
        )
        Icon(
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = "버튼 수정",
            tint = Color(0xFF6D6D6D),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(28.dp)
                .clickable { onEditClick() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ButtonDetailScreenPreview() {
    ButtonDetailScreen()
}