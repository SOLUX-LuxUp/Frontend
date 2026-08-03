package com.solux.luxup.taptap.feature.home.main.util.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.util.category.CategoryDeleteConfirmDialog
import com.solux.luxup.taptap.core.util.category.CategoryEditDialog
import com.solux.luxup.taptap.core.util.category.CategorySelectDropdown
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.home.main.util.MonthCalendarPicker
import com.solux.luxup.taptap.feature.home.main.util.RegisterReminderDialog
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 카테고리 선택 드롭다운에서 "카테고리 없음"을 명시적으로 고를 수 있게 해주는 항목 */
private const val NO_CATEGORY_LABEL = "No Category"

@Composable
fun CreateButtonScreen(
    categories: List<Category> = emptyList(),
    onCreateCategory: (name: String) -> Unit = {},
    onRenameCategory: (categoryId: Long, newName: String) -> Unit = { _, _ -> },
    onDeleteCategory: (categoryId: Long, deleteButtonsToo: Boolean) -> Unit = { _, _ -> },
    onReorderCategories: (categoryIds: List<Long>) -> Unit = {},
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
    selectedIconName: String? = null,
    selectedIconColor: IconColor? = null,
    initialName: String = "",
    initialCategoryName: String? = null,
    initialHasDeadline: Boolean = false,
    initialDeadlineMillis: Long? = null,
    initialIsEditMode: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSave: (
        name: String,
        category: String?,
        iconName: String?,
        iconColor: String?,
        deadlineMillis: Long?,
        onComplete: () -> Unit,
    ) -> Unit = { _, _, _, _, _, onComplete -> onComplete() },
    onNavigateToAlarmSettings: () -> Unit = {},
    onNavigateToIconSelect: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedCategory by remember { mutableStateOf(initialCategoryName) }
    var showCategoryEditDialog by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<String?>(null) }

    var hasDeadline by remember { mutableStateOf(initialHasDeadline) }
    var deadlineMillis by remember { mutableStateOf(initialDeadlineMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showRegisterReminderDialog by remember { mutableStateOf(false) }
    // 기존 버튼을 수정하러 들어온 경우(initialIsEditMode)엔 저장 시 "알림을 등록할까요?" 팝업 없이 바로 저장한다 —
    // 그 팝업은 새로 만든 버튼에 알림을 처음 걸지 물어보는 온보딩성 흐름이라 수정 진입에는 맞지 않는다.
    var isEditMode by remember { mutableStateOf(initialIsEditMode) }

    val selectedIconRes = selectedIconName?.let { ButtonIcons.resOf(it) }
    val selectedIconTint = selectedIconColor?.color

    val isSaveEnabled = name.isNotBlank() && hasDeadline && deadlineMillis != null

    fun completeSave() {
        onSave(name.trim(), selectedCategory, selectedIconName, selectedIconColor?.key, if (hasDeadline) deadlineMillis else null) {
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseWhiteColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp)
    ) {
        Spacer(Modifier.height(70.dp))

        Box(Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color(0xFFB1B1B1),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clickable { onNavigateBack() }
            )
            Text(
                text = if (isEditMode) "버튼 수정" else "버튼 만들기",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd))
                    )
                    .clickable(enabled = isSaveEnabled) {
                        if (initialIsEditMode) completeSave() else showRegisterReminderDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(140.dp)
                .circleDropShadow()
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onNavigateToIconSelect() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedIconRes != null) {
                Icon(
                    painter = painterResource(selectedIconRes),
                    contentDescription = null,
                    tint = selectedIconTint ?: Color(0xFF7CCBFF),
                    modifier = Modifier.size(60.dp)
                )
            } else {
                GradientIcon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))

        Text("이름", fontSize = 14.sp, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .dropShadow(cornerRadius = 10.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.White
            )
        )

        Spacer(Modifier.height(20.dp))

        Text("카테고리", fontSize = 14.sp, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(8.dp))
        CategorySelectDropdown(
            categories = listOf(NO_CATEGORY_LABEL) + categories.map { it.name },
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it.takeIf { it != NO_CATEGORY_LABEL } },
            onManageCategoriesClick = { showCategoryEditDialog = true }
        )

        Spacer(Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("기한설정", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            DeadlineToggle(
                checked = hasDeadline,
                onCheckedChange = { checked ->
                    hasDeadline = checked
                    if (checked && deadlineMillis == null) {
                        showDatePicker = true
                    }
                }
            )
        }

        if (hasDeadline) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .dropShadow(cornerRadius = 14.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = deadlineMillis?.let { formatDeadlineText(it) } ?: "날짜 선택",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6D6D6D)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("까지", fontSize = 15.sp, color = Color(0xFF6D6D6D))
            }
        }

        if (isEditMode) {
            Spacer(Modifier.height(100.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd)))
                    .clickable { onNavigateToAlarmSettings() }
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("알림설정", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }

    if (showCategoryEditDialog) {
        Dialog(
            onDismissRequest = { showCategoryEditDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryEditDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                categories = categories.map { it.name },
                onDismiss = { showCategoryEditDialog = false },
                onCreate = onCreateCategory,
                onRename = { oldName, newName ->
                    categories.find { it.name == oldName }?.let { onRenameCategory(it.id, newName) }
                    if (selectedCategory == oldName) {
                        selectedCategory = newName
                    }
                },
                onRequestDelete = { category -> categoryPendingDelete = category },
                onReorder = { newOrder ->
                    onReorderCategories(newOrder.mapNotNull { name -> categories.find { it.name == name }?.id })
                }
            )
        }
    }

    categoryPendingDelete?.let { category ->
        Dialog(
            onDismissRequest = { categoryPendingDelete = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryDeleteConfirmDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                category = category,
                onDismiss = { categoryPendingDelete = null },
                onConfirmDelete = { deleteButtonsToo ->
                    categories.find { it.name == category }?.let { onDeleteCategory(it.id, deleteButtonsToo) }
                    if (selectedCategory == category) {
                        selectedCategory = null
                    }
                    categoryPendingDelete = null
                }
            )
        }
    }

    if (showDatePicker) {
        Dialog(
            onDismissRequest = { showDatePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MonthCalendarPicker(
                selectedDateMillis = deadlineMillis,
                onDateSelected = { millis ->
                    deadlineMillis = millis
                    showDatePicker = false
                },
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }

    if (showRegisterReminderDialog) {
        Dialog(
            onDismissRequest = {
                showRegisterReminderDialog = false
                completeSave()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            RegisterReminderDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                onDismiss = {
                    showRegisterReminderDialog = false
                    completeSave()
                },
                onConfirm = {
                    showRegisterReminderDialog = false
                    onSave(name.trim(), selectedCategory, selectedIconName, selectedIconColor?.key, if (hasDeadline) deadlineMillis else null) {
                        isEditMode = true
                    }
                }
            )
        }
    }

    errorMessage?.let { message ->
        NoticeDialog(message = message, onDismiss = onErrorConsumed)
    }
}

// 피그마 Drop shadow(X:0, Y:0, Blur:4.8, Spread:2, Color:#000000 10%)를 그대로 구현한 사각 그림자
internal fun Modifier.dropShadow(
    cornerRadius: Dp,
    blurRadius: Dp = 4.8.dp,
    spread: Dp = 2.dp,
    color: Color = Color.Black,
    alpha: Float = 0.1f
): Modifier = this.drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    val spreadPx = spread.toPx()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(blurRadius.toPx(), 0f, 0f, shadowColor)
        canvas.drawRoundRect(
            left = -spreadPx,
            top = -spreadPx,
            right = size.width + spreadPx,
            bottom = size.height + spreadPx,
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )
    }
}

