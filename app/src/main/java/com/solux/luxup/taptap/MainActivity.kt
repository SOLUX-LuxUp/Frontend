package com.solux.luxup.taptap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavController
import com.solux.luxup.taptap.core.auth.TokenManager
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountInfoScreen
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountSettingsScreen
import com.solux.luxup.taptap.feature.auth.account.presentation.ChangePasswordScreen
import com.solux.luxup.taptap.feature.auth.login.presentation.LoginRoute
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupRoute
import com.solux.luxup.taptap.feature.auth.signup.presentation.signupGraph
import com.solux.luxup.taptap.feature.home.buttondetail.presentation.ButtonDetailScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.CreateButtonScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.CreateButtonViewModel
import com.solux.luxup.taptap.feature.home.main.util.presentation.IconSelectScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.MainHomeScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.MainHomeViewModel
import com.solux.luxup.taptap.feature.home.template.presentation.OnboardingTemplateRoute
import com.solux.luxup.taptap.feature.insight.daily.data.MockInsightDaily
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightDailyScreen
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightRatioAllScreen
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightTimelineAllScreen
import com.solux.luxup.taptap.feature.insight.daily.util.shiftDate
import com.solux.luxup.taptap.feature.insight.lifestyle.data.MockInsightLifestyle
import com.solux.luxup.taptap.feature.insight.lifestyle.presentation.InsightLifestyleScreen
import com.solux.luxup.taptap.feature.insight.monthly.data.MockInsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.presentation.InsightMonthlyScreen
import com.solux.luxup.taptap.feature.insight.monthly.util.shiftMonth
import com.solux.luxup.taptap.feature.insight.weekly.data.MockInsightWeekly
import com.solux.luxup.taptap.feature.insight.weekly.presentation.InsightWeeklyRatioAllScreen
import com.solux.luxup.taptap.feature.insight.weekly.presentation.InsightWeeklyScreen
import com.solux.luxup.taptap.feature.insight.weekly.util.shiftWeek
import com.solux.luxup.taptap.feature.notification.presentation.NotificationScreen
import com.solux.luxup.taptap.ui.theme.TapTapTheme
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashScreen
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashViewModel
import com.solux.luxup.taptap.feature.splash.presentation.SplashScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamDetailScreen
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonCreateRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonEditRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonInfoRoute
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonCreateGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonEditGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonInfoScreen
import com.solux.luxup.taptap.feature.team.presentation.button.timeline.TeamButtonTimelineRoute
import com.solux.luxup.taptap.feature.team.presentation.button.timeline.teamButtonTimelineScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamListScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.solux.luxup.taptap.feature.team.presentation.create.TeamCreateRoute
import com.solux.luxup.taptap.feature.team.presentation.create.teamCreateGraph
import com.solux.luxup.taptap.feature.team.presentation.setting.TeamSettingRoute
import com.solux.luxup.taptap.feature.team.presentation.setting.teamSettingGraph
import com.solux.luxup.taptap.feature.team.presentation.insight.TeamInsightViewModel
import com.solux.luxup.taptap.feature.team.presentation.insight.buttonall.TeamInsightButtonAllRoute
import com.solux.luxup.taptap.feature.team.presentation.insight.buttonall.TeamInsightButtonAllScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: 로그인 유저가 속한 팀 id로 교체 (현재는 임시 고정값)
private const val CURRENT_TEAM_ID = 1L

