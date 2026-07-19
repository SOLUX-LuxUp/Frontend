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
import com.solux.luxup.taptap.feature.home.template.data.onboardingTemplates
import com.solux.luxup.taptap.feature.home.template.model.ChecklistTemplate
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue

@Composable
fun OnboardingTemplateScreen(
    onSkip: () -> Unit = {}
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
            onboardingTemplates.forEach { template ->
                OnboardingOptionCard(template = template)
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
                modifier = Modifier.clickable { onSkip() }
            )
        }
    }
}

@Composable
private fun OnboardingOptionCard(
    template: ChecklistTemplate
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BrandWhiteBlue)
            .padding(20.dp)
    ) {
        Text(
            text = template.optionTitle,
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
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = template.categories.joinToString(" / ") { it.title },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6D6D6D)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OnboardingTemplateScreenPreview() {
    OnboardingTemplateScreen()
}