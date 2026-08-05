package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton
import kotlin.math.roundToInt

/** 항목 행 높이(아이콘 40dp에 의해 결정) + 항목 사이 간격 */
private val FavoriteRowHeight = 40.dp
private val FavoriteRowSpacing = 20.dp

/** 5개까지만 보이고 그 아래는 스크롤되도록 하는 최대 목록 높이 */
private val FavoriteListMaxHeight = FavoriteRowHeight * 5 + FavoriteRowSpacing * 4

// "즐겨찾기 수정" 팝업 - 즐겨찾기 목록에서 항목 제거
@Composable
fun FavoriteEditDialog(
    favorites: List<FavoriteButton>,
    onDismiss: () -> Unit,
    onSave: (List<FavoriteButton>) -> Unit,
    modifier: Modifier = Modifier
) {
    var favoriteList by remember(favorites) { mutableStateOf(favorites) }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var rowHeightPx by remember { mutableIntStateOf(0) }
    val spacingPx = with(LocalDensity.current) { 20.dp.roundToPx() }

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
                .heightIn(max = FavoriteListMaxHeight)
                .verticalScroll(rememberScrollState())
        ) {
            favoriteList.forEachIndexed { index, favorite ->
                if (index > 0) {
                    Spacer(Modifier.height(FavoriteRowSpacing))
                }
                key(favorite.buttonId) {
                val isDragging = draggingIndex == index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { if (rowHeightPx == 0) rowHeightPx = it.size.height }
                        .graphicsLayer { translationY = if (isDragging) dragOffsetY else 0f }
                        .zIndex(if (isDragging) 1f else 0f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "순서 변경",
                            tint = Color(0xFFB1B1B1),
                            modifier = Modifier
                                .size(20.dp)
                                .pointerInput(favorite.buttonId) {
                                    detectDragGestures(
                                        onDragStart = {
                                            draggingIndex = index
                                            dragOffsetY = 0f
                                        },
                                        onDragEnd = {
                                            draggingIndex = null
                                            dragOffsetY = 0f
                                        },
                                        onDragCancel = {
                                            draggingIndex = null
                                            dragOffsetY = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetY += dragAmount.y

                                            val from = draggingIndex ?: return@detectDragGestures
                                            val step = rowHeightPx + spacingPx
                                            if (step <= 0) return@detectDragGestures

                                            val moveBy = (dragOffsetY / step).roundToInt()
                                            if (moveBy == 0) return@detectDragGestures

                                            val to = (from + moveBy).coerceIn(0, favoriteList.lastIndex)
                                            if (to != from) {
                                                favoriteList = favoriteList.toMutableList().apply {
                                                    add(to, removeAt(from))
                                                }
                                                draggingIndex = to
                                                dragOffsetY -= moveBy * step
                                            }
                                        }
                                    )
                                }
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
