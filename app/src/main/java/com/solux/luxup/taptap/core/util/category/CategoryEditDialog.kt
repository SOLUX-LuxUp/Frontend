package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import kotlin.math.roundToInt

/**
 * "카테고리 수정" 팝업 - 카테고리 생성/이름 변경/삭제/순서 변경의 유일한 진입점.
 * 각 동작은 배치 저장 없이 즉시 onCreate/onRename/onRequestDelete/onReorder로 위쪽(실제 API 호출부)에 위임하고,
 * [categories]는 그 결과로 갱신된 최신 목록을 그대로 반영해 다시 그린다.
 */
@Composable
fun CategoryEditDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit,
    onRename: (oldName: String, newName: String) -> Unit,
    onRequestDelete: (category: String) -> Unit,
    onReorder: (newOrder: List<String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var categoryBeingRenamed by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(27.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp)
    ) {
        Text(
            text = "카테고리 수정",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White)
                .padding(top = 20.dp, bottom = 15.dp, start = 15.dp, end = 15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(50))
                    .clickable { showCreateDialog = true }
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("새로운 카테고리", fontSize = 14.sp, color = Color(0xFFB0B0B0))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "카테고리 추가",
                    tint = Color(0xFFB0B0B0),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(15.dp))

            CategoryOrderList(
                categories = categories,
                onReorder = onReorder,
                onEditClick = { categoryBeingRenamed = it },
                onDeleteClick = onRequestDelete
            )
        }
        Spacer(Modifier.height(30.dp))

        CategoryDialogButton(
            text = "닫기",
            textColor = Color(0xFFACACAC),
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss
        )
    }

    if (showCreateDialog) {
        Dialog(
            onDismissRequest = { showCreateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryCreateDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    onCreate(name)
                    showCreateDialog = false
                }
            )
        }
    }

    categoryBeingRenamed?.let { original ->
        Dialog(
            onDismissRequest = { categoryBeingRenamed = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryRenameDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                category = original,
                onDismiss = { categoryBeingRenamed = null },
                onRename = { newName ->
                    onRename(original, newName)
                    categoryBeingRenamed = null
                }
            )
        }
    }
}

/**
 * 왼쪽 ☰ 핸들을 눌러 끌면 그 자리에서 순서를 바꾼다. 모든 행의 높이가 같다고 보고
 * (행 높이 + spacing) 단위로 드래그 거리를 환산해 몇 칸을 넘었는지 계산한다.
 * 드래그가 끝나면(손을 뗄 때) 그 시점의 최종 순서로 [onReorder]를 한 번만 호출한다.
 */
@Composable
private fun CategoryOrderList(
    categories: List<String>,
    onReorder: (newOrder: List<String>) -> Unit,
    onEditClick: (category: String) -> Unit,
    onDeleteClick: (category: String) -> Unit,
) {
    var orderedCategories by remember(categories) { mutableStateOf(categories) }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var rowHeightPx by remember { mutableIntStateOf(0) }
    val spacingPx = with(LocalDensity.current) { 20.dp.roundToPx() }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        orderedCategories.forEachIndexed { index, category ->
            key(category) {
                var showMoreMenu by remember { mutableStateOf(false) }
                var moreIconWidthPx by remember { mutableIntStateOf(20) }
                val isDragging = draggingIndex == index

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { if (rowHeightPx == 0) rowHeightPx = it.size.height }
                        .graphicsLayer { translationY = if (isDragging) dragOffsetY else 0f }
                        .zIndex(if (isDragging) 1f else 0f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "순서 변경",
                            tint = Color(0xFFB1B1B1),
                            modifier = Modifier
                                .size(20.dp)
                                .pointerInput(category) {
                                    detectDragGestures(
                                        onDragStart = {
                                            draggingIndex = index
                                            dragOffsetY = 0f
                                        },
                                        onDragEnd = {
                                            draggingIndex = null
                                            dragOffsetY = 0f
                                            onReorder(orderedCategories)
                                        },
                                        onDragCancel = {
                                            draggingIndex = null
                                            dragOffsetY = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetY += dragAmount.y

                                            val from = draggingIndex ?: return@detectDragGestures
                                            val step = rowHeightPx + spacingPx
                                            if (step <= 0) return@detectDragGestures

                                            val moveBy = (dragOffsetY / step).roundToInt()
                                            if (moveBy == 0) return@detectDragGestures

                                            val to = (from + moveBy).coerceIn(0, orderedCategories.lastIndex)
                                            if (to != from) {
                                                orderedCategories = orderedCategories.toMutableList().apply {
                                                    add(to, removeAt(from))
                                                }
                                                draggingIndex = to
                                                dragOffsetY -= moveBy * step
                                            }
                                        }
                                    )
                                }
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(category, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                    }
                    Box(
                        modifier = Modifier.onGloballyPositioned { moreIconWidthPx = it.size.width }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "카테고리 옵션",
                            tint = Color(0xFFB1B1B1),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { showMoreMenu = true }
                        )

                        if (showMoreMenu) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = IntOffset(moreIconWidthPx + with(LocalDensity.current) { 10.dp.roundToPx() }, -15),
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                CategoryItemMoreMenu(
                                    onEditClick = {
                                        showMoreMenu = false
                                        onEditClick(category)
                                    },
                                    onDeleteClick = {
                                        showMoreMenu = false
                                        onDeleteClick(category)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 빨간 계열(삭제/위험) textColor면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다 */
private val DangerTextColors = setOf(Color(0xFFFF7B7B), Color(0xFFF6989C))

@Composable
internal fun CategoryDialogButton(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = textColor,
    onClick: () -> Unit
) {
    val isDanger = textColor in DangerTextColors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(borderColor, borderColor))

    Box(
        modifier = modifier
            .border(1.dp, borderBrush, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50.dp))
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else textColor,
        )
    }
}