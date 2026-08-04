package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonDetail
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.PermissionStatus
import com.solux.luxup.taptap.feature.team.model.TapPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonDetail
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermissionRequest
import com.solux.luxup.taptap.feature.team.model.TeamMember

private val ScreenPadding = 40.dp

private const val NO_EDIT_PERMISSION_MESSAGE = "지금은 팀장만 버튼을 수정할 수 있어요."

/**
 * 버튼 정보 (8.1.3)
 * 진입: 버튼 목록 ⋮ → 버튼 정보
 *
 * 상단 섹션(이름·카테고리·설명·사용 권한)은 관리자·비관리자가 동일하고,
 * 하단 권한 섹션만 갈린다.
 *  - 관리자(팀장 또는 생성자): 권한이 있는 멤버 / 권한 요청 탭
 *  - 비관리자: 권한 요청 버튼 + 권한이 있는 멤버 목록
 */
@Composable
fun TeamButtonInfoScreen(
    detail: TeamButtonDetail,
    currentUserId: Long,
    teamId: Long,
    /** allowedUserIds를 팀원 목록과 매칭한 결과 */
    allowedMembers: List<TeamMember>,
    permissionRequests: List<TeamButtonPermissionRequest>,
    onBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onRequestPermission: () -> Unit,
    onApproveRequest: (Long) -> Unit,
    onDenyRequest: (Long) -> Unit,
    modifier: Modifier = Modifier,
    /** ViewModel에서 내려주는 에러 안내 (권한 요청 실패 등) */
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
    /** 관리자 권한 섹션의 초기 탭 (0 권한이 있는 멤버 / 1 권한 요청) */
    initialPermissionTab: Int = 0,
    bottomBar: @Composable () -> Unit = {},
) {
    val isManager = detail.isManager(currentUserId)

    // 화면 내부 안내(수정 권한 없음)와 ViewModel 에러를 같은 모달로 보여준다
    var localNotice by remember { mutableStateOf<String?>(null) }
    val notice = localNotice ?: errorMessage

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        InfoTopBar(
            isManager = isManager,
            onBack = onBack,
            onEditClick = {
                if (detail.canEdit) onNavigateToEdit()
                else localNotice = NO_EDIT_PERMISSION_MESSAGE
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(110.dp)
                    .figmaDropShadow(cornerRadius = 55.dp, blurRadius = 10.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(detail.iconName)),
                    contentDescription = null,
                    tint = IconColor.from(detail.iconColor).color,
                    modifier = Modifier.size(48.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            InfoLabel("이름")
            Spacer(Modifier.height(8.dp))
            ReadOnlyField(
                text = detail.buttonName,
                minHeight = 46.dp,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoLabel("카테고리")
                ReadOnlyField(
                    text = detail.categoryName ?: "No Category",
                    minHeight = 38.dp,
                    modifier = Modifier.width(213.dp),
                )
            }

            Spacer(Modifier.height(18.dp))

            InfoLabel("버튼 설명")
            Spacer(Modifier.height(8.dp))
            ReadOnlyDescriptionField(text = detail.description.orEmpty())

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoLabel("버튼 사용 권한")
                ReadOnlyField(
                    text = TapPermission.from(detail.tapPermission).label,
                    minHeight = 38.dp,
                    modifier = Modifier.width(213.dp),
                )
            }

            // tapPermission이 all이면 권한 개념이 없으므로 하단 섹션 자체를 숨긴다
            if (TapPermission.from(detail.tapPermission) == TapPermission.CUSTOM) {
                Spacer(Modifier.height(24.dp))

                if (isManager) {
                    ManagerPermissionSection(
                        initialTab = initialPermissionTab,
                        allowedMembers = allowedMembers,
                        permissionRequests = permissionRequests,
                        onApproveRequest = onApproveRequest,
                        onDenyRequest = onDenyRequest,
                    )
                } else {
                    MemberPermissionSection(
                        allowedMembers = allowedMembers,
                        permissionStatus = detail.myPermission.permissionStatus,
                        hasTapPermission = detail.myPermission.hasTapPermission,
                        onRequestPermission = onRequestPermission,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        bottomBar()
    }

    notice?.let { message ->
        NoticeDialog(
            message = message,
            onDismiss = {
                localNotice = null
                onErrorConsumed()
            },
        )
    }
}

@Composable
private fun InfoTopBar(
    isManager: Boolean,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding),
        ) {
            BackArrowIcon(
                tint = Color(0xFFB1B1B1),
                size = 31.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBack),
            )
            Text(
                text = "버튼 정보",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center),
            )
            // 관리자(팀장/생성자)에게는 항상 수정 아이콘을 보여준다 — 클릭 시 실제 권한(canEdit)에 따라
            // 수정 화면으로 가거나 "지금은 팀장만 수정 가능" 안내를 띄운다. 비관리자는 애초에
            // 수정 권한을 가질 일이 없으므로 아이콘 자체를 노출하지 않는다.
            if (isManager) {
                Image(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "버튼 수정",
                    colorFilter = ColorFilter.tint(Color(0xFF1A1A1A)),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(26.dp)
                        .clickable(onClick = onEditClick),
                )
            }
        }
    }
}

