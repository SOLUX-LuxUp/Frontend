package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.components.OptionItem
import com.solux.luxup.taptap.core.ui.components.OptionSelectModal
import com.solux.luxup.taptap.core.ui.components.OptionSelectModalContent
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/**
 * 팀 프로필 설정 방식을 고르는 모달.
 * 공용 OptionSelectModal 을 그대로 사용하고 선택지만 정의한다.
 *
 * 이미지로 설정 → 갤러리 피커
 * 아이콘으로 설정 → TeamIconPickerSheet
 */
@Composable
fun TeamProfileSourceModal(
    onSelectImage: () -> Unit,
    onSelectIcon: () -> Unit,
    onDismiss: () -> Unit,
) {
    OptionSelectModal(
        options = listOf(
            OptionItem("이미지로 설정", onSelectImage),
            OptionItem("아이콘으로 설정", onSelectIcon),
        ),
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamProfileSourceModalPreview() {
    PreviewContainer {
        OptionSelectModalContent(
            options = listOf(
                OptionItem("이미지로 설정") {},
                OptionItem("아이콘으로 설정") {},
            ),
            onDismiss = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}