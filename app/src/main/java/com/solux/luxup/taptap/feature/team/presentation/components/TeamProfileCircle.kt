package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon

/**
 * 팀 프로필 원.
 * 팀 만들기 · 초대코드 공유 · 템플릿 선택 화면에서 공통 사용한다.
 *
 * 표시 우선순위: 이미지(teamImageUrl) → 아이콘(iconName + iconColor) → 미설정
 * 이미지와 아이콘은 택일이므로 둘 다 채워지는 경우는 없다.
 *
 * @param onClick 지정 시 탭 가능. 팀 만들기 화면에서만 사용한다
 */
@Composable
fun TeamProfileCircle(
    imageUrl: String?,
    iconName: String?,
    iconColor: String?,
    modifier: Modifier = Modifier,
    size: Dp = 138.dp,
    iconSize: Dp = 64.dp,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .figmaDropShadow(cornerRadius = size / 2)
            .clip(CircleShape)
            .background(Color.White)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            imageUrl != null -> AsyncImage(
                model = imageUrl,
                contentDescription = "팀 이미지",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            iconName != null -> Icon(
                painter = painterResource(TeamIcon.from(iconName).resId),
                contentDescription = null,
                tint = IconColor.from(iconColor).color,
                modifier = Modifier.size(iconSize),
            )

            else -> Icon(
                painter = painterResource(TeamIcon.DEFAULT.resId),
                contentDescription = null,
                tint = IconColor.DEFAULT.color,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 240, heightDp = 220)
@Composable
private fun TeamProfileCircleEmptyPreview() {
    PreviewContainer {
        TeamProfileCircle(
            imageUrl = null,
            iconName = null,
            iconColor = null,
            onClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 240, heightDp = 220)
@Composable
private fun TeamProfileCircleIconPreview() {
    PreviewContainer {
        TeamProfileCircle(
            imageUrl = null,
            iconName = "heart",
            iconColor = "cyan",
            modifier = Modifier.padding(24.dp),
        )
    }
}