@Composable
private fun InfoLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF6D6D6D),
    )
}

@Composable
private fun ReadOnlyField(
    text: String,
    minHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(minHeight)
            .figmaDropShadow(cornerRadius = 10.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text = text, fontSize = 14.sp, color = Color(0xFF6D6D6D))
    }
}

/** 버튼 설명 — 읽기 전용이지만 시안대로 글자수 카운터를 표시한다 */
@Composable
private fun ReadOnlyDescriptionField(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(91.dp)
            .figmaDropShadow(cornerRadius = 10.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(14.dp),
    ) {
        Text(text = text, fontSize = 14.sp, color = Color(0xFF6D6D6D))
        Text(
            text = "${text.length}/${TeamButtonForm.DESCRIPTION_MAX}",
            fontSize = 12.sp,
            color = Color(0xFFB1B1B1),
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

/** 비관리자 — 권한 요청 버튼 + 권한 있는 멤버 목록 */
@Composable
private fun MemberPermissionSection(
    allowedMembers: List<TeamMember>,
    permissionStatus: String?,
    hasTapPermission: Boolean,
    onRequestPermission: () -> Unit,
) {
    Column {
        // 이미 권한이 있거나 요청 중이면 다시 요청할 수 없다 (409 방지)
        // 요청 가능하면 진한 회색, 이미 권한 있음/요청됨이면 연한 회색
        val isPending = permissionStatus == PermissionStatus.PENDING
        val requestEnabled = !hasTapPermission && !isPending

        val borderColor = if (requestEnabled) Color(0xFF6D6D6D) else Color(0xFFDADADA)
        val textColor = if (requestEnabled) Color(0xFF6D6D6D) else Color(0xFFB1B1B1)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
                .height(35.dp)
                .border(1.dp, borderColor, RoundedCornerShape(17.5.dp))
                .clickable(enabled = requestEnabled, onClick = onRequestPermission),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when {
                    hasTapPermission -> "권한 있음"
                    isPending -> "요청됨"
                    else -> "권한 요청"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
            )
        }

        Spacer(Modifier.height(16.dp))

        allowedMembers.forEach { member ->
            MemberRow(
                displayName = member.displayName,
                profileImageUrl = member.profileImageUrl,
            )
        }
    }
}

/** 관리자 — 권한이 있는 멤버 / 권한 요청 탭 */
@Composable
private fun ManagerPermissionSection(
    initialTab: Int,
    allowedMembers: List<TeamMember>,
    permissionRequests: List<TeamButtonPermissionRequest>,
    onApproveRequest: (Long) -> Unit,
    onDenyRequest: (Long) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            PermissionTab(
                text = "권한이 있는 멤버",
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f),
            )
            PermissionTab(
                text = "권한 요청",
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        if (selectedTab == 0) {
            allowedMembers.forEach { member ->
                MemberRow(
                    displayName = member.displayName,
                    profileImageUrl = member.profileImageUrl,
                )
            }
        } else {
            permissionRequests.forEach { request ->
                MemberRow(
                    displayName = request.displayName,
                    profileImageUrl = request.profileImageUrl,
                ) {
                    ApproveButton(onClick = { onApproveRequest(request.userId) })
                    Spacer(Modifier.width(8.dp))
                    DenyButton(onClick = { onDenyRequest(request.userId) })
                }
            }
        }
    }
}

@Composable
private fun PermissionTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color(0xFF6D6D6D) else Color(0xFFB1B1B1),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (selected) {
                        Brush.horizontalGradient(listOf(Color(0xFF4BB4FF), Color(0xFF2085FF)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFFDADADA), Color(0xFFDADADA)))
                    },
                ),
        )
    }
}

