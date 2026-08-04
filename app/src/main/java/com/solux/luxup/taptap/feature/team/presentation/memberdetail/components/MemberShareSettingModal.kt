package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberSharedButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
@Composable
fun MemberShareSettingModal(
    buttons: List<TeamMemberSharedButton>,
    onDismiss: () -> Unit,                                   // 취소 / 바깥 클릭
    onSave: (List<TeamMemberSharedButton>) -> Unit,         // 저장 → 수정된 전체 (나중에 8.2.1 PATCH)
) {
    // 각 buttonId의 체크 상태 (isShared 초기값으로 시작, 로컬 편집)
    val checkState = remember {
        mutableStateMapOf<Long, Boolean>().apply {
            buttons.forEach { put(it.buttonId, it.isShared) }
        }
    }
    var query by remember { mutableStateOf("") }
    val categoryNames = remember(buttons) { buttons.mapNotNull { it.categoryName }.distinct() }
    var categoryFilter by remember { mutableStateOf<String?>(null) }   // null = 전체
    val filtered = buttons.filter {
        (categoryFilter == null || it.categoryName == categoryFilter) &&
            it.buttonName.contains(query.trim(), ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        // 바깥: 연한 파랑 배경
        Column(
            modifier = Modifier
                .fillMaxWidth(0.90f)          // 화면 폭의 85%만
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFDEEFFF))        // ⚠️ 바깥 파랑 — 피그마 값 확인
                .padding(25.dp)
        ) {
            // 제목
            Text(
                text = "공유할 버튼",
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))

            // 안쪽: 흰 카드 (검색 자리 + 체크리스트)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(14.dp)
            ) {
                // 카테고리 필터 + 검색
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShareCategoryFilterDropdown(
                        categories = categoryNames,
                        selected = categoryFilter,
                        onSelect = { categoryFilter = it },
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF2F2F2))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "검색",
                                fontFamily = Pretendard,
                                fontSize = 12.sp,
                                color = Color(0xFFB1B1B1)
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 12.sp,
                                color = Color(0xFF6D6D6D),
                            ),
                            cursorBrush = SolidColor(Color(0xFF2085FF)),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))

                // 체크 리스트
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    filtered.forEach { button ->
                        ShareButtonCheckItem(
                            buttonName = button.buttonName,
                            checked = checkState[button.buttonId] ?: false,
                            onToggle = {
                                checkState[button.buttonId] = !(checkState[button.buttonId] ?: false)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 취소 / 저장
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModalButton(
                    text = "취소",
                    textColor = Color(0xFFFF6B6B),
                    borderColor = Color(0xFFFF6B6B),
                    bgColor = Color.White,
                    isDanger = true,
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                )
                ModalButton(
                    text = "저장",
                    textColor = Color(0xFF8A8A8A),
                    borderColor = Color(0xFFD8D8D8),
                    bgColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val result = buttons.map {
                            it.copy(isShared = checkState[it.buttonId] ?: it.isShared)
                        }
                        onSave(result)
                    }
                )
            }
        }
    }
}

/** "ALL ▼" 카테고리 필터 드롭다운. 카테고리 관리 진입점은 없이 필터 용도로만 쓴다. */
@Composable
private fun ShareCategoryFilterDropdown(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Box {
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { isExpanded = !isExpanded }
                .onGloballyPositioned { headerHeightPx = it.size.height },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected ?: "ALL",
                fontFamily = Pretendard,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = if (isExpanded) "▲" else "▼",
                fontSize = 10.sp,
                color = Color(0xFF6D6D6D),
            )
        }

        if (isExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, headerHeightPx),
                onDismissRequest = { isExpanded = false },
                properties = PopupProperties(dismissOnClickOutside = true),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .border(0.5.dp, Color(0xFFE5E5E5), RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    val options = listOf<String?>(null) + categories
                    options.forEach { option ->
                        Text(
                            text = option ?: "ALL",
                            fontFamily = Pretendard,
                            fontSize = 13.sp,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {
                                    onSelect(option)
                                    isExpanded = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareButtonCheckItem(
    buttonName: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onToggle() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 자리 (임시 회색 원)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = buttonName,
            fontFamily = Pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.weight(1f)
        )
        // 체크박스 17×17, radius 2, 체크 시 그라데이션
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(RoundedCornerShape(2.dp))
                .then(
                    if (checked)
                        Modifier.background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                            )
                        )
                    else
                        Modifier
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD8D8D8), RoundedCornerShape(2.dp))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val strokeWidth = 2.dp.toPx()
                    val w = size.width
                    val h = size.height
                    // 체크마크 두 획: 왼쪽 아래로 내려갔다 오른쪽 위로
                    val path = Path().apply {
                        moveTo(w * 0.15f, h * 0.55f)
                        lineTo(w * 0.42f, h * 0.80f)
                        lineTo(w * 0.85f, h * 0.25f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}

/** isDanger(빨간 계열)면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다 */
@Composable
private fun ModalButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    bgColor: Color = Color.White,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(bgColor, bgColor))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(borderColor, borderColor))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text, fontFamily = Pretendard, fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else textColor,
        )
    }
}

@Preview
@Composable
private fun MemberShareSettingModalPreview() {
    MemberShareSettingModal(
        buttons = listOf(
            TeamMemberSharedButton(5, "일기 쓰기", "diary", "#FFC107", 1, "루틴", true),
            TeamMemberSharedButton(6, "코드 수정", "code", "#2085FF", 2, "업무", true),
            TeamMemberSharedButton(7, "필기하기", "note", "#3357FF", null, null, false),
            TeamMemberSharedButton(8, "단톡 연락", "chat", "#4BB4FF", 2, "업무", true),
            TeamMemberSharedButton(9, "운동 하기", "run", "#90F525", 3, "건강", false),
        ),
        onDismiss = {},
        onSave = {}
    )
}