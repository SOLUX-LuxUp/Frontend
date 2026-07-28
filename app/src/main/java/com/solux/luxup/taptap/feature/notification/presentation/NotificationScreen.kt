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
import androidx.compose.foundation.layout.offset
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
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.core.util.category.CategoryDeleteConfirmDialog
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.CategoryEditDialog
import com.solux.luxup.taptap.feature.notification.data.mockAddableNotifications
import com.solux.luxup.taptap.feature.notification.data.mockNotifications
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.feature.notification.util.NotificationAddDialog
import com.solux.luxup.taptap.feature.notification.util.NotificationDeleteConfirmDialog
import com.solux.luxup.taptap.feature.notification.util.NotificationListItem
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun NotificationScreen(
    notifications: List<NotificationItem> = mockNotifications,
    addableNotifications: List<NotificationItem> = mockAddableNotifications,
    onNavItemSelected: (BottomNavItem) -> Unit = {}
) {
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.NOTIFICATION) }
    var notificationsState by remember(notifications) { mutableStateOf(notifications) }
    var manageableCategories by remember { mutableStateOf(listOf("HEALTH", "ROUTINE", "TRAVEL", "WORK")) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCategoryEditDialog by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<String?>(null) }
    var checkedAddableIds by remember(addableNotifications) {
        mutableStateOf(addableNotifications.filter { it.isEnabled }.map { it.id }.toSet())
    }
    var detailTarget by remember { mutableStateOf<NotificationItem?>(null) }
    var editTarget by remember { mutableStateOf<NotificationItem?>(null) }
    var deleteTarget by remember { mutableStateOf<NotificationItem?>(null) }

    val filteredNotifications = remember(notificationsState, selectedCategory, searchQuery) {
        notificationsState
            .filter { selectedCategory == null || selectedCategory == "ALL" || it.category == selectedCategory }
            .filter { searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) }
    }

    val currentDetailTarget = detailTarget
    if (currentDetailTarget != null) {
        // 다이얼로그 창 안이 아니라 화면 전체에 직접 그려야 창 그림자(위/아래 블러)가 생기지 않음
        NotificationDetailScreen(
            item = currentDetailTarget,
            isNew = !checkedAddableIds.contains(currentDetailTarget.id),
            onBack = {
                detailTarget = null
                showAddDialog = true
            },
            onConfirm = {
                checkedAddableIds = checkedAddableIds + currentDetailTarget.id
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
            onConfirm = {
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
                    categories = manageableCategories + "ALL",
                    onCategorySelected = { selectedCategory = it },
                    onManageCategoriesClick = { showCategoryEditDialog = true }
                )
                Spacer(Modifier.width(16.dp))
                SearchBar(
                    modifier = Modifier.weight(1f),
                    fillWidth = true,
                    horizontalMargin = 0.dp,
                    placeholder = "버튼 검색"
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
                filteredNotifications.forEach { item ->
                    NotificationListItem(
                        item = item,
                        onToggle = { checked ->
                            notificationsState = notificationsState.map {
                                if (it.id == item.id) it.copy(isEnabled = checked) else it
                            }
                        },
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
                items = addableNotifications,
                checkedIds = checkedAddableIds,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                onDismiss = { showAddDialog = false },
                onItemClick = { item ->
                    showAddDialog = false
                    detailTarget = item
                },
                onSave = { checkedIds ->
                    checkedAddableIds = checkedIds
                    val newlyAdded = addableNotifications.filter { candidate ->
                        checkedIds.contains(candidate.id) &&
                            notificationsState.none { existing -> existing.title == candidate.title }
                    }
                    if (newlyAdded.isNotEmpty()) {
                        notificationsState = notificationsState + newlyAdded.map { candidate ->
                            candidate.copy(scheduleText = "알림 설정 필요")
                        }
                    }
                    showAddDialog = false
                }
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
                categories = manageableCategories,
                onDismiss = { showCategoryEditDialog = false },
                onCreate = { name ->
                    if (name !in manageableCategories) {
                        manageableCategories = manageableCategories + name
                    }
                },
                onRename = { oldName, newName ->
                    manageableCategories = manageableCategories.map { if (it == oldName) newName else it }
                },
                onRequestDelete = { category -> categoryPendingDelete = category }
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
                    manageableCategories = manageableCategories - category
                    if (deleteButtonsToo) {
                        notificationsState = notificationsState.filter { it.category != category }
                    }
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
                    notificationsState = notificationsState.filter { it.id != item.id }
                    deleteTarget = null
                }
            )
        }
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