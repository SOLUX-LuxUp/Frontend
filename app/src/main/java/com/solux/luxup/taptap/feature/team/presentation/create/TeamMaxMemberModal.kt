package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm

/**
 * 최대 인원을 고르는 모달.
 * 5단위 스텝, 5~30. 저장을 눌러야 반영되고 취소하면 변경되지 않는다.
 */
@Composable
fun TeamMaxMemberModal(
    currentValue: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamMaxMemberModalContent(
            currentValue = currentValue,
            onDismiss = onDismiss,
            onSave = onSave,
        )
    }
}

/** Dialog는 preview가 불가하므로 내용만 분리 */
@Composable
fun TeamMaxMemberModalContent(
    currentValue: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 저장 전까지는 화면에 반영하지 않는다
    var pendingValue by remember { mutableIntStateOf(currentValue) }

    Column(
        modifier = modifier
            .width(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFDCEBFB))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "최대 인원",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            MaxMemberWheel(
                value = pendingValue,
                onValueChange = { pendingValue = it },
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "명",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PillButton(
                label = "취소",
                contentColor = Color(0xFFF08A8A),
                onClick = onDismiss,
            )
            PillButton(
                label = "저장",
                contentColor = Color(0xFF9BA6B5),
                onClick = { onSave(pendingValue) },
            )
        }
    }
}

/**
 * 5단위 휠. 가운데 항목이 선택값이며 위아래로 이웃 값이 흐리게 보인다.
 */
@Composable
private fun MaxMemberWheel(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 30.dp
    val visibleCount = 3

    val options = TeamCreateForm.MAX_MEMBER_OPTIONS
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    val initialIndex = remember { options.indexOf(value).coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val selectedIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (offset > itemHeightPx / 2) index + 1 else index
        }
    }

    androidx.compose.runtime.LaunchedEffect(selectedIndex) {
        options.getOrNull(selectedIndex)?.let { selected ->
            if (selected != value) onValueChange(selected)
        }
    }

    Box(
        modifier = modifier
            .width(72.dp)
            .height(itemHeight * visibleCount)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(options) { option ->
                val isSelected = option == options.getOrNull(selectedIndex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.toString(),
                        fontSize = if (isSelected) 15.sp else 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFBDBDBD),
                    )
                }
            }
        }
    }
}

@Composable
private fun PillButton(
    label: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 85.dp, height = 36.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .border(1.dp, contentColor, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamMaxMemberModalPreview() {
    PreviewContainer {
        TeamMaxMemberModalContent(
            currentValue = 10,
            onDismiss = {},
            onSave = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}