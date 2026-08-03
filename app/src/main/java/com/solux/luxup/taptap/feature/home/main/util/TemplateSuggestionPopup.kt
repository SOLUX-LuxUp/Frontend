package com.solux.luxup.taptap.feature.home.main.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.home.template.model.TemplateButtonSuggestion

/**
 * "빠르게 만들기" 팝업 — "버튼 만들기" 초기 화면(건너뛰기 상태)과 동일한 추천 목록 UI를 그대로 재사용한다.* 6개 행 정도만 보이고 그 이후는 스크롤로 훑어보게 높이를 제한한다.
 */
@Composable
fun TemplateSuggestionPopupCard(
    title: String,
    items: List<TemplateButtonSuggestion>,
    onDismiss: () -> Unit,
    onItemClick: (TemplateButtonSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_x),
                contentDescription = "닫기",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(23.dp)
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
        Spacer(Modifier.height(40.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 234.dp), // 6행(34dp) + 행 사이 간격(6dp) 기준
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(items) { suggestion ->
                SuggestionRow(suggestion = suggestion, onClick = { onItemClick(suggestion) })
            }
        }
    }
}