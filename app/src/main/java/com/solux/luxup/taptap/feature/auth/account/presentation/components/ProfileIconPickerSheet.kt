package com.solux.luxup.taptap.feature.auth.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.ProfileIcon

/**
 * 프로필 아이콘을 고르는 하단 팝업 (설정 → 아바타 톱니 → "아이콘으로 설정").
 * 팀 프로필 아이콘 피커([com.solux.luxup.taptap.feature.team.presentation.create.TeamIconPickerSheet])와 달리
 * 색상은 아이콘에 이미 포함되어 있어 아이콘만 고른다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileIconPickerSheet(
    onDismiss: () -> Unit,
    onConfirm: (ProfileIcon) -> Unit,
    initialIcon: ProfileIcon? = null,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null,
    ) {
        ProfileIconPickerContent(
            onConfirm = onConfirm,
            initialIcon = initialIcon,
        )
    }
}

/** ModalBottomSheet은 preview가 불가하므로 내용만 분리 */
@Composable
fun ProfileIconPickerContent(
    onConfirm: (ProfileIcon) -> Unit,
    modifier: Modifier = Modifier,
    initialIcon: ProfileIcon? = null,
) {
    var selectedIcon by remember { mutableStateOf(initialIcon ?: ProfileIcon.DEFAULT) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Spacer(Modifier.height(0.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
        ) {
            Text(
                text = "프로필 선택",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D),
                modifier = Modifier.align(Alignment.Center),
            )
            ConfirmCheckIcon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .clickable { onConfirm(selectedIcon) },
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "아이콘",
            fontSize = 14.sp,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.padding(horizontal = 40.dp),
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally),
        ) {
            ProfileIcon.all.forEach { icon ->
                val isSelected = icon == selectedIcon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .figmaDropShadow(cornerRadius = 32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, Color(0xFF2085FF), CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { selectedIcon = icon },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(icon.resId),
                        contentDescription = icon.key,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ProfileIconPickerContentPreview() {
    PreviewContainer {
        ProfileIconPickerContent(onConfirm = {})
    }
}