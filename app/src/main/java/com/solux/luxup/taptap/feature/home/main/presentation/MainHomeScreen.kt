package com.solux.luxup.taptap.feature.home.main.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("메인 홈 화면 (준비중)")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MainHomeScreenPreview() {
    MainHomeScreen()
}
