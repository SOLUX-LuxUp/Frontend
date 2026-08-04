package com.solux.luxup.taptap.feature.team.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.util.category.CategoryDeleteConfirmDialog
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.CategoryEditDialog
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.feature.home.main.util.RecordCompleteBanner
import com.solux.luxup.taptap.feature.team.data.mockSuggestionsTogether
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import com.solux.luxup.taptap.feature.team.presentation.components.RecentRecordBanner
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonDeleteConfirmDialog
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonList
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonMenuDialog
import com.solux.luxup.taptap.feature.team.presentation.components.TeamFirstButtonSection
import com.solux.luxup.taptap.feature.team.presentation.components.TeamQuickCreatePopup
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private const val NO_TAP_PERMISSION_MESSAGE_MEMBER = "이 버튼을 누를 권한이 없어요.\n버튼 정보에서 권한을 요청해 보세요."
// 팀장은 버튼 수정 권한 정책을 스스로 바꿀 수 있어 "요청"이 아니라 "수정"을 안내한다.
private const val NO_TAP_PERMISSION_MESSAGE_OWNER = "이 버튼을 누를 권한이 없어요.\n버튼 정보에서 권한을 수정해 보세요."

/**
 * 활동 탭 진입점. ViewModel을 붙이고 토스트를 처리한다.
 */
@Composable
fun TeamActivityRoute(
    teamId: Long,
    currentUserId: Long,
    onNavigateToTimeline: (TeamButton) -> Unit,
    onNavigateToInfo: (TeamButton) -> Unit,
    modifier: Modifier = Modifier,
    showQuickCreatePopup: Boolean = false,
    onQuickCreatePopupDismissed: () -> Unit = {},
    /** 팀 생성 직후 진입인지 — true면 추천 섹션을 처음부터 노출한다 */
    showTemplateSection: Boolean = false,
    onFirstRecordMade: () -> Unit = {},
) {
    val viewModel: TeamActivityViewModel = hiltViewModel<TeamActivityViewModel, TeamActivityViewModel.Factory>(
        creationCallback = { factory -> factory.create(teamId, currentUserId) },
    )
    val context = LocalContext.current

    // 이 방문 동안 기록이 한 번이라도 생기면 위로 알려서 추천 섹션을 닫는다
    LaunchedEffect(viewModel.hasRecordedThisVisit) {
        if (viewModel.hasRecordedThisVisit) onFirstRecordMade()
    }

    // 버튼 생성/수정 화면에 다녀와도 이 화면(NavBackStackEntry)의 ViewModel 인스턴스는
    // 재사용되어 init{} 이 다시 안 불리므로, 화면이 다시 보일 때(RESUME)마다 새로고침한다.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 기록 성공 등 짧은 안내는 토스트로
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.consumeToast()
        }
    }

    TeamActivityScreen(
        buttons = viewModel.buttons,
        isTeamOwner = viewModel.isTeamOwner,
        suggestions = viewModel.suggestions,                    // 추가
        onSuggestionClick = viewModel::createFromSuggestion,
        categories = viewModel.categories,
        onCreateCategory = viewModel::createCategory,
        onRenameCategory = viewModel::renameCategory,
        onDeleteCategory = viewModel::deleteCategory,
        showQuickCreatePopup = showQuickCreatePopup,
        onQuickCreatePopupDismissed = onQuickCreatePopupDismissed,
        showTemplateSection = showTemplateSection,
        onRecordTap = viewModel::recordTap,
        onDeleteButton = viewModel::deleteButton,
        showRecordCompleteBanner = viewModel.showRecordCompleteBanner,
        onCancelRecord = viewModel::cancelPendingRecord,
        onNavigateToTimeline = onNavigateToTimeline,
        onNavigateToInfo = onNavigateToInfo,
        errorMessage = viewModel.errorMessage,
        onErrorConsumed = viewModel::consumeError,
        modifier = modifier,
    )
}

