package com.solux.luxup.taptap.feature.notification.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.core.util.category.CategoryDeleteConfirmDialog
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.CategoryEditDialog
import com.solux.luxup.taptap.feature.home.main.model.Category
import com.solux.luxup.taptap.feature.notification.data.mockAddableNotifications
import com.solux.luxup.taptap.feature.notification.data.mockNotifications
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.model.ReminderConfig
import com.solux.luxup.taptap.feature.notification.util.NotificationAddDialog
import com.solux.luxup.taptap.feature.notification.util.NotificationDeleteConfirmDialog
import com.solux.luxup.taptap.feature.notification.util.NotificationListItem
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun NotificationScreen(
    reminders: List<NotificationItem> = mockNotifications,
    addableButtons: List<NotificationItem> = mockAddableNotifications,
    categories: List<Category> = emptyList(),
    onToggle: (buttonId: Long, isEnabled: Boolean) -> Unit = { _, _ -> },
    onSaveDetail: (buttonId: Long, config: ReminderConfig) -> Unit = { _, _ -> },
    onDeleteReminder: (buttonId: Long) -> Unit = {},
    onCreateCategory: (name: String) -> Unit = {},
    onRenameCategory: (categoryId: Long, newName: String) -> Unit = { _, _ -> },
    onDeleteCategory: (categoryId: Long, deleteButtonsToo: Boolean) -> Unit = { _, _ -> },
    onReorderCategories: (categoryIds: List<Long>) -> Unit = {},
    onNavItemSelected: (BottomNavItem) -> Unit = {},
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {}
) {
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.NOTIFICATION) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCategoryEditDialog by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<String?>(null) }
    var detailTarget by remember { mutableStateOf<NotificationItem?>(null) }
    var editTarget by remember { mutableStateOf<NotificationItem?>(null) }
    var deleteTarget by remember { mutableStateOf<NotificationItem?>(null) }

    val filteredNotifications = remember(reminders, selectedCategory, searchQuery) {
        reminders
            .filter { selectedCategory == null || selectedCategory == "ALL" || it.category == selectedCategory }
            .filter { searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) }
    }

    val currentDetailTarget = detailTarget
    if (currentDetailTarget != null) {
        // 다이얼로그 창 안이 아니라 화면 전체에 직접 그려야 창 그림자(위/아래 블러)가 생기지 않음
        NotificationDetailScreen(
            item = currentDetailTarget,
            isNew = true,
            onBack = {
                detailTarget = null
                showAddDialog = true
            },
            onConfirm = { config ->
                onSaveDetail(currentDetailTarget.id, config)
                detailTarget = null
                showAddDialog = true
            }
        )
        return
    }

    val currentEditTarget = editTarget
    if (currentEditTarget != null) {
        // 목록의 "수정" 메뉴로 들어온 경우 - 완료/뒤로가기 시 목록 화면으로 바로 복귀
        NotificationDetailScreen(
            item = currentEditTarget,
            isNew = false,
            onBack = { editTarget = null },
            onConfirm = { config ->
                onSaveDetail(currentEditTarget.id, config)
                editTarget = null
            }
        )
        return
    }

    Scaffold(
        containerColor = BaseWhiteColor,
        bottomBar = {
            BottomNavBar(
                selected = selectedNavItem,
                onItemSelected = { item ->
                    selectedNavItem = item
                    onNavItemSelected(item)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BaseWhiteColor)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(Modifier.height(70.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text(
                    text = "알림",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.align(Alignment.Center)
                )
                GradientAddIcon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable { showAddDialog = true }
                )
            }

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryDropdown(
                    categories = categories.map { it.name } + "ALL",
                    onCategorySelected = { selectedCategory = it },
                    onManageCategoriesClick = { showCategoryEditDialog = true }
                )
                Spacer(Modifier.width(16.dp))
                SearchBar(
                    modifier = Modifier.weight(1f),
                    fillWidth = true,
                    horizontalMargin = 0.dp,
                    placeholder = "버튼 검색",
                    onQueryChange = { searchQuery = it }
                )
            }

            Spacer(Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("아직 알림이 없어요", fontSize = 14.sp, color = Color(0xFFB0B0B0))
                    }
                }

                filteredNotifications.forEach { item ->
                    NotificationListItem(
                        item = item,
                        onToggle = { checked -> onToggle(item.id, checked) },
                        onEditClick = { editTarget = item },
                        onDeleteClick = { deleteTarget = item }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (showAddDialog) {
        Dialog(
            onDismissRequest = { showAddDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            NotificationAddDialog(
                items = reminders + addableButtons,
                checkedIds = reminders.map { it.id }.toSet(),
                categories = categories.map { it.name } + "ALL",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                onDismiss = { showAddDialog = false },
                onItemClick = { item ->
                    showAddDialog = false
                    // 이미 알림이 설정된(체크된) 항목이면 "알림 수정"으로, 아니면 "알림 추가"로 연다.
                    if (item.config != null) {
                        editTarget = item
                    } else {
                        detailTarget = item
                    }
                },
                onSave = { showAddDialog = false }
            )
        }
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

    deleteTarget?.let { item ->
        Dialog(
            onDismissRequest = { deleteTarget = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            NotificationDeleteConfirmDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                item = item,
                onDismiss = { deleteTarget = null },
                onConfirmDelete = {
                    onDeleteReminder(item.id)
                    deleteTarget = null
                }
            )
        }
    }

    errorMessage?.let { message ->
        NoticeDialog(message = message, onDismiss = onErrorConsumed)
    }
}

@Composable
private fun GradientAddIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "알림 추가",
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

@Preview(showSystemUi = true)
@Composable
private fun NotificationScreenPreview() {
    NotificationScreen()
}