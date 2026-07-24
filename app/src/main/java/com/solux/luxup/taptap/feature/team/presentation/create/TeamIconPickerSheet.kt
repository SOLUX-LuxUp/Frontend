package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon

/**
 * 팀 프로필 아이콘 + 색상을 고르는 하단 팝업.
 *
 * 팀 버튼 아이콘 선택 화면(TeamIconSelectScreen)의 스타일을 따르되,
 * 하단 팝업이라 폭이 좁아 원 크기만 축소했다.
 * 팀 프로필 아이콘은 5종뿐이라 그리드 대신 가로 스크롤로 배치한다.
 *
 * 확인(✓) 시 iconName / iconColor 문자열이 전달되어 POST /api/teams 요청에 담긴다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamIconPickerSheet(
    onDismiss: () -> Unit,
    onConfirm: (iconName: String, iconColor: String) -> Unit,
    initialIconName: String? = null,
    initialIconColor: String? = null,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null,
    ) {
        TeamIconPickerContent(
            onConfirm = onConfirm,
            initialIconName = initialIconName,
            initialIconColor = initialIconColor,
        )
    }
}

/** ModalBottomSheet은 preview가 불가하므로 내용만 분리 */
@Composable
fun TeamIconPickerContent(
    onConfirm: (iconName: String, iconColor: String) -> Unit,
    modifier: Modifier = Modifier,
    initialIconName: String? = null,
    initialIconColor: String? = null,
) {
    var selectedIcon by remember {
        mutableStateOf(TeamIcon.from(initialIconName))
    }
    var selectedColor by remember {
        mutableStateOf(IconColor.from(initialIconColor))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Spacer(Modifier.height(24.dp))

        // 헤더 — 제목 가운데, 확인 우측
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "아이콘 선택",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center),
            )
            ConfirmCheckIcon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .clickable { onConfirm(selectedIcon.key, selectedColor.key) },
            )
        }

        Spacer(Modifier.height(28.dp))

        // 아이콘 — 5종 가로 스크롤
        Text(
            text = "아이콘",
            fontSize = 14.sp,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TeamIcon.all.forEach { item ->
                val isSelected = item == selectedIcon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .figmaDropShadow(cornerRadius = 30.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, selectedColor.color, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { selectedIcon = item },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(item.resId),
                        contentDescription = item.key,
                        tint = if (isSelected) selectedColor.color else Color(0xFF9E9E9E),
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE2E2E2)),
        )

        // 색상 — 12종 2행 × 6열
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text("색상", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(12.dp))

            IconColor.palette.chunked(6).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    row.forEach { token ->
                        val isSelected = token == selectedColor
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(token.color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) {
                                        Color(0xFF1A1A1A)
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = CircleShape,
                                )
                                .clickable { selectedColor = token },
                        )
                    }
                    repeat(6 - row.size) { Spacer(Modifier.size(44.dp)) }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TeamIconPickerContentPreview() {
    PreviewContainer {
        TeamIconPickerContent(onConfirm = { _, _ -> })
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TeamIconPickerContentSelectedPreview() {
    PreviewContainer {
        TeamIconPickerContent(
            onConfirm = { _, _ -> },
            initialIconName = "heart",
            initialIconColor = "pink",
        )
    }
}