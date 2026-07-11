package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.home.main.model.RecommendedButton
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue

@Composable
fun TemplateSuggestionPopupCard(
    title: String,
    items: List<RecommendedButton>,
    onDismiss: () -> Unit,
    onItemClick: (RecommendedButton) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color(0xFF6D6D6D),
                modifier = Modifier
                    .size(25.dp)
                    .clickable { onDismiss() }
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                RecommendedButtonRow(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun RecommendedButtonRow(item: RecommendedButton, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BrandWhiteBlue)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(item.tag, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        }
    }
}
