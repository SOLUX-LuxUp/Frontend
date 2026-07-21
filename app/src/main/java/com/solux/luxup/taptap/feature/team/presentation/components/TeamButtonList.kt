package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items          // ← 이게 빠졌었음
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton

@Composable
fun TeamButtonList(
    buttons: List<TeamButton>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(buttons) { button ->
            TeamButtonCard(button = button)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TeamButtonListPreview() {
    TeamButtonList(buttons = mockTeamButtons)
}