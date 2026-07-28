package com.solux.luxup.taptap.feature.home.main.util

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton

// "즐겨찾기 수정" 팝업 - 즐겨찾기 목록에서 항목 제거
@Composable
fun FavoriteEditDialog(
    favorites: List<FavoriteButton>,
    onDismiss: () -> Unit,
    onSave: (List<FavoriteButton>) -> Unit,
    modifier: Modifier = Modifier
) {
    var favoriteList by remember(favorites) { mutableStateOf(favorites) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(27.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp)
    ) {
        Text(
            text = "즐겨찾기 수정",
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
                .padding(15.dp)
        ) {
            favoriteList.forEachIndexed { index, favorite ->
                if (index > 0) {
                    Spacer(Modifier.height(20.dp))
                }
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
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFFE2E2E2), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(favorite.iconRes),
                                contentDescription = null,
                                tint = favorite.iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(favorite.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFB1B1B1), CircleShape)
                            .clickable { favoriteList = favoriteList - favorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "즐겨찾기 삭제",
                            tint = Color(0xFFB1B1B1),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryDialogButton(
                text = "취소",
                textColor = Color(0xFFFF7B7B),
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            CategoryDialogButton(
                text = "저장",
                textColor = Color(0xFFACACAC),
                modifier = Modifier.weight(1f),
                onClick = { onSave(favoriteList) }
            )
        }
    }
}
