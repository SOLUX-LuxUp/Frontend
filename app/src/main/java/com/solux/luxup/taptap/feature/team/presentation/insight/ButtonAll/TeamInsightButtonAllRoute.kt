package com.solux.luxup.taptap.feature.team.presentation.insight.buttonall

object TeamInsightButtonAllRoute {
    const val ARG_TEAM_ID = "teamId"
    const val ARG_PERIOD = "period"

    const val ROUTE = "team_insight_button_all/{$ARG_TEAM_ID}/{$ARG_PERIOD}"

    fun route(teamId: Long, period: String) = "team_insight_button_all/$teamId/$period"
}