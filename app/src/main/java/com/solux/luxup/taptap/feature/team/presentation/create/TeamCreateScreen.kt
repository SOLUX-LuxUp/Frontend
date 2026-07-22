package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm

/**
 * 팀 만들기 화면.
 *
 * 확인(✓) → POST /api/teams → teamId·inviteCode 수신 후 초대코드 공유 화면으로 이동.
 * 프로필 원 탭 → 이미지/아이콘 선택 모달 (호출부에서 처리)
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
        TeamCreateTopBar(
            onBackClick = onBackClick,
            onConfirmClick = onConfirmClick,
            isConfirmEnabled = !isSubmitting,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HorizontalPadding),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TeamProfileSlot(
                    imageUrl = form.teamImageUrl,
                    iconName = form.iconName,
                    iconColor = form.iconColor,
                    onClick = onProfileClick,
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            FieldLabel(text = "팀 이름")
            Spacer(modifier = Modifier.height(8.dp))
            TeamNameField(
                value = form.teamName,
                onValueChange = {
                    onTeamNameChange(it.take(TeamCreateForm.MAX_TEAM_NAME_LENGTH))
                },
            )

            Spacer(modifier = Modifier.height(28.dp))

            FieldLabel(text = "인원 제한")
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "최대", fontSize = 15.sp, color = LabelColor)
                MaxMemberPicker(
                    value = form.maxMember,
                    onValueChange = onMaxMemberChange,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Text(text = "명", fontSize = 15.sp, color = LabelColor)
            }
        }
    }
}

@Composable
private fun TeamCreateTopBar(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    isConfirmEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = HorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        BackArrow(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        Text(
            text = "팀 만들기",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextColor,
        )

        ConfirmButton(
            onClick = onConfirmClick,
            enabled = isConfirmEnabled,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

/**
 * 팀 프로필. 이미지 / 아이콘 / 미설정 3분기.
 * 탭하면 "이미지로 설정 / 아이콘으로 설정" 모달이 열린다.
 */
@Composable
private fun TeamProfileSlot(
    imageUrl: String?,
    iconName: String?,
    iconColor: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ProfileSize)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, ProfileBorderColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            imageUrl != null -> AsyncImage(
                model = imageUrl,
                contentDescription = "팀 이미지",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            iconName != null -> Icon(
                painter = painterResource(TeamIcon.from(iconName).resId),
                contentDescription = null,
                tint = IconColor.from(iconColor).color,
                modifier = Modifier.size(ProfileIconSize),
            )

            else -> Text(
                text = "+",
                fontSize = 28.sp,
                color = PlaceholderColor,
            )
        }
    }
}

@Composable
private fun FieldLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = LabelColor,
        modifier = modifier,
    )
}

@Composable
private fun TeamNameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 15.sp, color = TextColor),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
        )
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
    val options = TeamCreateForm.MAX_MEMBER_OPTIONS
    val itemHeightPx = with(LocalDensity.current) { PickerItemHeight.toPx() }

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
            .height(PickerItemHeight * VISIBLE_ITEM_COUNT)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp)),
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = PickerItemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(options) { option ->
                val isSelected = option == options.getOrNull(selectedIndex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PickerItemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.toString(),
                        fontSize = if (isSelected) 16.sp else 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextColor else UnselectedGray,
                    )
                }
            }
        }
    }
}

/** TODO: ic_arrow_back SVG 확정 시 painterResource 로 교체 */
@Composable
private fun BackArrow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val stroke = 2.dp.toPx()
            drawLine(
                color = LabelColor,
                start = Offset(size.width, size.height / 2),
                end = Offset(0f, size.height / 2),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = LabelColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * 0.4f, size.height * 0.15f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = LabelColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * 0.4f, size.height * 0.85f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** TODO: ic_check SVG 확정 시 painterResource 로 교체 */
@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
                } else {
                    Brush.horizontalGradient(listOf(DisabledGray, DisabledGray))
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            val stroke = 2.dp.toPx()
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height * 0.55f),
                end = Offset(size.width * 0.38f, size.height * 0.85f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width * 0.38f, size.height * 0.85f),
                end = Offset(size.width, size.height * 0.2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

private const val VISIBLE_ITEM_COUNT = 3

// TODO: Figma Inspect 값 확정 후 교체
private val HorizontalPadding = 40.dp
private val ProfileSize = 120.dp
private val ProfileIconSize = 56.dp
private val PickerItemHeight = 32.dp

private val TextColor = Color(0xFF1A1A1A)
private val LabelColor = Color(0xFF616161)
private val PlaceholderColor = Color(0xFFBDBDBD)
private val BorderColor = Color(0xFFE0E0E0)
private val ProfileBorderColor = Color(0xFFEDEDED)
private val UnselectedGray = Color(0xFFBDBDBD)
private val DisabledGray = Color(0xFFBDBDBD)
private val GradientStart = Color(0xFF4BB4FF)
private val GradientEnd = Color(0xFF2085FF)

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