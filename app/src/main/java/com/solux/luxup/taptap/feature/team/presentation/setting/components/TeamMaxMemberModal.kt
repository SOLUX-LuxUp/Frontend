package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.TeamSizePolicy
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 최대 인원을 고르는 모달. 5단위 스텝, 5~30.
 * 저장을 눌러야 반영되고 취소하면 변경되지 않는다.
 *
 * @param minSelectable 고를 수 있는 최소값. 팀 설정에서 인원을 줄일 때
 *  현재 팀원 수보다 작게는 못 줄이기 때문에 필요하다.
 *  (서버가 maxMember < memberCount 를 400 으로 막는다)
 */
@Composable
fun TeamMaxMemberModal(
    currentValue: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    minSelectable: Int = 0,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamMaxMemberModalContent(
            currentValue = currentValue,
            onDismiss = onDismiss,
            onSave = onSave,
            minSelectable = minSelectable,
        )
    }
}

/** Dialog 는 preview 가 불가하므로 내용만 분리 */
@Composable
fun TeamMaxMemberModalContent(
    currentValue: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minSelectable: Int = 0,
) {
    // 하한 미만은 아예 목록에서 뺀다
    val options = remember(minSelectable) {
        TeamSizePolicy.selectableOptions(minSelectable)
    }

    // 저장 전까지는 화면에 반영하지 않는다
    var pendingValue by remember {
        mutableIntStateOf(currentValue.coerceAtLeast(options.firstOrNull() ?: currentValue))
    }

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
                options = options,
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
            MaxMemberPillButton(
                label = "취소",
                contentColor = Color(0xFFF08A8A),
                isDanger = true,
                onClick = onDismiss,
            )
            MaxMemberPillButton(
                label = "저장",
                contentColor = Color(0xFF9BA6B5),
                onClick = { onSave(pendingValue) },
            )
        }
    }
}

/** 5단위 휠. 가운데 항목이 선택값이며 위아래로 이웃 값이 흐리게 보인다. */
@Composable
private fun MaxMemberWheel(
    value: Int,
    options: List<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 30.dp
    val visibleCount = 3

    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    val initialIndex = remember(options) { options.indexOf(value).coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val selectedIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (offset > itemHeightPx / 2) index + 1 else index
        }
    }

    LaunchedEffect(selectedIndex) {
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

        // 선택 영역 표시 — 가운데 칸 위아래 구분선. 팀 생성 화면의 MaxMemberPicker와 동일하게
        // 좌우로 살짝 들어가 있다(꽉 채우지 않음).
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
        ) {
            Spacer(Modifier.height(itemHeight))
            SelectionDivider()
            Spacer(Modifier.height(itemHeight))
            SelectionDivider()
        }
    }
}

@Composable
private fun SelectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF6D6D6D)),
    )
}

/** isDanger(빨간 계열 버튼)면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다 */
@Composable
private fun MaxMemberPillButton(
    label: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    // 눌렀을 때는 테두리도 배경과 같은 색으로 — 필 안으로 자연스럽게 묻힌다
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(contentColor, contentColor))

    Box(
        modifier = modifier
            .size(width = 85.dp, height = 36.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else contentColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "제약 없음")
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

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "현재 7명 — 5 제외")
@Composable
private fun TeamMaxMemberModalMinPreview() {
    PreviewContainer {
        TeamMaxMemberModalContent(
            currentValue = 10,
            minSelectable = 7,
            onDismiss = {},
            onSave = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}