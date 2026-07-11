package com.solux.luxup.taptap.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

@Composable
fun CategoryDropdown(
    modifier: Modifier = Modifier,
    categories: List<String> = defaultCategories,
    onCategorySelected: (String?) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

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
                }
            }
        }
    }
}
