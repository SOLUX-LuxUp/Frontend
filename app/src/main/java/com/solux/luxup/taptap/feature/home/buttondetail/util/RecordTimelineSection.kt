package com.solux.luxup.taptap.feature.home.buttondetail.util

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
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordEntry
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun RecordTimelineSection(
    group: TimelineGroup,
    onItemClick: (ButtonRecordEntry) -> Unit,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis()
) {
    Column(modifier = modifier.fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Text(group.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(0.dp))
        group.items.forEachIndexed { index, entry ->
            RecordTimelineItem(
                entry = entry,
                showConnectorAbove = index != 0,
                showConnectorBelow = index != group.items.lastIndex,
                onClick = { onItemClick(entry) },
                nowMillis = nowMillis
            )
        }
    }
}

private val MarkerAnchorHeight = 70.dp
private val MarkerSlotSize = 40.dp
private val ConnectorGap = 6.dp

@Composable
private fun RecordTimelineItem(
    entry: ButtonRecordEntry,
    showConnectorAbove: Boolean,
    showConnectorBelow: Boolean,
    onClick: () -> Unit,
    nowMillis: Long = System.currentTimeMillis()
) {
    // 메모 유무에 따라 위쪽 행 높이 자체는 다르지만(메모 없음 70dp / 있음 35dp), 마커/커넥터는 고정된 MarkerAnchorHeight를 기준으로
    // 배치해서 메모 유무나 첫 항목(위쪽 선 없음) 여부와 상관없이 위쪽 padding이 항상 동일하게 유지되도록 한다
    val rowHeight = if (entry.memo != null) 25.dp else 70.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
            .padding(horizontal = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight()
        ) {
            // 이모지/점 실제 크기와 무관하게 항상 같은 위치에서 선이 시작하도록 고정 슬롯 크기 기준으로 간격을 잡는다
            val connectorClearance = MarkerAnchorHeight / 2 + MarkerSlotSize / 2 + ConnectorGap
            if (showConnectorAbove) {
                // 위쪽 선은 고정된 MarkerAnchorHeight 구간 안에서만 생기므로 높이를 그 값으로 계산 (메모로 늘어난 아래쪽 길이에 영향받지 않도록)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .height(MarkerAnchorHeight - connectorClearance)
                        .width(1.dp)
                        .background(Color(0xFFD0D0D0))
                )
            }
            if (showConnectorBelow) {
                // 아래쪽 선은 메모 유무와 상관없이 항목 전체 높이(fillMaxHeight)만큼 다음 항목까지 이어지도록
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxHeight()
                        .padding(top = connectorClearance)
                        .width(1.dp)
                        .background(Color(0xFFD0D0D0))
                )
            }
            val markerSize = if (entry.emoji != null) 40.dp else 10.dp
            val markerTopOffset = (MarkerAnchorHeight - markerSize) / 2
            if (entry.emoji != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = markerTopOffset)
                        .size(markerSize),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.emoji,
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
                // 메모로 항목 전체 높이가 61dp(marker 하단 여백) 밑으로 줄어들면 아래쪽 커넥터 선이 그릴 공간이 사라지므로 최소 높이를 보장한다
                .heightIn(min = MarkerAnchorHeight),
            // 라벨/점/시간 행과 메모 사이는 붙어있고, 남는 여백은 이 블록 전체 위쪽에 남도록 아래로 붙인다
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRowRelativeText(entry.recordedAt, nowMillis),
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    modifier = Modifier.width(60.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (index < 2) BlueGradientStart else BlueGradientEnd)
                            )
                        }
                    }
                }
                Text(
                    text = formatRecordedAtDisplay(entry.recordedAt),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D)
                )
            }
            if (entry.memo != null) {
                Text(
                    text = entry.memo,
                    fontSize = 13.sp,
                    color = Color(0xFFB1B1B1)
                )
            }
        }
    }
}