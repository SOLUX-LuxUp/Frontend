package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.emoji2.emojipicker.EmojiPickerView
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 팀 타임라인 기록 모달 3종.
 *
 *   기록 섹션 탭
 *     → TeamRecordActionMenu (기록 삭제 / 메모 추가·수정)
 *         → TeamRecordDeleteConfirmDialog
 *         → TeamMemoEmojiDialog
 *
 * 본인이 남긴 기록에서만 열리므로, 모달 자체는 권한을 따지지 않는다.
 * 개인 파트와 디자인은 같지만 home 패키지 의존을 만들지 않으려고 팀 쪽에 따로 둔다.
 */

private val DangerColor = Color(0xFFF6989C)
private val TextColor = Color(0xFF6D6D6D)
private val DialogBackground = Color(0xFFDEEFFF)

/** 팀 메모 최대 길이. TODO(백엔드): memo 길이 제한 확인 필요 */
private const val MemoMaxLength = 50
private const val EmojiMax = 1
private val EmojiOptions = listOf("❤\uFE0F", "\uD83D\uDC94", "\uD83D\uDE0A", "\uD83D\uDE02", "\uD83D\uDE34", "\uD83D\uDE23")

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

// ------------------------------------------------------- 메모 / 이모지 모달

private enum class MemoEmojiTab(val label: String) {
    MEMO("메모"),
    EMOJI("이모지"),
}

@Composable
fun TeamMemoEmojiDialog(
    record: TeamButtonTimelineRecord,
    onDismiss: () -> Unit,
    onSave: (memo: String?, emoji: String?) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamMemoEmojiContent(
            initialMemo = record.memo,
            initialEmoji = record.emoji,
            onDismiss = onDismiss,
            onSave = onSave,
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}

@Composable
fun TeamMemoEmojiContent(
    onDismiss: () -> Unit,
    onSave: (memo: String?, emoji: String?) -> Unit,
    modifier: Modifier = Modifier,
    initialMemo: String? = null,
    initialEmoji: String? = null,
) {
    var selectedTab by remember {
        mutableStateOf(if (initialEmoji != null) MemoEmojiTab.EMOJI else MemoEmojiTab.MEMO)
    }
    var memoText by remember { mutableStateOf(initialMemo.orEmpty()) }
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }

    Column(
        modifier = modifier
            .width(215.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(Color(0xFFDEF0FF))
            .padding(horizontal = 15.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MemoEmojiTab.entries.forEach { tab ->
                TabItem(
                    label = tab.label,
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            MemoEmojiTab.MEMO -> {
                OutlinedTextField(
                    value = memoText,
                    onValueChange = { if (it.length <= MemoMaxLength) memoText = it },
                    placeholder = { Text("메모를 입력하세요.", color = Color(0xFFB1B1B1)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(102.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.White,
                    ),
                )
                Counter(current = memoText.length, max = MemoMaxLength)
            }

            MemoEmojiTab.EMOJI -> {
                EmojiGrid(
                    selectedEmoji = selectedEmoji,
                    onEmojiSelected = { selectedEmoji = it },
                    onClear = { selectedEmoji = null },
                )
                Counter(current = if (selectedEmoji != null) 1 else 0, max = EmojiMax)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            DialogOutlinedButton(
                text = "취소",
                contentColor = Color(0xFFFF7B7B),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                height = 37.dp,
                fontSize = 18.sp,
                cornerRadius = 100.dp,
            )
            DialogOutlinedButton(
                text = "저장",
                contentColor = Color(0xFFACACAC),
                onClick = {
                    // 메모·이모지 둘 다 비우면 null로 보내 기존 값을 지운다
                    onSave(memoText.ifBlank { null }, selectedEmoji)
                },
                modifier = Modifier.weight(1f),
                height = 37.dp,
                fontSize = 18.sp,
                cornerRadius = 100.dp,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Counter(current: Int, max: Int) {
    Spacer(Modifier.height(10.dp))
    Text(
        "$current/$max",
        fontSize = 13.sp,
        color = TextColor,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Text(
            label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextColor,
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
                        SolidColor(Color.White)
                    },
                ),
        )
    }
}

// ------------------------------------------------------------- 이모지 그리드

private sealed interface EmojiCell
private data object ClearCell : EmojiCell
private data object AddCell : EmojiCell
private data class OptionCell(val emoji: String) : EmojiCell

@Composable
private fun EmojiGrid(
    selectedEmoji: String?,
    onEmojiSelected: (String) -> Unit,
    onClear: () -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    val cells: List<EmojiCell> = buildList {
        add(ClearCell)
        addAll(EmojiOptions.map(::OptionCell))
        // 프리셋에 없는 이모지를 피커에서 고른 경우에도 선택 상태가 보이도록
        if (selectedEmoji != null && selectedEmoji !in EmojiOptions) {
            add(OptionCell(selectedEmoji))
        }
        add(AddCell)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 고정 크기를 쓰면 모달 폭보다 넓어져 원이 찌그러지므로 4등분 + 정사각 비율로 잡는다
        cells.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { cell ->
                    val cellModifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                    when (cell) {
                        ClearCell -> ClearEmojiCell(onClick = onClear, modifier = cellModifier)
                        AddCell -> AddEmojiCell(onClick = { showPicker = true }, modifier = cellModifier)
                        is OptionCell -> OptionEmojiCell(
                            emoji = cell.emoji,
                            selected = selectedEmoji == cell.emoji,
                            onClick = { onEmojiSelected(cell.emoji) },
                            modifier = cellModifier,
                        )
                    }
                }
                // 마지막 줄이 4개보다 적을 때 셀 크기가 커지지 않도록 빈 칸을 채운다
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }

    if (showPicker) {
        EmojiPickerDialog(
            onEmojiPicked = {
                onEmojiSelected(it)
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }
}

/** 시스템 이모지 피커(androidx.emoji2) */
@Composable
private fun EmojiPickerDialog(
    onEmojiPicked: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    EmojiPickerView(context).apply {
                        setOnEmojiPickedListener { onEmojiPicked(it.emoji) }
                    }
                },
            )
        }
    }
}

@Composable
private fun ClearEmojiCell(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .dashedCircleBorder(Color(0xFF989898))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "이모지 지우기",
            tint = Color(0xFF989898),
            modifier = Modifier.size(25.dp),
        )
    }
}

@Composable
private fun AddEmojiCell(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Color(0xFF989898), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "이모지 더보기",
            tint = Color(0xFF989898),
            modifier = Modifier.size(25.dp),
        )
    }
}

@Composable
private fun OptionEmojiCell(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) Color(0xFFE2E2E2) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = 30.sp)
    }
}

private fun Modifier.dashedCircleBorder(color: Color, strokeWidth: Dp = 1.dp): Modifier =
    this.drawBehind {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f),
        )
        drawCircle(
            color = color,
            radius = (size.minDimension - strokeWidth.toPx()) / 2f,
            style = stroke,
        )
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

@Preview(name = "메모 탭", showBackground = true, widthDp = 390)
@Composable
private fun TeamMemoEmojiMemoPreview() {
    TeamMemoEmojiContent(
        onDismiss = {},
        onSave = { _, _ -> },
        modifier = Modifier.padding(40.dp),
    )
}

@Preview(name = "이모지 탭", showBackground = true, widthDp = 390)
@Composable
private fun TeamMemoEmojiEmojiPreview() {
    TeamMemoEmojiContent(
        onDismiss = {},
        onSave = { _, _ -> },
        initialEmoji = "\uD83D\uDE0A",
        modifier = Modifier.padding(40.dp),
    )
}