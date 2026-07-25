package com.solux.luxup.taptap.feature.insight.lifestyle.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.util.InsightBackHeader
import com.solux.luxup.taptap.feature.insight.lifestyle.data.MockInsightLifestyle
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyle
import com.solux.luxup.taptap.feature.insight.lifestyle.util.InsightLifestyleForgottenSection
import com.solux.luxup.taptap.feature.insight.lifestyle.util.InsightLifestyleRecommendSection
import com.solux.luxup.taptap.feature.insight.lifestyle.util.InsightLifestyleSummaryCard

private val EmptyStateColor = Color(0xFFB0B0B0)

/**
 * 나의 라이프 스타일 — 먼슬리 레포트의 "나의 라이프 스타일" 배너에서 진입.
 * GET /api/insights/lifestyle 조회 결과를 요약 카드 + 버튼 추가/삭제 추천으로 보여준다.
 */
@Composable
fun InsightLifestyleScreen(
    data: InsightLifestyle,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAddRecommendation: (Long) -> Unit = {},
    onDeleteRecommendation: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        Spacer(Modifier.height(70.dp))

        InsightBackHeader(title = "나의 라이프 스타일", onBack = onBack)

        Spacer(Modifier.height(20.dp))

        if (!data.analysisAvailable) {
            LifestyleEmptyState()
            return@Column
        }

        val addRecommendations = data.recommendations.filter { it.recType == "ADD" }
        val deleteRecommendations = data.recommendations.filter { it.recType == "DELETE" }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InsightLifestyleSummaryCard(
                lifestyleLabel = data.lifestyleLabel,
                lifestyleCaption = data.lifestyleCaption,
                analysisButtons = data.analysisButtons
            )

            InsightLifestyleRecommendSection(
                recommendations = addRecommendations,
                onAddClick = onAddRecommendation
            )

            InsightLifestyleForgottenSection(
                recommendations = deleteRecommendations,
                onDeleteClick = onDeleteRecommendation
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LifestyleEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "아직 분석할 기록이 부족해요", fontSize = 14.sp, color = EmptyStateColor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun InsightLifestyleScreenPreview() {
    PreviewContainer {
        InsightLifestyleScreen(data = MockInsightLifestyle, onBack = {})
    }
}

@Preview(showBackground = true, name = "분석 불가", widthDp = 390, heightDp = 800)
@Composable
private fun InsightLifestyleScreenEmptyPreview() {
    PreviewContainer {
        InsightLifestyleScreen(
            data = MockInsightLifestyle.copy(analysisAvailable = false),
            onBack = {}
        )
    }
}