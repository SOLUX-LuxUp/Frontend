package com.solux.luxup.taptap.feature.team.presentation.button.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.ChevronDownIcon
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon

private val BorderColor = Color(0xFFE5E7EB)
private val LabelColor = Color(0xFF6D6D6D)
private val TextColor = Color(0xFF6D6D6D)
private val PlaceholderColor = Color(0xFFB0B3B8)

/** 뒤로 + 타이틀 + 우측 확인 체크로 구성된 공통 상단바 */
@Composable
fun FormTopBar(
    title: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 40.dp),
        ) {
            BackArrowIcon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(31.02.dp)
                    .clickable(onClick = onBack),
            )
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColor,
                modifier = Modifier.align(Alignment.Center),
            )
            ConfirmCheckIcon(
                enabled = confirmEnabled,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .clickable(enabled = confirmEnabled, onClick = onConfirm),
            )
        }
    }
}

@Composable
fun FormLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = LabelColor,
        modifier = modifier,
    )
}

/** 한 줄 입력 박스 */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLength: Int = Int.MAX_VALUE,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, fontSize = 15.sp, color = PlaceholderColor)
        }
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = TextColor),
            cursorBrush = SolidColor(Color(0xFF2D8CFF)),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** 여러 줄 입력 박스 + 우하단 글자수 카운터 */
@Composable
fun FormMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 91.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(14.dp),
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, fontSize = 15.sp, color = PlaceholderColor)
        }
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = TextStyle(fontSize = 14.sp, color = TextColor),
            cursorBrush = SolidColor(Color(0xFF2D8CFF)),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "${value.length}/$maxLength",
            fontSize = 13.sp,
            color = PlaceholderColor,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

/**
 * 오른쪽 정렬 드롭다운. 카테고리 / 버튼 사용 권한 공용.
 * NOTE: 정수민님 CategoryDropdown과 시각적으로 겹치는 부분이 있어,
 *       팀 쪽 폼이 확정되면 core/ui로 합칠지 논의 필요.
 */
@Composable
fun <T> FormDropdown(
    selected: T,
    options: List<T>,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 213.dp,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .width(width)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = labelOf(selected), fontSize = 15.sp, color = TextColor)
            ChevronDownIcon()
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labelOf(option), fontSize = 14.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * 카테고리 전용 드롭다운. 닫혀 있을 때는 [FormDropdown]과 동일한 213x38 박스,
 * 펼쳤을 때는 정수민님 CategorySelectDropdown과 동일한 팝업(카테고리 관리 진입점 포함)을 쓴다.
 */
@Composable
fun CategoryFormDropdown(
    selectedCategoryName: String?,
    categories: List<String>,
    onCategorySelected: (String?) -> Unit,
    onManageCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 213.dp,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .width(width)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { isExpanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = selectedCategoryName ?: com.solux.luxup.taptap.feature.team.model.TeamButtonCategory.NONE_LABEL,
                fontSize = 15.sp,
                color = TextColor,
            )
            ChevronDownIcon()
        }

        if (isExpanded) {
            androidx.compose.ui.window.Popup(
                alignment = Alignment.TopEnd,
                offset = androidx.compose.ui.unit.IntOffset(0, 0),
                onDismissRequest = { isExpanded = false },
                properties = androidx.compose.ui.window.PopupProperties(dismissOnClickOutside = true),
            ) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(5.dp))
                        .padding(12.dp),
                ) {
                    categories.forEach { category ->
                        Text(
                            text = category,
                            fontSize = 15.sp,
                            color = TextColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCategorySelected(category)
                                    isExpanded = false
                                }
                                .padding(vertical = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFD9D9D9))
                            .clickable {
                                isExpanded = false
                                onManageCategoriesClick()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = if (categories.isEmpty()) "+ ADD" else "카테고리 수정",
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TeamButtonFormComponentsPreview() {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("기획서 최신본으로 올리기") }
    var category by remember { mutableStateOf("No Category") }

    Column {
        FormTopBar(
            title = "팀 버튼 만들기",
            onBack = {},
            onConfirm = {},
        )

        Column(modifier = Modifier.padding(24.dp)) {
            FormLabel("이름")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "새로운 버튼",
                maxLength = 15,
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FormLabel("카테고리")
                FormDropdown(
                    selected = category,
                    options = listOf("No Category", "HEALTH", "ROUTINE", "WORK"),
                    labelOf = { it },
                    onSelect = { category = it },
                )
            }

            Spacer(Modifier.height(20.dp))

            FormLabel("버튼 설명")
            Spacer(Modifier.height(8.dp))
            FormMultilineField(
                value = description,
                onValueChange = { description = it },
                maxLength = 50,
                placeholder = "설명을 적어주세요",
            )
        }
    }
}

/** 확인 버튼 비활성 상태 */
@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FormTopBarDisabledPreview() {
    FormTopBar(
        title = "멤버 권한 설정",
        onBack = {},
        onConfirm = {},
        confirmEnabled = false,
    )
}