private fun NavController.navigateToTab(item: BottomNavItem) {
    val route = when (item) {
        BottomNavItem.HOME -> "mainHome"
        BottomNavItem.NOTIFICATION -> "notification"
        BottomNavItem.TEAM -> "teamList"
        BottomNavItem.SETTINGS -> "accountSettings"
        BottomNavItem.RECORD -> "insightDaily"
    }
    navigate(route) {
        popUpTo("mainHome") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 로그인 시 AuthRepository가 저장한 userId. 로그인 화면을 거치지 않고는 이 지점에 도달하지 않는다.
        val currentUserId = tokenManager.getUserId() ?: -1L
        setContent {
            TapTapTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }
                    composable("login") {
                        LoginRoute(
                            onNavigateToSignupEmail = {
                                navController.navigate(SignupRoute.GRAPH)
                            },
                            onLoginSuccess = {
                                navController.navigate("postLoginSplash") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("postLoginSplash") {
                        val postLoginSplashViewModel: PostLoginSplashViewModel = hiltViewModel()
                        PostLoginSplashScreen(
                            isReady = postLoginSplashViewModel.hasButtons != null,
                            onNavigateToHome = {
                                // 만든 버튼이 하나도 없으면(신규 유저·전부 삭제한 유저 등) 온보딩 화면을 보여준다.
                                val destination = if (postLoginSplashViewModel.hasButtons == true) "mainHome" else "home"
                                navController.navigate(destination) {
                                    popUpTo("postLoginSplash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        OnboardingTemplateRoute(
                            onTemplateSelected = { templateId ->
                                navController.navigate("mainHomeWithTemplate/$templateId") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onSkip = {
                                navController.navigate("mainHome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("mainHome") { backStackEntry ->
                        val mainHomeViewModel = hiltViewModel<
                            MainHomeViewModel,
                            MainHomeViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(null) })
                        // 버튼 생성 화면을 다녀왔을 때(back navigation) 새로 만든 버튼이 보이도록 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    mainHomeViewModel.refreshButtons()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        MainHomeScreen(
                            user = mainHomeViewModel.homeUser,
                            recentRecord = mainHomeViewModel.recentRecord,
                            favoriteButtons = mainHomeViewModel.favoriteButtons,
                            habitButtons = mainHomeViewModel.habitButtons,
                            firstButtonSuggestions = mainHomeViewModel.suggestions,
                            groupFirstButtonSuggestionsByCategory = false,
                            categories = mainHomeViewModel.categories,
                            onCreateCategory = mainHomeViewModel::createCategory,
                            onRenameCategory = mainHomeViewModel::renameCategory,
                            onDeleteCategory = mainHomeViewModel::deleteCategory,
                            onReorderCategories = mainHomeViewModel::reorderCategories,
                            errorMessage = mainHomeViewModel.errorMessage,
                            onErrorConsumed = mainHomeViewModel::consumeError,
                            onFirstButtonSuggestionClick = mainHomeViewModel::applySuggestion,
                            onNavigateToCreateButton = {
                                navController.navigate("createButton")
                            },
                            onNavigateToEditButton = { button ->
                                navController.navigate("editButton/${button.buttonId}")
                            },
                            onToggleFavorite = mainHomeViewModel::setFavorite,
                            onReorderFavorites = mainHomeViewModel::reorderFavorites,
                            onDeleteButton = mainHomeViewModel::deleteButton,
                            onNavigateToButtonDetail = {
                                // TODO: 선택한 버튼 id를 라우트에 실어 상세 데이터 조회 연결
                                navController.navigate("buttonDetail")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    // 온보딩에서 템플릿을 고른 직후에만 진입 — 카테고리별 추천 버튼을 보여준다
                    composable(
                        "mainHomeWithTemplate/{templateId}",
                        arguments = listOf(navArgument("templateId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val templateId = backStackEntry.arguments?.getLong("templateId") ?: 0L
                        val mainHomeViewModel = hiltViewModel<
                            MainHomeViewModel,
                            MainHomeViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(templateId) })
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    mainHomeViewModel.refreshButtons()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        MainHomeScreen(
                            user = mainHomeViewModel.homeUser,
                            recentRecord = mainHomeViewModel.recentRecord,
                            favoriteButtons = mainHomeViewModel.favoriteButtons,
                            habitButtons = mainHomeViewModel.habitButtons,
                            firstButtonSuggestions = mainHomeViewModel.suggestions,
                            categories = mainHomeViewModel.categories,
                            onCreateCategory = mainHomeViewModel::createCategory,
                            onRenameCategory = mainHomeViewModel::renameCategory,
                            onDeleteCategory = mainHomeViewModel::deleteCategory,
                            onReorderCategories = mainHomeViewModel::reorderCategories,
                            errorMessage = mainHomeViewModel.errorMessage,
                            onErrorConsumed = mainHomeViewModel::consumeError,
                            onFirstButtonSuggestionClick = mainHomeViewModel::applySuggestion,
                            onNavigateToCreateButton = {
                                navController.navigate("createButton")
                            },
                            onNavigateToEditButton = { button ->
                                navController.navigate("editButton/${button.buttonId}")
                            },
                            onToggleFavorite = mainHomeViewModel::setFavorite,
                            onReorderFavorites = mainHomeViewModel::reorderFavorites,
                            onDeleteButton = mainHomeViewModel::deleteButton,
                            onNavigateToButtonDetail = {
                                // TODO: 선택한 버튼 id를 라우트에 실어 상세 데이터 조회 연결
                                navController.navigate("buttonDetail")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("buttonDetail") {
                        ButtonDetailScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("notification") {
                        NotificationScreen(
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("insightDaily") {
                        var targetDate by remember { mutableStateOf(MockInsightDaily.targetDate) }
                        InsightDailyScreen(
                            data = MockInsightDaily.copy(targetDate = targetDate),
                            onNavigateToTimelineAll = {
                                navController.navigate("insightTimelineAll")
                            },
                            onNavigateToRatioAll = {
                                navController.navigate("insightRatioAll")
                            },
                            onNavigateToButtonDetail = {
                                // TODO: 선택한 기록의 버튼 상세로 이동
                                navController.navigate("buttonDetail")
                            },
                            onDeleteRecord = {
                                // TODO: 기록 삭제 API 연결
                            },
                            onPrevDay = { targetDate = targetDate.shiftDate(-1) },
                            onNextDay = { targetDate = targetDate.shiftDate(1) },
                            onSelectWeekly = {
                                navController.navigate("insightWeekly")
                            },
                            onSelectMonthly = {
                                navController.navigate("insightMonthly")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("insightWeekly") {
                        var weekStart by remember { mutableStateOf(MockInsightWeekly.weekStart) }
                        InsightWeeklyScreen(
                            data = MockInsightWeekly.copy(weekStart = weekStart),
                            onNavigateToRatioAll = {
                                navController.navigate("insightWeeklyRatioAll")
                            },
                            onPrevWeek = { weekStart = weekStart.shiftWeek(-1) },
                            onNextWeek = { weekStart = weekStart.shiftWeek(1) },
                            onSelectDaily = {
                                navController.popBackStack()
                            },
                            onSelectMonthly = {
                                navController.navigate("insightMonthly")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("insightMonthly") {
                        var yearMonth by remember {
                            mutableStateOf(MockInsightMonthly.year to MockInsightMonthly.month)
                        }
                        InsightMonthlyScreen(
                            data = MockInsightMonthly.copy(year = yearMonth.first, month = yearMonth.second),
                            onPrevMonth = { yearMonth = shiftMonth(yearMonth.first, yearMonth.second, -1) },
                            onNextMonth = { yearMonth = shiftMonth(yearMonth.first, yearMonth.second, 1) },
                            onSelectDaily = {
                                navController.navigate("insightDaily")
                            },
                            onSelectWeekly = {
                                navController.popBackStack()
                            },
                            onNavigateToLifestyle = {
                                navController.navigate("insightLifestyle")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("insightLifestyle") {
                        InsightLifestyleScreen(
                            data = MockInsightLifestyle,
                            onBack = {
                                navController.popBackStack()
                            },
                            onAddRecommendation = {
                                // TODO: 라이프스타일 추천 수락(ADD) API 연결
                            },
                            onDeleteRecommendation = {
                                // TODO: 라이프스타일 추천 수락(DELETE) API 연결
                            }
                        )
                    }
                    composable("insightWeeklyRatioAll") {
                        var weekStart by remember { mutableStateOf(MockInsightWeekly.weekStart) }
                        InsightWeeklyRatioAllScreen(
                            weekStart = weekStart,
                            dailyTapCounts = MockInsightWeekly.dailyTapCounts,
                            categoryTapCounts = MockInsightWeekly.categoryTapCounts,
                            buttonTapCounts = MockInsightWeekly.buttonTapCounts,
                            totalTapCount = MockInsightWeekly.totalTapCount,
                            onBack = { navController.popBackStack() },
                            onPrevWeek = { weekStart = weekStart.shiftWeek(-1) },
                            onNextWeek = { weekStart = weekStart.shiftWeek(1) },
                            onSelectDaily = {
                                navController.navigate("insightDaily")
                            }
                        )
                    }
                    composable("insightTimelineAll") {
                        var targetDate by remember { mutableStateOf(MockInsightDaily.targetDate) }
                        InsightTimelineAllScreen(
                            targetDate = targetDate,
                            timeline = MockInsightDaily.timeline,
                            onBack = { navController.popBackStack() },
                            onNavigateToButtonDetail = {
                                // TODO: 선택한 기록의 버튼 상세로 이동
                                navController.navigate("buttonDetail")
                            },
                            onDeleteRecord = {
                                // TODO: 기록 삭제 API 연결
                            },
                            onPrevDay = { targetDate = targetDate.shiftDate(-1) },
                            onNextDay = { targetDate = targetDate.shiftDate(1) }
                        )
                    }
                    composable("insightRatioAll") {
                        var targetDate by remember { mutableStateOf(MockInsightDaily.targetDate) }
                        InsightRatioAllScreen(
                            targetDate = targetDate,
                            categoryTapCounts = MockInsightDaily.categoryTapCounts,
                            buttonTapCounts = MockInsightDaily.buttonTapCounts,
                            totalTapCount = MockInsightDaily.totalTapCount,
                            onBack = { navController.popBackStack() },
                            onPrevDay = { targetDate = targetDate.shiftDate(-1) },
                            onNextDay = { targetDate = targetDate.shiftDate(1) }
                        )
                    }
                    composable("accountSettings") {
                        AccountSettingsScreen(
                            onNavigateToAccountInfo = {
                                navController.navigate("accountInfo")
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("accountInfo") {
                        AccountInfoScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigateToChangePassword = {
                                navController.navigate("changePassword")
                            }
                        )
                    }
                    composable("changePassword") {
                        ChangePasswordScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onChangeComplete = {
                                navController.popBackStack()
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable("createButton") { backStackEntry ->
                        val selectedIconName by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconName", null)
                            .collectAsState()
                        val selectedIconColorKey by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconColorKey", null)
                            .collectAsState()
                        val createButtonViewModel = hiltViewModel<
                            CreateButtonViewModel,
                            CreateButtonViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(null) })
                        CreateButtonScreen(
                            categories = createButtonViewModel.categories,
                            onCreateCategory = createButtonViewModel::createCategory,
                            onRenameCategory = createButtonViewModel::renameCategory,
                            onDeleteCategory = createButtonViewModel::deleteCategory,
                            onReorderCategories = createButtonViewModel::reorderCategories,
                            errorMessage = createButtonViewModel.errorMessage,
                            onErrorConsumed = createButtonViewModel::consumeError,
                            selectedIconName = selectedIconName,
                            selectedIconColor = selectedIconColorKey?.let { IconColor.from(it) },
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSave = { name, categoryName, iconName, iconColor, deadlineMillis, onComplete ->
                                createButtonViewModel.save(
                                    name = name,
                                    categoryName = categoryName,
                                    iconName = iconName,
                                    iconColor = iconColor,
                                    deadlineMillis = deadlineMillis,
                                    onSuccess = onComplete,
                                )
                            },
                            onNavigateToIconSelect = {
                                navController.navigate("iconSelect")
                            }
                        )
                    }
                    composable(
                        "editButton/{buttonId}",
                        arguments = listOf(navArgument("buttonId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val buttonId = backStackEntry.arguments?.getLong("buttonId") ?: 0L
                        val selectedIconName by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconName", null)
                            .collectAsState()
                        val selectedIconColorKey by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconColorKey", null)
                            .collectAsState()
                        val editButtonViewModel = hiltViewModel<
                            CreateButtonViewModel,
                            CreateButtonViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(buttonId) })

                        if (editButtonViewModel.isLoadingInitial) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            CreateButtonScreen(
                                categories = editButtonViewModel.categories,
                                onCreateCategory = editButtonViewModel::createCategory,
                                onRenameCategory = editButtonViewModel::renameCategory,
                                onDeleteCategory = editButtonViewModel::deleteCategory,
                                onReorderCategories = editButtonViewModel::reorderCategories,
                                errorMessage = editButtonViewModel.errorMessage,
                                onErrorConsumed = editButtonViewModel::consumeError,
                                selectedIconName = selectedIconName ?: editButtonViewModel.initialIconName,
                                selectedIconColor = selectedIconColorKey?.let { IconColor.from(it) }
                                    ?: editButtonViewModel.initialIconColor,
                                initialName = editButtonViewModel.initialName,
                                initialCategoryName = editButtonViewModel.initialCategoryName,
                                initialHasDeadline = editButtonViewModel.initialDeadlineMillis != null,
                                initialDeadlineMillis = editButtonViewModel.initialDeadlineMillis,
                                initialIsEditMode = true,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onSave = { name, categoryName, iconName, iconColor, deadlineMillis, onComplete ->
                                    editButtonViewModel.save(
                                        name = name,
                                        categoryName = categoryName,
                                        iconName = iconName,
                                        iconColor = iconColor,
                                        deadlineMillis = deadlineMillis,
                                        onSuccess = onComplete,
                                    )
                                },
                                onNavigateToIconSelect = {
                                    navController.navigate("iconSelect")
                                }
                            )
                        }
                    }
                    composable("iconSelect") {
                        IconSelectScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onConfirm = { iconName, iconColor ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconName", iconName)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconColorKey", iconColor.key)
                                navController.popBackStack()
                            }
                        )
                    }
                    signupGraph(
                        navController = navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onSignupComplete = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )

                    // ---- 팀 스페이스 ----
                    // 팀 목록 — 하단 네비 TEAM 탭 진입점
                    composable("teamList") { backStackEntry ->
                        val teamListViewModel: TeamListViewModel = hiltViewModel()
                        // 팀 생성/참여 후 이 화면으로 돌아왔을 때(back navigation)는
                        // ViewModel 인스턴스가 재사용되어 init{} 이 다시 안 불리므로,
                        // 화면이 다시 보일 때(RESUME)마다 목록을 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    teamListViewModel.loadTeams()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        TeamListScreen(
                            teams = teamListViewModel.teams,
                            onNavigateToTeamCreate = {
                                navController.navigate(TeamCreateRoute.GRAPH)
                            },
                            onNavigateToTeamDetail = { teamId ->
                                navController.navigate("teamDetail/$teamId")
                            },
                            onJoinTeam = { code ->
                                teamListViewModel.joinTeam(code)
                            },
                            onFavoriteClick = { teamId ->
                                teamListViewModel.toggleFavorite(teamId)
                            },
                            joinErrorMessage = teamListViewModel.joinErrorMessage,
                            isJoining = teamListViewModel.isJoining,
                            joinedTeamId = teamListViewModel.joinedTeamId,
                            onJoinedConsumed = { teamListViewModel.consumeJoinedSignal() },
                            onJoinModalOpened = { teamListViewModel.consumeJoinError() },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            },
                        )
                    }

                    // 팀 상세 — 활동·인사이트·멤버 탭 셸 (하단 네비 TEAM 탭 진입점)
                    composable(
                        "teamDetail/{teamId}?fromCreation={fromCreation}",
                        arguments = listOf(
                            navArgument("teamId") { type = NavType.LongType },
                            navArgument("fromCreation") { type = NavType.BoolType; defaultValue = false },
                        )
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getLong("teamId") ?: 0L
                        val fromCreation = backStackEntry.arguments?.getBoolean("fromCreation") ?: false
                        com.solux.luxup.taptap.feature.team.presentation.components.ResetTeamDeletionDismissalOnEntry(
                            sessionId = backStackEntry.id,
                            teamId = teamId,
                        )
                        val teamDetailViewModel = hiltViewModel<
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel,
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(teamId) })
                        TeamDetailScreen(
                            teamId = teamId,
                            teamName = teamDetailViewModel.teamName ?: "LUX-UP",
                            currentUserId = currentUserId,
                            hasSelectedTemplate = teamDetailViewModel.hasSelectedTemplate,
                            initialQuickCreateMode = fromCreation,
                            onExit = { navController.popBackStack() },
                            // + → 직접 만들기
                            onCreateButton = {
                                navController.navigate(TeamButtonCreateRoute.graph(teamId))
                            },
                            onOpenTeamSettings = {                                   // 멤버 탭 톱니바퀴 → 팀 설정 그래프
                                navController.navigate(TeamSettingRoute.graph(teamId))
                            },
                            // 버튼 정보 화면의 수정 아이콘에서 진입하므로 현재는 미사용
                            onEditButton = { buttonId ->
                                navController.navigate(TeamButtonEditRoute.graph(teamId, buttonId))
                            },
                            // ⋮ → 버튼 정보
                            onOpenButtonInfo = { buttonId ->
                                navController.navigate(TeamButtonInfoRoute.route(teamId, buttonId))
                            },
                            // 길게 누르기 → 타임라인
                            onOpenButtonTimeline = { buttonId ->
                                navController.navigate(TeamButtonTimelineRoute.route(teamId, buttonId))
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            },
                            onNavigateToInsightButtonAll = { tId, period ->
                                navController.navigate(TeamInsightButtonAllRoute.route(tId, period))
                            },
                        )
                    }

                    composable(
                        TeamInsightButtonAllRoute.ROUTE,
                        arguments = listOf(
                            navArgument(TeamInsightButtonAllRoute.ARG_TEAM_ID) { type = NavType.LongType },
                            navArgument(TeamInsightButtonAllRoute.ARG_PERIOD) { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getLong(TeamInsightButtonAllRoute.ARG_TEAM_ID) ?: 0L
                        val period = backStackEntry.arguments?.getString(TeamInsightButtonAllRoute.ARG_PERIOD) ?: "DAILY"

                        // 인사이트 탭에서 이미 조회해둔 값을 그대로 쓴다 (같은 teamDetail 백스택 엔트리에 스코프된 ViewModel 공유)
                        val teamDetailEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("teamDetail/{teamId}?fromCreation={fromCreation}")
                        }
                        val insightViewModel = hiltViewModel<
                            TeamInsightViewModel,
                            TeamInsightViewModel.Factory,
                            >(
                            viewModelStoreOwner = teamDetailEntry,
                            creationCallback = { factory -> factory.create(teamId) },
                        )
                        val memberActivity = when (period) {
                            "WEEKLY" -> insightViewModel.weekly?.memberActivity
                            "MONTHLY" -> insightViewModel.monthly?.memberActivity
                            else -> insightViewModel.daily?.memberActivity
                        }.orEmpty()

                        TeamInsightButtonAllScreen(
                            teamName = "LUX-UP",              // TODO: 실제 팀명 조회로 교체 (지금은 Mock)
                            teamId = teamId,
                            memberActivity = memberActivity,
                            currentUserId = currentUserId,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 팀 생성 — 만들기 / 초대코드 공유 / 템플릿 선택 (ViewModel 공유)
                    teamCreateGraph(
                        navController = navController,
                        onFinish = { teamId ->
                            navController.navigate("teamDetail/$teamId?fromCreation=true") {
                                popUpTo("teamList")
                            }
                        },
                    )

                    // 팀 버튼 생성 — 만들기 / 아이콘 선택 / 멤버 권한 설정 (ViewModel 공유)
                    teamButtonCreateGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onCreated = { navController.popBackStack() }
                    )

                    // 팀 버튼 수정 — 생성 화면을 공유하고 상세 조회로 초기값을 채운다
                    teamButtonEditGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onUpdated = { navController.popBackStack() }
                    )

                    // 팀 버튼 정보 — 관리자/비관리자 분기, 우상단 아이콘으로 수정 진입
                    teamButtonInfoScreen(
                        navController = navController,
                        currentUserId = currentUserId,
                    )

                    // 팀 버튼 타임라인 — 목록에서 길게 누르기로 진입
                    teamButtonTimelineScreen(
                        navController = navController,
                        currentUserId = currentUserId,
                    )

                    // 팀 설정 · 팀 관리 — 6개 화면이 TeamSettingViewModel 공유
                    teamSettingGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onExitTeam = {
                            // 팀 나가기 / 팀 삭제 후 팀 목록으로. 팀 스페이스 스택을 걷어낸다.
                            navController.navigate("teamList") {
                                popUpTo("teamList") { inclusive = true }
                            }
                        },
                        onNavItemSelected = { item ->
                            // 하단 탭 바에서 다른 탭 선택 → 팀 스페이스를 벗어나 해당 탭으로
                            navController.navigateToTab(item)
                        },
                    )
                }
            }
        }
    }
}