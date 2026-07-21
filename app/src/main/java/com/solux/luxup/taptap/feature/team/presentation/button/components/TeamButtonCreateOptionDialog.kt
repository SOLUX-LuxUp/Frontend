package com.solux.luxup.taptap.feature.team.presentation.button.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 팀 상세 우상단 + 를 눌렀을 때 뜨는 생성 방식 선택 모달.
 *
 * - 직접 만들기 → 팀 버튼 만들기 화면
 * - 빠르게 만들기 → 카테고리별 버튼 빠른 생성 모달 (구현 예정)
 *
 * 닫기는 바깥 탭 / 뒤로가기로 처리한다.
 */
@Composable
fun TeamButtonCreateOptionDialog(
    onDismiss: () -> Unit,
    onSelectManual: () -> Unit,
    onSelectQuick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        // 172dp 고정 폭을 쓰기 위해 플랫폼 기본 너비 해제
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamButtonCreateOptionContent(
            onSelectManual = onSelectManual,
            onSelectQuick = onSelectQuick,
        )
    }
}

/** Dialog는 preview가 안 돼서 내용만 분리 */
@Composable
fun TeamButtonCreateOptionContent(
    onSelectManual: () -> Unit,
    onSelectQuick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(172.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 21.dp, vertical = 19.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OptionButton(text = "직접 만들기", onClick = onSelectManual)
        OptionButton(text = "빠르게 만들기", onClick = onSelectQuick)
    }
}

@Composable
private fun OptionButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(41.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
        )
    }
}

@Preview(showBackground = true, widthDp = 260, heightDp = 210)@Composable
private fun TeamButtonCreateOptionContentPreview() {
    TeamButtonCreateOptionContent(
        onSelectManual = {},
        onSelectQuick = {},
        modifier = Modifier.padding(40.dp),
    )
}