package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd

@Composable
fun FavoriteAddBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(105.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFDEF0FF))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "즐겨찾기 추가",
            tint = BlueGradientEnd,
            modifier = Modifier.size(40.dp)
        )
    }
}
