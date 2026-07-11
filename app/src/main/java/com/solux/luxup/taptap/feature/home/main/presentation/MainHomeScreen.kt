package com.solux.luxup.taptap.feature.home.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.feature.home.main.data.TEMPLATE_FIRST_BUTTON_TITLE
import com.solux.luxup.taptap.feature.home.main.data.TEMPLATE_QUICK_BUTTON_TITLE
import com.solux.luxup.taptap.feature.home.main.data.mockFavoriteButtons
import com.solux.luxup.taptap.feature.home.main.data.mockHabitButtons
import com.solux.luxup.taptap.feature.home.main.data.mockHomeUser
import com.solux.luxup.taptap.feature.home.main.data.mockRecentRecord
import com.solux.luxup.taptap.feature.home.main.data.recommendedButtons
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton
import com.solux.luxup.taptap.feature.home.main.model.HabitButton
import com.solux.luxup.taptap.feature.home.main.model.HomeUser
import com.solux.luxup.taptap.feature.home.main.model.RecentRecord
import com.solux.luxup.taptap.feature.home.main.model.RecommendedButton
import com.solux.luxup.taptap.feature.home.main.util.AddButtonMenuPopup
import com.solux.luxup.taptap.feature.home.main.util.FavoriteAddBox
import com.solux.luxup.taptap.feature.home.main.util.FavoriteButtonBox
import com.solux.luxup.taptap.feature.home.main.util.HabitButtonGrid
import com.solux.luxup.taptap.feature.home.main.util.RecentRecordBox
import com.solux.luxup.taptap.feature.home.main.util.TemplateSuggestionPopupCard
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun MainHomeScreen(
    user: HomeUser = mockHomeUser,
    recentRecord: RecentRecord? = mockRecentRecord,
    favoriteButtons: List<FavoriteButton> = mockFavoriteButtons,
    habitButtons: List<HabitButton> = mockHabitButtons,
    suggestions: List<RecommendedButton> = recommendedButtons
) {
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.HOME) }

    // 상단 + 버튼 드롭다운에서 "빠르게 만들기"를 선택했을 때만 뜨는 추천 버튼 팝업
    var showQuickCreatePopup by remember { mutableStateOf(false) }

    // 카테고리 드롭다운에서 선택된 카테고리 (null 또는 "ALL"이면 전체 노출)
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val filteredHabitButtons = remember(habitButtons, selectedCategory) {
        if (selectedCategory == null || selectedCategory == "ALL") {
            habitButtons
        } else {
            habitButtons.filter { it.category == selectedCategory }
        }
    }

    fun onRecommendedButtonClick(item: RecommendedButton) {
        // TODO: 실제 버튼 생성 플로우 연결 (선택한 템플릿으로 다음 화면 이동)
    }

    Scaffold(
        containerColor = BaseWhiteColor,
        bottomBar = {
            BottomNavBar(selected = selectedNavItem, onItemSelected = { selectedNavItem = it })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BaseWhiteColor)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            HomeTopBar(
                onCreateManually = { /* TODO: 직접 만들기 플로우 연결 */ },
                onCreateQuickly = { showQuickCreatePopup = true }
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(brush = Brush.linearGradient(colors = listOf(BlueGradientStart, BlueGradientEnd)))) {
                        append(user.nickname)
                    }
                    withStyle(SpanStyle(color = Color(0xFF1A1A1A))) {
                        append("님 반가워요!")
                    }
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text("오늘의 습관도 기록해봐요.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6D6D6D))

            Spacer(Modifier.height(30.dp))
            Text("최근 기록", fontSize = 15.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(8.dp))
            RecentRecordBox(record = recentRecord)

            Spacer(Modifier.height(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("즐겨찾기", fontSize = 15.sp, color = Color(0xFF6D6D6D))
                Spacer(Modifier.width(2.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_setting),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                favoriteButtons.forEach { button ->
                    FavoriteButtonBox(button = button, onClick = { /* TODO: 즐겨찾기 버튼 클릭 */ })
                }
                FavoriteAddBox(onClick = { /* TODO: 즐겨찾기 추가 */ })
            }

            Spacer(Modifier.height(30.dp))
            CategoryDropdown(onCategorySelected = { selectedCategory = it })
            Spacer(Modifier.height(8.dp))

            if (habitButtons.isEmpty()) {
                TemplateSuggestionExpandedCard(title = TEMPLATE_FIRST_BUTTON_TITLE)
            } else {
                HabitButtonGrid(buttons = filteredHabitButtons)
            }

            Spacer(Modifier.height(30.dp))
        }
    }

    if (showQuickCreatePopup) {
        Dialog(onDismissRequest = { showQuickCreatePopup = false }) {
            TemplateSuggestionPopupCard(
                title = TEMPLATE_QUICK_BUTTON_TITLE,
                items = suggestions,
                onDismiss = { showQuickCreatePopup = false },
                onItemClick = {
                    onRecommendedButtonClick(it)
                    showQuickCreatePopup = false
                }
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    onCreateManually: () -> Unit,
    onCreateQuickly: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TapTap.",
            style = TextStyle(
                fontSize = 31.sp,
                fontWeight = FontWeight.SemiBold,
                brush = Brush.linearGradient(colors = listOf(BlueGradientStart, BlueGradientEnd))
            )
        )
        Spacer(Modifier.weight(1f))
        GradientIcon(
            imageVector = Icons.Default.Search,
            contentDescription = "검색",
            modifier = Modifier.size(31.dp)
        )
        Spacer(Modifier.width(10.dp))
        AddButtonWithMenu(
            onCreateManually = onCreateManually,
            onCreateQuickly = onCreateQuickly
        )
    }
}

@Composable
private fun AddButtonWithMenu(
    onCreateManually: () -> Unit,
    onCreateQuickly: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    GradientIcon(
        imageVector = Icons.Default.Add,
        contentDescription = "추가",
        modifier = Modifier
            .size(31.dp)
            .clickable { showMenu = true }
    )

    if (showMenu) {
        Dialog(
            onDismissRequest = { showMenu = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, end = 40.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                AddButtonMenuPopup(
                    onDismiss = { showMenu = false },
                    onCreateManually = {
                        showMenu = false
                        onCreateManually()
                    },
                    onCreateQuickly = {
                        showMenu = false
                        onCreateQuickly()
                    }
                )
            }
        }
    }
}

@Composable
private fun GradientIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = Color.Unspecified,
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val brush = Brush.linearGradient(colors = listOf(BlueGradientStart, BlueGradientEnd))
                onDrawWithContent {
                    drawContent()
                    drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                }
            }
    )
}

@Composable
private fun TemplateSuggestionExpandedCard(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .figmaDropShadow(cornerRadius = 13.dp),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFEFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        }
    }
}

private fun Modifier.figmaDropShadow(
    cornerRadius: Dp,
    color: Color = Color.Black,
    alpha: Float = 0.15f,
    blurRadius: Dp = 7.dp
): Modifier = this.drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(blurRadius.toPx(), 0f, 0f, shadowColor)
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MainHomeScreenPreview() {
    MainHomeScreen()
}