@Composable
private fun MemberRow(
    displayName: String,
    profileImageUrl: String?,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(imageUrl = profileImageUrl, size = 32.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            text = displayName,
            fontSize = 14.sp,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Composable
private fun ApproveButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFF2085FF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CheckMarkIcon(modifier = Modifier.size(14.dp), tint = Color.White, strokeRatio = 0.18f)
    }
}

@Composable
private fun DenyButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFFB1B1B1))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_x),
            contentDescription = "거부",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(12.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun TeamButtonInfoManagerPreview() {
    TeamButtonInfoScreen(
        detail = MockTeamButtonDetail.detail,
        currentUserId = 1L, // 생성자 = 관리자
        teamId = 1L,
        allowedMembers = MockTeamMembers.filter { it.userId in MockTeamButtonDetail.detail.allowedUserIds },
        permissionRequests = MockTeamButtonDetail.permissionRequests,
        onBack = {},
        onNavigateToEdit = {},
        onRequestPermission = {},
        onApproveRequest = {},
        onDenyRequest = {},
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun TeamButtonInfoMemberPreview() {
    TeamButtonInfoScreen(
        detail = MockTeamButtonDetail.detail.copy(
            isTeamOwner = false,
            canEdit = false,
            myPermission = MockTeamButtonDetail.detail.myPermission.copy(
                hasTapPermission = false,
                permissionStatus = null,
            ),
        ),
        currentUserId = 5L, // 일반 멤버
        teamId = 1L,
        allowedMembers = MockTeamMembers.filter { it.userId in MockTeamButtonDetail.detail.allowedUserIds },
        permissionRequests = emptyList(),
        onBack = {},
        onNavigateToEdit = {},
        onRequestPermission = {},
        onApproveRequest = {},
        onDenyRequest = {},
    )
}

/** 관리자 — 권한 요청 탭이 선택된 상태 */
@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun TeamButtonInfoManagerRequestTabPreview() {
    TeamButtonInfoScreen(
        detail = MockTeamButtonDetail.detail,
        currentUserId = 1L,
        teamId = 1L,
        allowedMembers = MockTeamMembers.filter { it.userId in MockTeamButtonDetail.detail.allowedUserIds },
        permissionRequests = MockTeamButtonDetail.permissionRequests,
        onBack = {},
        onNavigateToEdit = {},
        onRequestPermission = {},
        onApproveRequest = {},
        onDenyRequest = {},
        initialPermissionTab = 1,
    )
}

/** 비관리자 — 이미 권한 요청을 보낸 상태 (버튼이 "요청됨"으로 잠김) */
@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun TeamButtonInfoMemberPendingPreview() {
    TeamButtonInfoScreen(
        detail = MockTeamButtonDetail.detail.copy(
            isTeamOwner = false,
            canEdit = false,
            myPermission = MockTeamButtonDetail.detail.myPermission.copy(
                hasTapPermission = false,
                permissionStatus = PermissionStatus.PENDING,
            ),
        ),
        currentUserId = 5L,
        teamId = 1L,
        allowedMembers = MockTeamMembers.filter { it.userId in MockTeamButtonDetail.detail.allowedUserIds },
        permissionRequests = emptyList(),
        onBack = {},
        onNavigateToEdit = {},
        onRequestPermission = {},
        onApproveRequest = {},
        onDenyRequest = {},
    )
}