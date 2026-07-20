package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonCreate
import com.solux.luxup.taptap.feature.team.model.TapPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormDropdown
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormLabel
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormMultilineField
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormTextField
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormTopBar

private val ScreenPadding = 24.dp // TODO: Dimens 상수화 시 정리

/**
 * 팀 버튼 만들기 (8.1.1)
 * 진입: TeamActivityScreen 우상단 + 아이콘
 *
 * NOTE: 아이콘 선택 / 멤버 권한 설정이 별도 화면이라 form 상태는 반드시 이 화면 바깥
 *  (nested nav graph 스코프 ViewModel 또는 savedStateHandle)에서 들고 있어야
 *  화면 이동 후 돌아왔을 때 입력값이 날아가지 않음.
 */
@Composable
fun TeamButtonCreateScreen(
    form: TeamButtonForm,
    categories: List<TeamButtonCategory>,
    confirmEnabled: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelect: (TeamButtonCategory) -> Unit,
    onTapPermissionChange: (TapPermission) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onIconClick: () -> Unit,
    onMemberSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        FormTopBar(
            title = "팀 버튼 만들기",
            onBack = onBack,
            onConfirm = onConfirm,
            confirmEnabled = confirmEnabled,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(16.dp))

            // 아이콘 미리보기 — 탭하면 아이콘 선택 화면
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
                    .shadow(6.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onIconClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(form.iconName)),
                    contentDescription = null,
                    tint = form.iconColor.color,
                    modifier = Modifier.size(52.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            FormLabel("이름")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = form.name,
                onValueChange = onNameChange,
                placeholder = "새로운 버튼",
                maxLength = TeamButtonForm.NAME_MAX,
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FormLabel("카테고리")
                FormDropdown(
                    selected = form.category,
                    options = categories,
                    labelOf = { it.name },
                    onSelect = onCategorySelect,
                )
            }

            Spacer(Modifier.height(20.dp))

            FormLabel("버튼 설명")
            Spacer(Modifier.height(8.dp))
            FormMultilineField(
                value = form.description,
                onValueChange = onDescriptionChange,
                maxLength = TeamButtonForm.DESCRIPTION_MAX,
                placeholder = "설명을 적어주세요",
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FormLabel("버튼 사용 권한")
                FormDropdown(
                    selected = form.tapPermission,
                    options = TapPermission.entries,
                    labelOf = { it.label },
                    onSelect = onTapPermissionChange,
                )
            }

            // custom일 때만 노출되는 멤버 선택 영역
            if (form.tapPermission == TapPermission.CUSTOM) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    FormLabel("사용할 수 있는 멤버")
                    Text(
                        text = "${form.allowedMemberCount}명",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(23.dp))
                        .clickable(onClick = onMemberSelectClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "멤버 선택",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        bottomBar()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TeamButtonCreateScreenPreview() {
    var form by remember { mutableStateOf(TeamButtonForm(iconName = "book")) }
    TeamButtonCreateScreen(
        form = form,
        categories = MockTeamButtonCreate.categories,
        confirmEnabled = form.canSubmit,
        onNameChange = { form = form.copy(name = it) },
        onDescriptionChange = { form = form.copy(description = it) },
        onCategorySelect = { form = form.copy(category = it) },
        onTapPermissionChange = { form = form.copy(tapPermission = it) },
        onBack = {},
        onConfirm = {},
        onIconClick = {},
        onMemberSelectClick = {},
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TeamButtonCreateScreenCustomPreview() {
    var form by remember {
        mutableStateOf(
            TeamButtonForm(
                name = "기획서 업데이트",
                iconName = "book",
                tapPermission = TapPermission.CUSTOM,
                allowedUserIds = listOf(1L, 2L, 3L),
            ),
        )
    }
    TeamButtonCreateScreen(
        form = form,
        categories = MockTeamButtonCreate.categories,
        confirmEnabled = form.canSubmit,
        onNameChange = { form = form.copy(name = it) },
        onDescriptionChange = { form = form.copy(description = it) },
        onCategorySelect = { form = form.copy(category = it) },
        onTapPermissionChange = { form = form.copy(tapPermission = it) },
        onBack = {},
        onConfirm = {},
        onIconClick = {},
        onMemberSelectClick = {},
    )
}