package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.core.util.formatTimeOfDay
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import com.solux.luxup.taptap.feature.team.model.TimelineDayGroup
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 팀 타임라인 일자 섹션.
 *
 * 개인 파트의 RecordTimelineSection과 마커·커넥터 계산은 동일하고,
 * 우측에 기록한 유저 프로필이 붙는 만큼 가운데 점을 3개에서 1개로 줄였다.
 */
@Composable
fun TeamRecordTimelineSection(
    group: TimelineDayGroup,
    currentUserId: Long,
    onItemClick: (TeamButtonTimelineRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(group.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D6D6D))
        group.records.forEachIndexed { index, record ->
            TeamRecordTimelineItem(
                record = record,
                isMine = record.isMine(currentUserId),
                showConnectorAbove = index != 0,
                showConnectorBelow = index != group.records.lastIndex,
                onClick = { onItemClick(record) }
            )
        }
    }
}

private val MarkerAnchorHeight = 70.dp
private val MarkerSlotSize = 40.dp
private val ConnectorGap = 6.dp

@Composable
private fun TeamRecordTimelineItem(
    record: TeamButtonTimelineRecord,
    isMine: Boolean,
    showConnectorAbove: Boolean,
    showConnectorBelow: Boolean,
    onClick: () -> Unit
) {
    val rowHeight = if (record.memo != null) 25.dp else 70.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            // 본인이 남긴 기록에서만 액션 메뉴가 열린다
            .clickable(enabled = isMine) { onClick() }
            .padding(horizontal = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight()
        ) {
            val connectorClearance = MarkerAnchorHeight / 2 + MarkerSlotSize / 2 + ConnectorGap
            if (showConnectorAbove) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .height(MarkerAnchorHeight - connectorClearance)
                        .width(1.dp)
                        .background(Color(0xFFD0D0D0))
                )
            }
            if (showConnectorBelow) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxHeight()
                        .padding(top = connectorClearance)
                        .width(1.dp)
                        .background(Color(0xFFD0D0D0))
                )
            }
            val markerSize = if (record.emoji != null) 40.dp else 10.dp
            val markerTopOffset = (MarkerAnchorHeight - markerSize) / 2
            if (record.emoji != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = markerTopOffset)
                        .size(markerSize),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = record.emoji,
                        fontSize = 30.sp,
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = markerTopOffset)
                        .size(markerSize)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFD0D0D0), CircleShape)
                )
            }
        }
        Spacer(Modifier.width(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = MarkerAnchorHeight),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimeAgo(record.recordedAt),
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    modifier = Modifier.width(60.dp)
                )
                // 프로필 자리를 확보하기 위해 점은 1개만
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(BlueGradientStart)
                    )
                }
                Text(
                    text = formatTimeOfDay(record.recordedAt),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D)
                )
                Spacer(Modifier.width(12.dp))
                UserAvatar(imageUrl = record.recordedBy.profileImageUrl, size = 24.dp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = record.recordedBy.displayName,
                    fontSize = 13.sp,
                    color = Color(0xFF6D6D6D)
                )
            }
            record.memo?.let { memo ->
                // 기록자 프로필이 붙어 폭이 좁으므로 한 줄로 자른다.
                // 전체 내용은 메모 수정 모달에서 확인
                Text(
                    text = memo,
                    fontSize = 13.sp,
                    color = Color(0xFFB1B1B1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}