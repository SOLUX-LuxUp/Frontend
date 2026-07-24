package com.solux.luxup.taptap.feature.insight.daily.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.data.MockInsightDaily
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTimelineItem
import com.solux.luxup.taptap.feature.insight.daily.util.InsightBackHeader
import com.solux.luxup.taptap.feature.insight.daily.util.InsightDateNav
import com.solux.luxup.taptap.feature.insight.daily.util.InsightRecordActionMenu
import com.solux.luxup.taptap.feature.insight.daily.util.InsightRecordDeleteConfirmDialog
import com.solux.luxup.taptap.feature.insight.daily.util.InsightTimelineRow

private val GroupLabelColor = Color(0xFF6D6D6D)
private val EmptyStateColor = Color(0xFFB0B0B0)
private val MoreDotsColor = Color(0xFFB1B1B1)

/**
 * 타임라인 전체보기 — [InsightDailyScreen]의 "타임라인 전체보기"에서 진입.
 * 각 행의 "..." → 기록 삭제 / 기록으로 이동
 */
@Composable
fun InsightTimelineAllScreen(
    targetDate: String,
    timeline: List<InsightTimelineItem>,
    onBack: () -> Unit,
    onNavigateToButtonDetail: (buttonId: Long) -> Unit,
    onDeleteRecord: (InsightTimelineItem) -> Unit,
    modifier: Modifier = Modifier,
    onPrevDay: () -> Unit = {},
    onNextDay: () -> Unit = {}
) {
    var actionTarget by remember { mutableStateOf<InsightTimelineItem?>(null) }
    var deleteTarget by remember { mutableStateOf<InsightTimelineItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        Spacer(Modifier.height(70.dp))
        InsightBackHeader(title = "레포트", onBack = onBack)
        Spacer(Modifier.height(20.dp))
        InsightDateNav(targetDate = targetDate, onPrevDay = onPrevDay, onNextDay = onNextDay)
        Spacer(Modifier.height(20.dp))

        if (timeline.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("아직 기록이 없어요", fontSize = 14.sp, color = EmptyStateColor)
            }
            return@Column
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = "타임라인",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GroupLabelColor
            )
            Spacer(Modifier.height(5.dp))

            val sorted = timeline.sortedByDescending { it.recordedAt }
            sorted.forEachIndexed { index, item ->
                InsightTimelineRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == sorted.lastIndex,
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_more),
                            contentDescription = "더보기",
                            tint = MoreDotsColor,
                            modifier = Modifier
                                .clickable { actionTarget = item }
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    actionTarget?.let { target ->
        InsightRecordActionMenu(
            onDismiss = { actionTarget = null },
            onDeleteClick = {
                actionTarget = null
                deleteTarget = target
            },
            onMoveToRecordClick = {
                actionTarget = null
                onNavigateToButtonDetail(target.buttonId)
            }
        )
    }

    deleteTarget?.let { target ->
        InsightRecordDeleteConfirmDialog(
            recordedAt = target.recordedAt,
            onDismiss = { deleteTarget = null },
            onConfirmDelete = {
                deleteTarget = null
                onDeleteRecord(target)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 700)
@Composable
private fun InsightTimelineAllScreenPreview() {
    PreviewContainer {
        InsightTimelineAllScreen(
            targetDate = MockInsightDaily.targetDate,
            timeline = MockInsightDaily.timeline,
            onBack = {},
            onNavigateToButtonDetail = {},
            onDeleteRecord = {}
        )
    }
}