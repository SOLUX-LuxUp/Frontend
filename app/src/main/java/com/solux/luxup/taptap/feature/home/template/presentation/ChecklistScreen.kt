package com.solux.luxup.taptap.feature.home.template.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.home.template.data.memoryTemplate
import com.solux.luxup.taptap.feature.home.template.model.ChecklistTemplate
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun ChecklistScreen(
    template: ChecklistTemplate,
    categoryIndex: Int,
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val category = template.categories[categoryIndex]
    val checkedItems = remember(template.id, categoryIndex) {
        mutableStateListOf(*BooleanArray(category.items.size).toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseWhiteColor)
            .padding(horizontal = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color(0xFFB1B1B1),
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = template.optionTitle,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = category.title,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                category.items.forEachIndexed { index, item ->
                    ChecklistItemRow(
                        label = item,
                        checked = checkedItems[index],
                        onToggle = { checkedItems[index] = !checkedItems[index] }
                    )
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(BlueGradientStart, BlueGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("다음", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "건너뛰기",
                fontSize = 18.sp,
                color = Color(0xFFB1B1B1),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onSkip() }
            )
        }
    }
}

@Composable
private fun ChecklistItemRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6D6D6D)
        )
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (checked) BlueGradientEnd else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (checked) BlueGradientEnd else Color(0xFF2085FF),
                    shape = RoundedCornerShape(3.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ChecklistScreenPreview() {
    ChecklistScreen(template = memoryTemplate, categoryIndex = 0)
}
