package com.solux.luxup.taptap.feature.team.presentation.button.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.ChevronDownIcon
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon

private val BorderColor = Color(0xFFE5E7EB)
private val LabelColor = Color(0xFF6B7280)
private val TextColor = Color(0xFF111827)
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
    ) {
        BackArrowIcon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable(onClick = onBack),
        )
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor,
            modifier = Modifier.align(Alignment.Center),
        )
        ConfirmCheckIcon(
            enabled = confirmEnabled,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable(enabled = confirmEnabled, onClick = onConfirm),
        )
    }
}

@Composable
fun FormLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 13.sp,
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
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
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
            textStyle = TextStyle(fontSize = 15.sp, color = TextColor),
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
            .heightIn(min = 96.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(14.dp),
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, fontSize = 15.sp, color = PlaceholderColor)
        }
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = TextStyle(fontSize = 15.sp, color = TextColor),
            cursorBrush = SolidColor(Color(0xFF2D8CFF)),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "${value.length}/$maxLength",
            fontSize = 12.sp,
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
    width: androidx.compose.ui.unit.Dp = 150.dp,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .width(width)
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = labelOf(selected), fontSize = 14.sp, color = TextColor)
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