/**
 * 팀 활동 탭 — 팀 공유 버튼 목록.
 *
 * 카드 진입 플로우
 *  - 짧게 누르기 → 탭 기록 (권한 없으면 안내 모달)
 *  - 길게 누르기 → 버튼 타임라인
 *  - ⋮ → 버튼 정보 / 버튼 삭제
 *
 * 버튼 수정은 버튼 정보 화면의 우측 상단 아이콘으로 진입한다.
 *
 * 팀 생성 직후에는 [showTemplateSection]이 true로 들어와 "첫 번째 버튼을 만들어보세요" 추천 섹션이
 * 자동 노출된다. 이 방문 동안 기록이 하나라도 생기면 사라지고(onFirstRecordMade), 이후 버튼을 다시
 * 0개로 되돌려도 다시 뜨지 않는다 — 다른 방문에서는 애초에 showTemplateSection이 false로 들어온다.
 * 건너뛴 팀은 suggestions가 비어 있어 제목만 노출된다.
 *
 * 그와 별개로 "+" → "빠르게 만들기"는 언제든 [TeamQuickCreatePopup] 팝업으로 추천 목록을 보여준다.
 */
@Composable
fun TeamActivityScreen(
    buttons: List<TeamButton>,
    isTeamOwner: Boolean = false,
    suggestions: List<TeamButtonSuggestion> = emptyList(),
    onSuggestionClick: (TeamButtonSuggestion) -> Unit = {},
    categories: List<TeamButtonCategory> = emptyList(),
    onCreateCategory: (String) -> Unit = {},
    onRenameCategory: (categoryId: Long, newName: String) -> Unit = { _, _ -> },
    onDeleteCategory: (categoryId: Long, deleteButtonsToo: Boolean) -> Unit = { _, _ -> },
    showQuickCreatePopup: Boolean = false,
    onQuickCreatePopupDismissed: () -> Unit = {},
    showTemplateSection: Boolean = false,
    onRecordTap: (TeamButton) -> Unit = {},
    onDeleteButton: (TeamButton) -> Unit = {},
    showRecordCompleteBanner: Boolean = false,
    onCancelRecord: () -> Unit = {},
    onNavigateToTimeline: (TeamButton) -> Unit = {},
    onNavigateToInfo: (TeamButton) -> Unit = {},
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ⋮ 메뉴 / 삭제 확인 모달의 대상 버튼
    var menuTarget by remember { mutableStateOf<TeamButton?>(null) }
    var deleteTarget by remember { mutableStateOf<TeamButton?>(null) }
    var localNotice by remember { mutableStateOf<String?>(null) }
    val notice = localNotice ?: errorMessage

    var showCategoryEditDialog by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<TeamButtonCategory?>(null) }

    // "ALL" 또는 null이면 전체 노출, 그 외에는 선택된 카테고리 이름으로 categoryId를 찾아 필터링
    var categoryFilter by remember { mutableStateOf<String?>(null) }
    val filteredButtons = remember(buttons, categoryFilter, categories) {
        if (categoryFilter == null || categoryFilter == "ALL") {
            buttons
        } else {
            val categoryId = categories.find { it.categoryName == categoryFilter }?.categoryId
            buttons.filter { it.categoryId == categoryId }
        }
    }

    val listState = rememberLazyListState()

    Box(modifier = modifier) {
    Column {
        // 최근 기록 배너 — 서버가 latestRecord.recordedAt 최신순으로 정렬해서 주므로
        // 기록이 있는 첫 번째 버튼이 곧 가장 최근 기록이다
        val recentButton = buttons.firstOrNull { it.latestRecord != null }
        Column(modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)) {
            Text("최근 기록", fontSize = 14.sp, color = Color(0xFF6D6D6D), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(11.dp))
            if (recentButton != null) {
                RecentRecordBanner(recentButton)
            } else {
                EmptyRecordBanner()
            }
            Spacer(Modifier.height(30.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryDropdown(
                categories = categories.map { it.categoryName } + "ALL",
                onCategorySelected = { categoryFilter = it },
                onManageCategoriesClick = { showCategoryEditDialog = true },
            )
            SearchBar(modifier = Modifier.weight(1f), placeholder = "버튼 검색")
        }

        when {
            buttons.isEmpty() && showTemplateSection -> {
                TeamFirstButtonSection(
                    suggestions = suggestions,
                    onSuggestionClick = onSuggestionClick,
                    isFirstButton = true,
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp),
                )
            }

            buttons.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("버튼이 없어요", fontSize = 14.sp, color = Color(0xFF8A94A6))
                }
            }

            filteredButtons.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("이 카테고리에 버튼이 없어요", fontSize = 14.sp, color = Color(0xFF8A94A6))
                }
            }

            else -> {
                TeamButtonList(
                    buttons = filteredButtons,
                    state = listState,
                    onButtonClick = { button ->
                        if (button.hasTapPermission) onRecordTap(button)
                        else localNotice = if (isTeamOwner) NO_TAP_PERMISSION_MESSAGE_OWNER else NO_TAP_PERMISSION_MESSAGE_MEMBER
                    },
                    onButtonLongClick = onNavigateToTimeline,
                    onButtonMenuClick = { menuTarget = it },
                    header = if (showTemplateSection) {
                        {
                            TeamFirstButtonSection(
                                suggestions = suggestions,
                                onSuggestionClick = onSuggestionClick,
                                isFirstButton = false,
                            )
                        }
                    } else null,
                )
            }
        }
    }

        // 활동 탭 콘텐츠는 팀 상세 상단바/탭 아래에 중첩돼 있어, 일반 Box 정렬로는 화면 진짜
        // 최상단(개인 파트와 같은 위치)에 띄울 수 없다. Popup은 창 좌표 기준이라 중첩과 무관하게
        // 항상 화면 맨 위에 뜬다.
        Popup(
            alignment = Alignment.TopCenter,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        ) {
            AnimatedVisibility(
                visible = showRecordCompleteBanner,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                RecordCompleteBanner(onCancel = onCancelRecord)
            }
        }
    }

    if (showQuickCreatePopup) {
        TeamQuickCreatePopup(
            suggestions = suggestions,
            onDismiss = onQuickCreatePopupDismissed,
            onSuggestionClick = { suggestion ->
                onSuggestionClick(suggestion)
                onQuickCreatePopupDismissed()
            },
        )
    }

    menuTarget?.let { target ->
        TeamButtonMenuDialog(
            onDismiss = { menuTarget = null },
            onSelectInfo = {
                menuTarget = null
                onNavigateToInfo(target)
            },
            onSelectDelete = {
                menuTarget = null
                deleteTarget = target
            },
        )
    }

    deleteTarget?.let { target ->
        TeamButtonDeleteConfirmDialog(
            buttonName = target.buttonName,
            onDismiss = { deleteTarget = null },
            onConfirmDelete = {
                deleteTarget = null
                onDeleteButton(target)
            },
        )
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

    if (showCategoryEditDialog) {
        Dialog(
            onDismissRequest = { showCategoryEditDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CategoryEditDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                categories = categories.map { it.categoryName },
                onDismiss = { showCategoryEditDialog = false },
                onCreate = onCreateCategory,
                onRename = { oldName, newName ->
                    categories.find { it.categoryName == oldName }?.let { onRenameCategory(it.categoryId, newName) }
                },
                onRequestDelete = { name -> categoryPendingDelete = categories.find { it.categoryName == name } },
            )
        }
    }

    categoryPendingDelete?.let { category ->
        Dialog(
            onDismissRequest = { categoryPendingDelete = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CategoryDeleteConfirmDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                category = category.categoryName,
                onDismiss = { categoryPendingDelete = null },
                onConfirmDelete = { deleteButtonsToo ->
                    onDeleteCategory(category.categoryId, deleteButtonsToo)
                    categoryPendingDelete = null
                },
            )
        }
    }
}

