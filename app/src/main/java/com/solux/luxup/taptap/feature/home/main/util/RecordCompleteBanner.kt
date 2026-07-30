package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 습관 버튼을 한 번 탭해 기록을 남기면 화면 상단에 잠깐(3초) 뜨는 완료 알림.
 * "취소"를 누르면 방금 남긴 기록을 되돌린다.
 */
@Composable
fun RecordCompleteBanner(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("기록 완료!", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
        Spacer(Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(Color.White.copy(alpha = 0.7f)),
        )
        Spacer(Modifier.width(20.dp))
        Text(
            "취소",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.clickable(onClick = onCancel),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RecordCompleteBannerPreview() {
    RecordCompleteBanner(onCancel = {}, modifier = Modifier.padding(24.dp))
}