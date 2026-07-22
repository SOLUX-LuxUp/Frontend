package com.solux.luxup.taptap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.solux.luxup.taptap.feature.auth.login.presentation.LoginScreen
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupEmailScreen
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupPasswordScreen
import com.solux.luxup.taptap.feature.home.buttondetail.presentation.ButtonDetailScreen
import com.solux.luxup.taptap.feature.home.main.presentation.CreateButtonScreen
import com.solux.luxup.taptap.feature.home.main.presentation.IconSelectScreen
import com.solux.luxup.taptap.feature.home.main.presentation.MainHomeScreen
import com.solux.luxup.taptap.feature.home.template.presentation.OnboardingTemplateScreen
import com.solux.luxup.taptap.ui.theme.TapTapTheme
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashScreen
import com.solux.luxup.taptap.feature.splash.presentation.SplashScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamDetailScreen
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonCreateRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonEditRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonInfoRoute
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonCreateGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonEditGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonInfoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapTapTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "teamDetail/1") {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToSignupEmail = {
                                navController.navigate("signupEmail")
                            },
                            onLoginSuccess = {
                                navController.navigate("postLoginSplash") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("postLoginSplash") {
                        PostLoginSplashScreen(
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("postLoginSplash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        OnboardingTemplateScreen(
                            onSkip = {
                                navController.navigate("mainHome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("mainHome") {
                        MainHomeScreen(
                            onNavigateToCreateButton = {
                                navController.navigate("createButton")
                            },
                            onNavigateToButtonDetail = {
                                // TODO: 선택한 버튼 id를 라우트에 실어 상세 데이터 조회 연결
                                navController.navigate("buttonDetail")
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
                    composable("createButton") { backStackEntry ->
                        val selectedIconRes by backStackEntry.savedStateHandle
                            .getStateFlow<Int?>("selectedIconRes", null)
                            .collectAsState()
                        val selectedIconTintArgb by backStackEntry.savedStateHandle
                            .getStateFlow<Int?>("selectedIconTint", null)
                            .collectAsState()
                        CreateButtonScreen(
                            selectedIconRes = selectedIconRes,
                            selectedIconTint = selectedIconTintArgb?.let { Color(it) },
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToIconSelect = {
                                navController.navigate("iconSelect")
                            }
                        )
                    }
                    composable("iconSelect") {
                        IconSelectScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onConfirm = { iconRes, tint ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconRes", iconRes)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconTint", tint.toArgb())
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("signupEmail") {
                        SignupEmailScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onVerified = { email ->
                                navController.navigate("signupPassword/$email")
                            }
                        )
                    }
                    composable(
                        "signupPassword/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email").orEmpty()
                        SignupPasswordScreen(
                            email = email,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSignupComplete = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    // ---- 팀 스페이스 ----

                    // 팀 상세 — 활동·인사이트·멤버 탭 셸
                    composable(
                        "teamDetail/{teamId}",
                        arguments = listOf(navArgument("teamId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getLong("teamId") ?: 0L
                        TeamDetailScreen(
                            onExit = { navController.popBackStack() },
                            // + → 직접 만들기
                            onCreateButton = {
                                navController.navigate(TeamButtonCreateRoute.graph(teamId))
                            },
                            // 버튼 정보 화면의 수정 아이콘에서 진입하므로 현재는 미사용
                            onEditButton = { buttonId ->
                                navController.navigate(TeamButtonEditRoute.graph(teamId, buttonId))
                            },
                            // ⋮ → 버튼 정보
                            onOpenButtonInfo = { buttonId ->
                                navController.navigate(TeamButtonInfoRoute.route(teamId, buttonId))
                            },
                        )
                    }

                    // 팀 버튼 생성 — 만들기 / 아이콘 선택 / 멤버 권한 설정 (ViewModel 공유)
                    teamButtonCreateGraph(
                        navController = navController,
                        currentUserId = 4L,   // TODO: 로그인 유저 id로 교체
                        onCreated = { navController.popBackStack() }
                    )

                    // 팀 버튼 수정 — 생성 화면을 공유하고 상세 조회로 초기값을 채운다
                    teamButtonEditGraph(
                        navController = navController,
                        currentUserId = 4L,
                        onUpdated = { navController.popBackStack() }
                    )

                    // 팀 버튼 정보 — 관리자/비관리자 분기, 우상단 아이콘으로 수정 진입
                    teamButtonInfoScreen(
                        navController = navController,
                        currentUserId = 4L,
                    )
                }
            }
        }
    }
}