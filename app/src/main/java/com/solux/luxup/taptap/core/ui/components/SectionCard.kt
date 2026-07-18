package com.solux.luxup.taptap.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow

/**
 * 공용 섹션 카드 — 흰 배경 + 둥근 모서리 + 그림자 (+ 선택적 테두리)
 * 팀원 상세, 팀 인사이트 섹션 등 공용
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .figmaDropShadow(cornerRadius = cornerRadius)
            .clip(shape)
            .background(Color.White)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}