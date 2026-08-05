package com.solux.luxup.taptap.feature.notification.util

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.resolveCategoryFilter
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd

// "알림 추가" 모달 - 버튼 후보 목록에서 알림을 걸 버튼을 체크해 선택
// 체크리스트 항목을 탭하면 onItemClick으로 알려서 세부 설정 화면(전체화면)으로 넘김 - 다이얼로그 창 안에 중첩하면 창 그림자가 생기므로 호출부에서 다이얼로그 밖으로 빼서 띄워야 함
@Composable
fun NotificationAddDialog(
    items: List<NotificationItem>,
    checkedIds: Set<Long>,
    onDismiss: () -> Unit,
    onSave: (checkedIds: Set<Long>) -> Unit,
    onItemClick: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<String> = emptyList()
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val filteredItems = remember(items, query, selectedCategory) {
        items
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEF0FF))
            .padding(30.dp)
    ) {
        Text(
            "알림 추가",
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
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryDropdown(
                    categories = categories,
                    onCategorySelected = { selectedCategory = resolveCategoryFilter(it) }
                )
                Spacer(Modifier.width(12.dp))
                SearchBar(
                    modifier = Modifier.weight(1f),
                    placeholder = "버튼 검색",
                    horizontalMargin = 0.dp,
                    fillWidth = true,
                    onQueryChange = { query = it }
                )
            }
            Spacer(Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                filteredItems.forEach { item ->
                    NotificationCheckRow(
                        item = item,
                        checked = checkedIds.contains(item.id),
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DialogPillButton(
                text = "취소",
                textColor = Color(0xFFFF7B7B),
                borderColor = Color(0xFFFF7B7B),
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            )
            DialogPillButton(
                text = "저장",
                textColor = Color(0xFFACACAC),
                borderColor = Color(0xFFACACAC),
                onClick = { onSave(checkedIds) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NotificationCheckRow(
    item: NotificationItem,
    checked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .notificationBadgeDropShadow()
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            item.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (checked) BlueGradientEnd else Color.White)
                .border(
                    width = 1.dp,
                    color = if (checked) Color.Transparent else Color(0xFFB1B1B1),
                    shape = RoundedCornerShape(2.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DialogPillButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}