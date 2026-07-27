package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.solux.luxup.taptap.core.ui.components.OptionItem
import com.solux.luxup.taptap.core.ui.components.OptionSelectModal

/**
 * 팀 생성 플로우 라우트.
 *
 * 만들기 → 초대코드 공유 → 템플릿 선택 → 팀 상세(활동 탭)
 * 세 화면이 TeamCreateViewModel 을 공유한다.
 */
object TeamCreateRoute {
    const val GRAPH = "teamCreateGraph"

    const val FORM = "teamCreate/form"
    const val INVITE_CODE = "teamCreate/inviteCode"
    const val TEMPLATE = "teamCreate/template"
}

/**
 * @param onFinish 플로우 종료 후 이동할 곳. 생성된 teamId 를 전달한다
 */
fun NavGraphBuilder.teamCreateGraph(
    navController: NavController,
    onFinish: (Long) -> Unit,
) {
    navigation(
        route = TeamCreateRoute.GRAPH,
        startDestination = TeamCreateRoute.FORM,
    ) {
        composable(TeamCreateRoute.FORM) { backStackEntry ->
            val viewModel = backStackEntry.sharedCreateViewModel(navController)

            var showSourceModal by remember { mutableStateOf(false) }
            var showIconPicker by remember { mutableStateOf(false) }

            val photoPicker = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                uri?.let { viewModel.updateImage(it.toString()) }
            }

            TeamCreateScreen(
                form = viewModel.form,
                onTeamNameChange = viewModel::updateTeamName,
                onMaxMemberChange = viewModel::updateMaxMember,
                onProfileClick = { showSourceModal = true },
                onBack = { navController.popBackStack() },
                onConfirm = {
                    viewModel.createTeam {
                        navController.navigate(TeamCreateRoute.INVITE_CODE)
                    }
                },
                isSubmitting = viewModel.isSubmitting,
            )

            if (showSourceModal) {
                TeamProfileSourceModal(
                    onSelectImage = {
                        showSourceModal = false
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSelectIcon = {
                        showSourceModal = false
                        showIconPicker = true
                    },
                    onDismiss = { showSourceModal = false },
                )
            }

            if (showIconPicker) {
                TeamIconPickerSheet(
                    initialIconName = viewModel.form.iconName,
                    initialIconColor = viewModel.form.iconColor,
                    onDismiss = { showIconPicker = false },
                    onConfirm = { iconName, iconColor ->
                        viewModel.updateIcon(iconName, iconColor)
                        showIconPicker = false
                    },
                )
            }
        }

        composable(TeamCreateRoute.INVITE_CODE) { backStackEntry ->
            val viewModel = backStackEntry.sharedCreateViewModel(navController)
            val team = viewModel.createdTeam ?: return@composable
            val context = LocalContext.current

            TeamInviteCodeScreen(
                team = team,
                onShareClick = { code ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "TAPTAP에서 '${team.teamName}' 팀에 초대받았어요!\n" +
                                    "앱에서 아래 코드를 입력해 참여해보세요.\n\n$code",
                        )
                    }
                    context.startActivity(Intent.createChooser(intent, "팀 코드 공유하기"))
                },
                onConfirmClick = {
                    navController.navigate(TeamCreateRoute.TEMPLATE) {
                        // 팀이 이미 생성됐으므로 만들기 화면으로 돌아가지 못하게 한다
                        popUpTo(TeamCreateRoute.FORM) { inclusive = true }
                    }
                },
            )
        }

        composable(TeamCreateRoute.TEMPLATE) { backStackEntry ->
            val viewModel = backStackEntry.sharedCreateViewModel(navController)
            val team = viewModel.createdTeam ?: return@composable

            LaunchedEffect(Unit) { viewModel.loadTemplates() }

            TeamTemplateScreen(
                team = team,
                templates = viewModel.templates,
                onTemplateClick = { template ->
                    viewModel.applyTemplate(template.templateId) {
                        onFinish(team.teamId)
                    }
                },
                onSkipClick = {
                    viewModel.skipTemplate {
                        onFinish(team.teamId)
                    }
                },
            )
        }
    }
}

/** 중첩 그래프 스코프의 ViewModel — 세 화면이 같은 인스턴스를 공유한다 */
@androidx.compose.runtime.Composable
private fun androidx.navigation.NavBackStackEntry.sharedCreateViewModel(
    navController: NavController,
): TeamCreateViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(TeamCreateRoute.GRAPH)
    }
    return hiltViewModel(parentEntry)
}