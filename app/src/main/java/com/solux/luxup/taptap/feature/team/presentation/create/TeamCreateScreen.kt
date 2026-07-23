package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle

/**
 * 팀 만들기 화면.
 *
 * 확인(✓) → POST /api/teams → teamId·inviteCode 수신 후 초대코드 공유 화면으로 이동.
 * 프로필 원 탭 → 이미지/아이콘 선택 모달 (호출부에서 처리)
 *
 * 색상·치수는 사용처에 직접 기입. Figma 값 확정 시 해당 위치에서 수정.
 */
@Composable
fun TeamCreateScreen(
    form: TeamCreateForm,
    onTeamNameChange: (String) -> Unit,
    onMaxMemberChange: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // 상단바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            BackArrowIcon(
                tint = Color(0xFFB1B1B1),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(31.02.dp)
                    .clickable(onClick = onBackClick),
            )

            Text(
                text = "팀 만들기",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
            )

            ConfirmCheckIcon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .clickable(onClick = onConfirmClick),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TeamProfileCircle(
                    imageUrl = form.teamImageUrl,
                    iconName = form.iconName,
                    iconColor = form.iconColor,
                    onClick = onProfileClick,
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // 팀 이름
            Text(
                text = "팀 이름",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF616161),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = form.teamName,
                    onValueChange = {
                        onTeamNameChange(it.take(TeamCreateForm.MAX_TEAM_NAME_LENGTH))
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF1A1A1A)),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 인원 제한
            Text(
                text = "인원 제한",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF616161),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "최대", fontSize = 15.sp, color = Color(0xFF616161))
                MaxMemberPicker(
                    value = form.maxMember,
                    onValueChange = onMaxMemberChange,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Text(text = "명", fontSize = 15.sp, color = Color(0xFF616161))
            }
        }
    }
}


/**
 * 최대 인원 휠 피커. 5단위 스텝, 5~30.
 * 가운데 항목이 선택값이며 위아래로 이웃 값이 흐리게 보인다.
 */
@Composable
fun MaxMemberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 32.dp
    val visibleCount = 3

    val options = TeamCreateForm.MAX_MEMBER_OPTIONS
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    val initialIndex = remember { options.indexOf(value).coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val selectedIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (offset > itemHeightPx / 2) index + 1 else index
        }
    }

    LaunchedEffect(selectedIndex) {
        options.getOrNull(selectedIndex)?.let { selected ->
            if (selected != value) onValueChange(selected)
        }
    }

    Box(
        modifier = modifier
            .width(64.dp)
            .height(itemHeight * visibleCount)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(options) { option ->
                val isSelected = option == options.getOrNull(selectedIndex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.toString(),
                        fontSize = if (isSelected) 16.sp else 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFBDBDBD),
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TeamCreateScreenEmptyPreview() {
    PreviewContainer {
        TeamCreateScreen(
            form = TeamCreateForm(),
            onTeamNameChange = {},
            onMaxMemberChange = {},
            onProfileClick = {},
            onBackClick = {},
            onConfirmClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TeamCreateScreenFilledPreview() {
    PreviewContainer {
        TeamCreateScreen(
            form = TeamCreateForm(
                teamName = "새로운 팀1",
                iconName = "heart",
                iconColor = "cyan",
                maxMember = 15,
            ),
            onTeamNameChange = {},
            onMaxMemberChange = {},
            onProfileClick = {},
            onBackClick = {},
            onConfirmClick = {},
        )
    }
}