/**
 * 기록이 아직 없을 때의 최근 기록 배너.
 * RecentRecordBanner와 높이를 맞추기 위해 동일한 padding·아이콘 크기 구조를 따른다.
 * (아이콘 원 80dp + vertical padding 24dp × 2 = 128dp)
 */
@Composable
private fun EmptyRecordBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.height(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "아직 기록이 없어요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

/** RecentRecordBanner의 아이콘 크기와 동일하게 유지할 것 */
private val RECENT_RECORD_ICON_SIZE = 48.dp

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@Composable
private fun TeamActivityScreenPreview() {
    TeamActivityScreen(buttons = mockTeamButtons)
}

/** 템플릿을 선택한 팀의 초기 화면 (버튼 없음) */
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@Composable
private fun TeamActivityScreenTemplatePreview() {
    TeamActivityScreen(
        buttons = emptyList(),
        suggestions = mockSuggestionsTogether,
    )
}

/** 템플릿을 건너뛴 팀의 초기 화면 (버튼 없음) */
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@Composable
private fun TeamActivityScreenSkipPreview() {
    TeamActivityScreen(
        buttons = emptyList(),
        suggestions = emptyList(),
    )
}

/** "+" → "빠르게 만들기" 팝업이 떠 있는 상태 */
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@Composable
private fun TeamActivityScreenQuickCreatePreview() {
    TeamActivityScreen(
        buttons = mockTeamButtons,
        suggestions = mockSuggestionsTogether,
        showQuickCreatePopup = true,
    )
}
