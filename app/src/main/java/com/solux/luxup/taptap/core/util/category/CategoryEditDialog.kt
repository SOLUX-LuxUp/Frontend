package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup

/**
 * "카테고리 수정" 팝업 - 카테고리 생성/이름 변경/삭제의 유일한 진입점.
 * 각 동작은 배치 저장 없이 즉시 onCreate/onRename/onRequestDelete로 위쪽(실제 API 호출부)에 위임하고,
 * [categories]는 그 결과로 갱신된 최신 목록을 그대로 반영해 다시 그린다.
 */
@Composable
fun CategoryEditDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit,
    onRename: (oldName: String, newName: String) -> Unit,
    onRequestDelete: (category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var categoryBeingRenamed by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(27.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp)
    ) {
        Text(
            text = "카테고리 수정",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White)
                .padding(top = 20.dp, bottom = 15.dp, start = 15.dp, end = 15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(50))
                    .clickable { showCreateDialog = true }
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("새로운 카테고리", fontSize = 14.sp, color = Color(0xFFB0B0B0))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "카테고리 추가",
                    tint = Color(0xFFB0B0B0),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(15.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                categories.forEach { category ->
                    key(category) {
                        var showMoreMenu by remember { mutableStateOf(false) }
                        var moreIconWidthPx by remember { mutableIntStateOf(20) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = Color(0xFFB1B1B1),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(category, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                            }
                            Box(
                                modifier = Modifier.onGloballyPositioned { moreIconWidthPx = it.size.width }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "카테고리 옵션",
                                    tint = Color(0xFFB1B1B1),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { showMoreMenu = true }
                                )

                                if (showMoreMenu) {
                                    Popup(
                                        alignment = Alignment.TopStart,
                                        offset = IntOffset(moreIconWidthPx + with(LocalDensity.current) { 10.dp.roundToPx() }, -15),
                                        onDismissRequest = { showMoreMenu = false }
                                    ) {
                                        CategoryItemMoreMenu(
                                            onEditClick = {
                                                showMoreMenu = false
                                                categoryBeingRenamed = category
                                            },
                                            onDeleteClick = {
                                                showMoreMenu = false
                                                onRequestDelete(category)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(30.dp))

        CategoryDialogButton(
            text = "닫기",
            textColor = Color(0xFFACACAC),
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss
        )
    }

    if (showCreateDialog) {
        Dialog(
            onDismissRequest = { showCreateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryCreateDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    onCreate(name)
                    showCreateDialog = false
                }
            )
        }
    }

    categoryBeingRenamed?.let { original ->
        Dialog(
            onDismissRequest = { categoryBeingRenamed = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CategoryRenameDialog(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                category = original,
                onDismiss = { categoryBeingRenamed = null },
                onRename = { newName ->
                    onRename(original, newName)
                    categoryBeingRenamed = null
                }
            )
        }
    }
}

@Composable
internal fun CategoryDialogButton(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = textColor,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}
