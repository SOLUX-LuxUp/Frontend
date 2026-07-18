package com.solux.luxup.taptap.core.ui.theme

import androidx.compose.runtime.Composable
import com.solux.luxup.taptap.ui.theme.TapTapTheme

/** 프리뷰 전용 래퍼 — TapTapTheme으로 감싸 Pretendard 등 테마가 프리뷰에도 적용되게 함 */
@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    TapTapTheme {
        content()
    }
}