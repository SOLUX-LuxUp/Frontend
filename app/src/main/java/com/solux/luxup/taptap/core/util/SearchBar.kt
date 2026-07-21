package com.solux.luxup.taptap.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "검색",
    height: Dp = 35.dp,
    cornerRadius: Dp = 12.dp,
    horizontalMargin: Dp = 20.dp,
    fillWidth: Boolean = false,
    onQueryChange: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .padding(horizontal = horizontalMargin)
            .height(height)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier.width(260.dp))
            .figmaDropShadow(
                cornerRadius = cornerRadius,
                alpha = 0.15f,
                blurRadius = 7.dp
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFFB0B0B0),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(placeholder, fontSize = 13.sp, color = Color(0xFFB0B0B0))
                    }
                    innerTextField()
                }
            }
        )
    }
}