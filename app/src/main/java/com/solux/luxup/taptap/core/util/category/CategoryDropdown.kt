package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.solux.luxup.taptap.R

private val defaultCategories = listOf("HEALTH", "ROUTINE", "TRAVEL", "WORK", "ALL")

// 필터용 카테고리 드롭다운 - "ALL"을 포함한 카테고리 중 하나를 선택.
// 관리 가능한 카테고리("ALL" 제외)가 하나도 없으면 "카테고리 수정" 대신 "+ ADD"를 보여준다 -
// 두 경우 모두 onManageCategoriesClick으로 같은 관리 모달을 띄우는 진입점일 뿐, 생성/수정/삭제는 항상 그 모달 안에서만 이뤄진다.
@Composable
fun CategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String> = defaultCategories,
    onCategorySelected: (String?) -> Unit = {},
    onManageCategoriesClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val hasManageableCategories = categories.any { it != "ALL" && it != "No Category" }

    // 선택 중이던 카테고리가 삭제되거나 이름이 바뀌어 목록에서 사라지면 필터를 ALL로 되돌린다.
    LaunchedEffect(categories) {
        if (selectedCategory != null && selectedCategory !in categories) {
            selectedCategory = null
            onCategorySelected(null)
        }
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .onGloballyPositioned { headerHeightPx = it.size.height },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory ?: "ALL",
                fontSize = 15.sp,
                color = Color(0xFF6D6D6D)
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                painter = painterResource(if (isExpanded) R.drawable.arrow_up else R.drawable.arrow_down),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(12.dp)
            )
        }

        if (isExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, headerHeightPx),
                onDismissRequest = { isExpanded = false },
                properties = PopupProperties(dismissOnClickOutside = false)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .border(0.5.dp, Color(0xFFE5E5E5), RoundedCornerShape(0.dp))
                        .padding(all = 4.dp)
                ) {
                    categories.forEach { category ->
                        Text(
                            text = category,
                            fontSize = 15.sp,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier
                                .clickable {
                                    selectedCategory = category
                                    onCategorySelected(category)
                                    isExpanded = false
                                }
                                .padding(vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD9D9D9), RoundedCornerShape(50))
                            .clickable {
                                isExpanded = false
                                onManageCategoriesClick()
                            }
                            .padding(horizontal = 8.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (hasManageableCategories) "카테고리 수정" else "+ ADD",
                            fontSize = 14.sp,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                }
            }
        }
    }
}