package com.solux.luxup.taptap.feature.home.template.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.feature.home.template.data.mockOnboardingTemplates
import com.solux.luxup.taptap.feature.home.template.model.OnboardingTemplate
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue

/**
 * 온보딩 템플릿 선택 화면 진입점. ViewModel을 붙인다.
 *
 * 카드 선택 → API 호출 없이 templateId를 다음 화면(메인 홈)으로 전달.
 * 건너뛰기 → POST /api/templates/skip
 */
@Composable
fun OnboardingTemplateRoute(
    onTemplateSelected: (Long) -> Unit,
    onSkip: () -> Unit,
) {
    val viewModel: OnboardingTemplateViewModel = hiltViewModel()

    OnboardingTemplateScreen(
        templates = viewModel.templates,
        onTemplateClick = { template -> onTemplateSelected(template.templateId) },
        onSkipClick = { viewModel.skipTemplate(onSkip) },
        errorMessage = viewModel.errorMessage,
        onErrorConsumed = viewModel::consumeError,
    )
}

@Composable
fun OnboardingTemplateScreen(
    templates: List<OnboardingTemplate>,
    onTemplateClick: (OnboardingTemplate) -> Unit = {},
    onSkipClick: () -> Unit = {},
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseWhiteColor)
            .padding(horizontal = 40.dp)
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = "어떤 일상을\n기록하고싶나요?",
            style = TextStyle(
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                brush = Brush.linearGradient(
                    colors = listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
        )

        Spacer(modifier = Modifier.height(60.dp))

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            templates.forEach { template ->
                OnboardingOptionCard(
                    template = template,
                    onClick = { onTemplateClick(template) },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "건너뛰기",
                fontSize = 18.sp,
                color = Color(0xFFB1B1B1),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onSkipClick() }
            )
        }
    }

    errorMessage?.let { message ->
        NoticeDialog(
            message = message,
            onDismiss = onErrorConsumed,
        )
    }
}

/**
 * 카드 하단의 태그 목록("집 / 생활 / 안전 / 건강")은 서버 응답(GET /api/templates)에 없는 값이라
 * templateType 기준으로 화면에서 직접 채운다.
 */
private val templateCategoryTags: Map<String, List<String>> = mapOf(
    "memory" to listOf("집", "생활", "안전", "건강"),
    "self_care" to listOf("몸 관리", "마음 관리", "생활 리듬"),
    "productivity" to listOf("정돈", "학습", "탐색", "성장"),
)

@Composable
private fun OnboardingOptionCard(
    template: OnboardingTemplate,
    onClick: () -> Unit,
) {
    val categoryTags = templateCategoryTags[template.templateType].orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BrandWhiteBlue)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Text(
            text = template.templateName.substringBefore('(').trim(),
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = template.description,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6D6D6D)
        )
        if (categoryTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = categoryTags.joinToString(" / "),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D6D6D)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OnboardingTemplateScreenPreview() {
    OnboardingTemplateScreen(templates = mockOnboardingTemplates)
}