// 피그마 Drop shadow(X:0, Y:0, Blur:4.8, Spread:2, Color:#000000 10%)를 그대로 구현한 원형 그림자
private fun Modifier.circleDropShadow(
    blurRadius: Dp = 22.2.dp,
    spread: Dp = 0.dp,
    color: Color = Color.Black,
    alpha: Float = 0.1f
): Modifier = this.drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(blurRadius.toPx(), 0f, 0f, shadowColor)
        canvas.drawCircle(
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.minDimension / 2f + spread.toPx(),
            paint = paint
        )
    }
}

// 트랙 크기와 무관하게 썸(동그라미) 크기를 독립적으로 조절할 수 있는 커스텀 토글
@Composable
private fun DeadlineToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trackWidth: Dp = 52.dp,
    trackHeight: Dp = 26.dp,
    thumbSize: Dp = 20.dp,
    thumbPadding: Dp = 2.dp,
    checkedTrackColor: Color = Color(0xFF2085FF),
    uncheckedTrackColor: Color = Color(0xFFE2E2E2),
    thumbColor: Color = Color.White
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        label = "deadlineToggleThumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) checkedTrackColor else uncheckedTrackColor,
        label = "deadlineToggleTrackColor"
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor)
        )
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

private val deadlineDateFormat = SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN)

private fun formatDeadlineText(millis: Long): String = deadlineDateFormat.format(Date(millis))

@Preview(showSystemUi = true)
@Composable
private fun CreateButtonScreenPreview() {
    CreateButtonScreen()
}