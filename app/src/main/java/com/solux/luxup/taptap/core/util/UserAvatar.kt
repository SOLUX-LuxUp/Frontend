package com.solux.luxup.taptap.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.ProfileIcon

@Composable
fun UserAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val localIcon = ProfileIcon.parseToken(imageUrl)

    if (localIcon != null) {
        // 로컬 아이콘 선택("icon:{key}" 토큰) — 원 포함 SVG → tint 미지정으로 원본 유지
        Icon(
            painter = painterResource(localIcon.resId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = modifier.size(size)
        )
    } else if (imageUrl.isNullOrBlank()) {
        // 플레이스홀더 = ic_profile (원 포함 SVG → tint 미지정으로 원본 유지)
        Icon(
            painter = painterResource(R.drawable.ic_profile_thin),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = modifier.size(size)
        )
    } else {
        Box(
            modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size).clip(CircleShape),
                loading = {
                    Icon(painterResource(R.drawable.ic_profile_thin), null,
                        tint = Color.Unspecified, modifier = Modifier.size(size))
                },
                error = {
                    Icon(painterResource(R.drawable.ic_profile_thin), null,
                        tint = Color.Unspecified, modifier = Modifier.size(size))
                }
            )
        }
    }
}