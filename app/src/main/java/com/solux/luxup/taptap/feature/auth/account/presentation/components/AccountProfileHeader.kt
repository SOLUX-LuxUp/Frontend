package com.solux.luxup.taptap.feature.auth.account.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.auth.account.data.MockAccountUser
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser

/** 아바타 + 우하단 톱니(프로필 설정) 배지 + 닉네임. 설정/계정 정보 화면 상단에서 공용으로 사용 */
@Composable
fun AccountProfileHeader(
    user: AccountUser = MockAccountUser,
    onEditProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            UserAvatar(imageUrl = user.profileImageUrl, size = 120.dp)
            Icon(
                painter = painterResource(R.drawable.ic_setting),
                contentDescription = "프로필 설정",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 0.dp, y = 0.dp)
                    .size(50.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEditProfileClick,
                    ),
            )
        }
        Spacer(Modifier.width(20.dp))
        Text(
            text = user.nickname,
            fontFamily = Pretendard,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AccountProfileHeaderPreview() {
    AccountProfileHeader(modifier = Modifier.width(320.dp))
}