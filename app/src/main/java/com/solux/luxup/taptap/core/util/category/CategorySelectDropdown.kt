package com.solux.luxup.taptap.core.util.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.home.main.util.presentation.dropShadow

// "버튼 만들기" 화면의 카테고리 선택 드롭다운 - 선택 박스 + 카테고리 목록.
// 카테고리가 하나도 없으면 "+ ADD", 있으면 "카테고리 수정"을 보여주지만 둘 다 같은 관리 모달(onManageCategoriesClick)로 들어간다 -
// 카테고리 생성/수정/삭제는 항상 그 모달 안에서만 가능하다.
@Composable
fun CategorySelectDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onManageCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var headerWidthPx by remember { mutableIntStateOf(0) }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val hasManageableCategories = categories.any { it != "No Category" }

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .dropShadow(cornerRadius = 5.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
                .onGloballyPositioned {
                    headerWidthPx = it.size.width
                    headerHeightPx = it.size.height
                }
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Text(
                text = selectedCategory ?: "No Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
            Icon(
                painter = painterResource(if (isExpanded) R.drawable.arrow_up else R.drawable.arrow_down),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(20.dp)
            )
        }

        if (isExpanded) {
            val density = LocalDensity.current
            val gapPx = with(density) { 5.dp.roundToPx() }
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, headerHeightPx + gapPx),
                onDismissRequest = { isExpanded = false },
                properties = PopupProperties(dismissOnClickOutside = false)
            ) {
                Column(
                    modifier = Modifier
                        .width(with(density) { headerWidthPx.toDp() })
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(5.dp))
                        .padding(12.dp)
                ) {
                    categories.forEach { category ->
                        Text(
                            text = category,
                            fontSize = 18.sp,
                            color = Color(0xFF6D6D6D),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCategorySelected(category)
                                    isExpanded = false
                                }
                                .padding(vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFD9D9D9))
                            .clickable {
                                isExpanded = false
                                onManageCategoriesClick()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (hasManageableCategories) "카테고리 수정" else "+ ADD",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}