package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormLabel
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormTextField
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormTopBar
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle

private val ScreenPadding = 40.dp // 전 화면 공통 좌우 여백

/**
 * 팀 만들기 (8.0.2)
 *
 * 확인(✓) → POST /api/teams → teamId·inviteCode 수신 후 초대코드 공유 화면으로 이동.
 * 프로필 원 탭 → 이미지/아이콘 선택 모달 (호출부에서 처리)
 *
 * 레이아웃은 팀 버튼 만들기(TeamButtonCreateScreen)와 통일한다.
 * 인원 제한은 화면 안 휠에서 바로 선택한다. (모달 방식은 TeamMaxMemberModal 에 보관)
 */
@Composable
fun TeamCreateScreen(
    form: TeamCreateForm,
    onTeamNameChange: (String) -> Unit,
    onMaxMemberChange: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        FormTopBar(
            title = "팀 만들기",
            onBack = onBack,
            onConfirm = onConfirm,
            confirmEnabled = !isSubmitting,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(63.dp))

            // 프로필 미리보기 — 탭하면 이미지/아이콘 선택 모달
            TeamProfileCircle(
                imageUrl = form.teamImageUrl,
                iconName = form.iconName,
                iconColor = form.iconColor,
                onClick = onProfileClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(Modifier.height(36.dp))

            FormLabel("팀 이름")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = form.teamName,
                onValueChange = onTeamNameChange,
                placeholder = "새로운 팀",
                maxLength = TeamCreateForm.MAX_TEAM_NAME_LENGTH,
            )

            Spacer(Modifier.height(20.dp))

            FormLabel("인원 제한")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "최대",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D),
                )
                MaxMemberPicker(
                    value = form.maxMember,
                    onValueChange = onMaxMemberChange,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
                Text(
                    text = "명",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D),
                )
            }

            Spacer(Modifier.height(40.dp))
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
    val itemHeight = 29.33.dp    // 88 / 3
    val visibleCount = 3
    val boxWidth = 98.dp

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
            .width(boxWidth)
            .height(itemHeight * visibleCount)
            .figmaDropShadow(cornerRadius = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
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
                        fontSize = if (isSelected) 16.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFB1B1B1),
                    )
                }
            }
        }

        // 선택 영역 표시 — 가운데 칸 위아래 구분선. 좌우로 살짝 들어가 있다
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
        ) {
            Spacer(Modifier.height(itemHeight))
            SelectionDivider()
            Spacer(Modifier.height(itemHeight))
            SelectionDivider()
        }
    }
}

@Composable
private fun SelectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF6D6D6D)),
    )
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TeamCreateScreenEmptyPreview() {
    PreviewContainer {
        TeamCreateScreen(
            form = TeamCreateForm(),
            onTeamNameChange = {},
            onMaxMemberChange = {},
            onProfileClick = {},
            onBack = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
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
            onBack = {},
            onConfirm = {},
        )
    }
}