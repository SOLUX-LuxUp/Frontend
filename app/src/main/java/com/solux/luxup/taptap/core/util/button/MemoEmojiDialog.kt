package com.solux.luxup.taptap.core.util.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.emoji2.emojipicker.EmojiPickerView
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private const val MemoMaxLength = 100
private const val EmojiPickerMax = 1
private val EmojiOptions = listOf("❤\uFE0F", "\uD83D\uDC94", "\uD83D\uDE0A", "\uD83D\uDE02", "\uD83D\uDE34", "\uD83D\uDE23")

private enum class MemoEmojiTab(val label: String) {
    MEMO("메모"),
    EMOJI("이모지")
}

// 타임라인 기록에 메모/이모지를 추가하는 팝업 ("메모" / "이모지" 탭 전환형)
@Composable
fun MemoEmojiDialog(
    onDismiss: () -> Unit,
    onSave: (memo: String?, emoji: String?) -> Unit,
    modifier: Modifier = Modifier,
    initialMemo: String? = null,
    initialEmoji: String? = null,
    customEmojis: List<String> = emptyList(),
    onAddCustomEmoji: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(if (initialEmoji != null) MemoEmojiTab.EMOJI else MemoEmojiTab.MEMO) }
    var memoText by remember { mutableStateOf(initialMemo.orEmpty()) }
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MemoEmojiTab.entries.forEach { tab ->
                MemoEmojiTabItem(
                    label = tab.label,
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        when (selectedTab) {
            MemoEmojiTab.MEMO -> {
                OutlinedTextField(
                    value = memoText,
                    onValueChange = { if (it.length <= MemoMaxLength) memoText = it },
                    placeholder = { Text("메모를 입력하세요.", color = Color(0xFFB1B1B1)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.White
                    )
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "${memoText.length}/$MemoMaxLength",
                    fontSize = 13.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            MemoEmojiTab.EMOJI -> {
                EmojiGrid(
                    selectedEmoji = selectedEmoji,
                    onEmojiSelected = { selectedEmoji = it },
                    onClear = { selectedEmoji = null },
                    customEmojis = customEmojis,
                    onAddCustomEmoji = onAddCustomEmoji
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "${if (selectedEmoji != null) 1 else 0}/$EmojiPickerMax",
                    fontSize = 13.sp,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryDialogButton(
                text = "취소",
                textColor = Color(0xFFF6989C),
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            CategoryDialogButton(
                text = "저장",
                textColor = Color(0xFFACACAC),
                modifier = Modifier.weight(1f),
                onClick = {
                    onSave(memoText.ifBlank { null }, selectedEmoji)
                }
            )
        }
    }
}

@Composable
private fun MemoEmojiTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            label,
            fontSize = 18.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color(0xFF6D6D6D) else Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(15.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    if (selected) {
                        Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
                    } else {
                        SolidColor(Color(0xFFFFFFFF))
                    }
                )
        )
    }
}

private sealed interface EmojiCellSpec
private data object ClearCell : EmojiCellSpec
private data object AddCell : EmojiCellSpec
private data class EmojiOptionCell(val emoji: String) : EmojiCellSpec

@Composable
private fun EmojiGrid(
    selectedEmoji: String?,
    onEmojiSelected: (String) -> Unit,
    onClear: () -> Unit,
    customEmojis: List<String> = emptyList(),
    onAddCustomEmoji: (String) -> Unit = {}
) {
    var showEmojiPicker by remember { mutableStateOf(false) }
    val cells: List<EmojiCellSpec> = buildList {
        add(ClearCell)
        addAll(EmojiOptions.map(::EmojiOptionCell))
        // 피커에서 새로 고른 이모지는 프리셋에 없어도 계속 그리드에 남아있도록 추가
        addAll(customEmojis.filter { it !in EmojiOptions }.map(::EmojiOptionCell))
        // 프리셋에도 저장된 목록에도 없는, 지금 막 고른 이모지도 즉시 보이도록
        if (selectedEmoji != null && selectedEmoji !in EmojiOptions && selectedEmoji !in customEmojis) {
            add(EmojiOptionCell(selectedEmoji))
        }
        add(AddCell)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cells.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { cell ->
                    when (cell) {
                        is ClearCell -> ClearEmojiCell(onClick = onClear)
                        is AddCell -> AddEmojiCell(onClick = { showEmojiPicker = true })
                        is EmojiOptionCell -> EmojiOptionEmojiCell(
                            emoji = cell.emoji,
                            selected = selectedEmoji == cell.emoji,
                            onClick = { onEmojiSelected(cell.emoji) }
                        )
                    }
                }
            }
        }
    }

    if (showEmojiPicker) {
        EmojiPickerDialog(
            onEmojiPicked = {
                onEmojiSelected(it)
                onAddCustomEmoji(it)
                showEmojiPicker = false
            },
            onDismiss = { showEmojiPicker = false }
        )
    }
}

// 시스템 이모지 피커(androidx.emoji2 emoji2-emojipicker)를 다이얼로그로 띄워서 이모지를 선택받는다
@Composable
private fun EmojiPickerDialog(
    onEmojiPicked: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    EmojiPickerView(context).apply {
                        setOnEmojiPickedListener { onEmojiPicked(it.emoji) }
                    }
                }
            )
        }
    }
}

private val EmojiCellSize = 50.dp

// 이모지 선택 해제 셀. 점선 원 + X 아이콘
@Composable
private fun ClearEmojiCell(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(EmojiCellSize)
            .dashedCircleBorder(Color(0xFF989898))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "이모지 지우기",
            tint = Color(0xFF989898),
            modifier = Modifier.size(25.dp)
        )
    }
}

// 시스템 이모지 키보드로 이동하는 셀. 실선 원 + Plus 아이콘
@Composable
private fun AddEmojiCell(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(EmojiCellSize)
            .border(1.dp, Color(0xFF989898), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "이모지 더보기",
            tint = Color(0xFF989898),
            modifier = Modifier.size(25.dp)
        )
    }
}

// 실제 이모지 옵션 셀. 배경/테두리 없이 이모지만 표시하고, 선택 시에만 옅은 원형 배경으로 강조
@Composable
private fun EmojiOptionEmojiCell(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(EmojiCellSize)
            .clip(CircleShape)
            .background(if (selected) Color(0xFFE2E2E2) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 30.sp)
    }
}

private fun Modifier.dashedCircleBorder(color: Color, strokeWidth: Dp = 1.dp): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
    )
    drawCircle(color = color, radius = (size.minDimension - strokeWidth.toPx()) / 2f, style = stroke)
}